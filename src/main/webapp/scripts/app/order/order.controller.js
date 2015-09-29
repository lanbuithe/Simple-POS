'use strict';

angular.module('posApp')
  .controller('OrderController', ['$scope', '$filter', '$window', 'toaster', 'Principal', 'ParseLinks', 
    'TableNo', 'ItemCategory', 'Item', 'ItemService', 'Constants', 'Utils', 'OrderService', 
    function($scope, $filter, $window, toaster, Principal, ParseLinks, 
    TableNo, ItemCategory, Item, ItemService, Constants, Utils, OrderService) {

    var orderCtrl = this;
	
    orderCtrl.tables = TableNo.query();
    orderCtrl.itemCategories = [];
    orderCtrl.items = [];
    orderCtrl.endCategoryIndex = Constants.perCategory - 1;
    orderCtrl.endItemIndex = Constants.perItem - 1;
    orderCtrl.order = {};
    // for hold order
    orderCtrl.holdOrders = [];
    orderCtrl.pageHoldOrder = 1;
    orderCtrl.linkHoldOrders = [];
    // for move item from order
    orderCtrl.moveItemFromTable = {};
    orderCtrl.moveItemFromOrders = [];
    orderCtrl.pageMoveItemFromOrder = 1;
    orderCtrl.linkMoveItemFromOrders = [];
    orderCtrl.moveItemFromOrder = {};
    // for move item to order
    orderCtrl.moveItemToTable = {};
    orderCtrl.moveItemToOrders = [];
    orderCtrl.pageMoveItemToOrder = 1;
    orderCtrl.linkMoveItemToOrders = [];
    orderCtrl.moveItemToOrder = {};

    orderCtrl.filterMoveItemToTable = function(moveItemToTable) {
      return !Utils.isUndefinedOrNull(moveItemToTable) && 
          !Utils.isUndefinedOrNull(orderCtrl.moveItemFromTable) && 
          moveItemToTable.id !== orderCtrl.moveItemFromTable.id; 
    };

    function checkMoveItemOrder(moveItemOrder) {
      if (!Utils.isUndefinedOrNull(moveItemOrder) && !Utils.isUndefinedOrNull(orderCtrl.order) && 
        orderCtrl.order.id === moveItemOrder.id) {
        if (Utils.isUndefinedOrNull(moveItemOrder.details) || 
          moveItemOrder.details.length === 0) {
          init();
        } else {
          orderCtrl.order = angular.copy(moveItemOrder);
          sumOrderAmount(orderCtrl.order);
        }
      }
    };

    orderCtrl.acceptMoveItem = function() {
      orderCtrl.moveItemToOrder.tableNo = orderCtrl.moveItemToTable;
      var moveItemOrders = [orderCtrl.moveItemFromOrder, orderCtrl.moveItemToOrder];
      OrderService.moveItem(moveItemOrders).then(function(response) {
        if (response.data) {
          checkMoveItemOrder(orderCtrl.moveItemFromOrder);
          checkMoveItemOrder(orderCtrl.moveItemToOrder);                   
          toaster.pop('success', $filter('translate')('order.messages.info.order.move.item.successfully'));
          $('#moveItemModal').modal('hide');
        } else {
          toaster.pop('warning', $filter('translate')('order.messages.info.order.move.item.failed'));
        }
      });            
    };

    orderCtrl.moveItem = function() {
      if (Utils.isUndefinedOrNull(orderCtrl.moveItemToTable.id)) {
        toaster.pop('warning', $filter('translate')('order.messages.info.order.table.select'));
        return;
      }
      var fromOrderDetails = _.where(orderCtrl.moveItemFromOrder.details, {selected: true});
      var toOrderDetail = null;
      if (!Utils.isUndefinedOrNull(fromOrderDetails) && fromOrderDetails.length > 0) {
          angular.forEach(fromOrderDetails, function(fromOrderDetail) {
            toOrderDetail = _.findWhere(orderCtrl.toMoveItemOrder, {itemId: fromOrderDetail.itemId});
            if (Utils.isUndefinedOrNull(toOrderDetail)) {
              toOrderDetail = angular.copy(fromOrderDetail);
              toOrderDetail.quantity = angular.copy(toOrderDetail.tmpQuantity);
              orderCtrl.moveItemToOrder.details.push(toOrderDetail);
            } else {
              toOrderDetail.quantity = toOrderDetail.quantity + toOrderDetail.tmpQuantity;
            }
            toOrderDetail.amount = toOrderDetail.quantity * toOrderDetail.item.price;
            fromOrderDetail.quantity = fromOrderDetail.quantity - fromOrderDetail.tmpQuantity;
            fromOrderDetail.tmpQuantity = angular.copy(fromOrderDetail.quantity);
            fromOrderDetail.amount = fromOrderDetail.quantity * fromOrderDetail.item.price;
            if (fromOrderDetail.quantity <= 0) {
              orderCtrl.moveItemFromOrder.details = _.without(orderCtrl.moveItemFromOrder.details, _.findWhere(orderCtrl.moveItemFromOrder.details, {quantity: fromOrderDetail.quantity})); 
            }
            sumOrderAmount(orderCtrl.moveItemFromOrder);
            sumOrderAmount(orderCtrl.moveItemToOrder);
          });
      } else {
        toaster.pop('warning', $filter('translate')('order.messages.info.order.item.select'));
      }
    };

    orderCtrl.changeMoveItemToTable = function() {
      orderCtrl.moveItemToOrders.length = 0;
      orderCtrl.pageMoveItemToOrder = 1;
      orderCtrl.linkMoveItemToOrders = {};
      orderCtrl.moveItemToOrder.details = [];
      var tableId = null;
      if (!Utils.isUndefinedOrNull(orderCtrl.moveItemToTable) && 
          !Utils.isUndefinedOrNull(orderCtrl.moveItemToTable.id)) {
        tableId = orderCtrl.moveItemToTable.id;
      }
      OrderService.getByTableIdStatusCreatedDate(orderCtrl.pageMoveItemToOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
        function(response) {
          if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
            orderCtrl.linkMoveItemToOrders = ParseLinks.parse(response.headers('link'));
            orderCtrl.moveItemToOrders = response.data;
            if (orderCtrl.moveItemToOrders.length > 1) {
              $('#moveItemToOrderModal').modal('show');    
            } else {
              orderCtrl.moveItemToOrder = orderCtrl.moveItemToOrders[0];
              copyItemIdOrderDetail(orderCtrl.moveItemToOrder);
            }                        
          }
      });
    };

    orderCtrl.selectMoveItemToOrder = function(moveItemToOrder) {
      orderCtrl.moveItemToOrder = moveItemToOrder;
      copyItemIdOrderDetail(orderCtrl.moveItemToOrder);
      $('#moveItemToOrderModal').modal('hide');
    };

    function copyItemIdOrderDetail(order) {
      if (!Utils.isUndefinedOrNull(order) && !Utils.isUndefinedOrNull(order.details) && 
        order.details.length > 0) {
        angular.forEach(order.details, function(orderDetail) {
            orderDetail.itemId = angular.copy(orderDetail.item.id);
        });
      }            
    };

    orderCtrl.loadPageMoveItemToOrder = function(page) {
      orderCtrl.moveItemToOrders.length = 0;
      orderCtrl.pageMoveItemToOrder = page;
      orderCtrl.linkMoveItemToOrders = {};
      var tableId = null;
      if (!Utils.isUndefinedOrNull(orderCtrl.moveItemToTable) && 
        !Utils.isUndefinedOrNull(orderCtrl.moveItemToTable.id)) {
        tableId = orderCtrl.moveItemToTable.id;
      }           
      OrderService.getByTableIdStatusCreatedDate(orderCtrl.pageMoveItemToOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
        function(response) {
          if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
            orderCtrl.linkMoveItemToOrders = ParseLinks.parse(response.headers('link'));
            orderCtrl.moveItemToOrders = response.data;
          } else {
            toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
          }
      });
    };

    function copyQuantityOrderDetail(order) {
      if (!Utils.isUndefinedOrNull(order) && !Utils.isUndefinedOrNull(order.details) && 
        order.details.length > 0) {
        angular.forEach(order.details, function(orderDetail) {
          orderDetail.tmpQuantity = angular.copy(orderDetail.quantity);
          orderDetail.itemId = angular.copy(orderDetail.item.id);
        });
      }
    }; 

    orderCtrl.changeMoveItemFromTable = function() {
      orderCtrl.moveItemFromOrders.length = 0;
      orderCtrl.pageMoveItemFromOrder = 1;
      orderCtrl.linkMoveItemFromOrders = {};
      var tableId = null;
      if (!Utils.isUndefinedOrNull(orderCtrl.moveItemFromTable) && 
        !Utils.isUndefinedOrNull(orderCtrl.moveItemFromTable.id)) {
        tableId = orderCtrl.moveItemFromTable.id;
      }
      OrderService.getByTableIdStatusCreatedDate(orderCtrl.pageMoveItemFromOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
        function(response) {
          if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
            orderCtrl.linkMoveItemFromOrders = ParseLinks.parse(response.headers('link'));
            orderCtrl.moveItemFromOrders = response.data;
          } else {
            toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
          }
      });
    };

    orderCtrl.selectMoveItemFromOrder = function(moveItemFromOrder) {
      orderCtrl.moveItemFromOrder = moveItemFromOrder;
      orderCtrl.moveItemFromTable = moveItemFromOrder.tableNo;
      copyQuantityOrderDetail(orderCtrl.moveItemFromOrder);
      $('#moveItemFromOrderModal').modal('hide');
      $('#moveItemModal').modal('show');
    };

    orderCtrl.loadPageMoveItemFromOrder = function(page) {
      orderCtrl.moveItemFromOrders.length = 0;
      orderCtrl.pageMoveItemFromOrder = page;
      orderCtrl.linkMoveItemFromOrders = {};
      var tableId = null;
      if (!Utils.isUndefinedOrNull(orderCtrl.moveItemFromTable) && 
          !Utils.isUndefinedOrNull(orderCtrl.moveItemFromTable.id)) {
        tableId = orderCtrl.moveItemFromTable.id;
      }           
      OrderService.getByTableIdStatusCreatedDate(orderCtrl.pageMoveItemFromOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
        function(response) {
          if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
            orderCtrl.linkMoveItemFromOrders = ParseLinks.parse(response.headers('link'));
            orderCtrl.moveItemFromOrders = response.data;
          } else {
            toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
          }
      });
    };

    orderCtrl.openMoveItem = function() {
      orderCtrl.pageMoveItemOrder = 1;
      orderCtrl.moveItemFromTable = angular.copy(orderCtrl.order.tableNo);
      orderCtrl.linkMoveItemFromOrders = {};
      orderCtrl.moveItemFromOrders.length = 0;
      orderCtrl.moveItemToTable = {};
      orderCtrl.moveItemToOrder = {};            
      var tableId = null;
      if (!Utils.isUndefinedOrNull(orderCtrl.moveItemFromTable) && 
        !Utils.isUndefinedOrNull(orderCtrl.moveItemFromTable.id)) {
        tableId = orderCtrl.moveItemFromTable.id;
      }
      OrderService.getByTableIdStatusCreatedDate(orderCtrl.pageHoldOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
        function(response) {
          if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
            orderCtrl.linkMoveItemFromOrders = ParseLinks.parse(response.headers('link'));
            orderCtrl.moveItemFromOrders = response.data;
            if (orderCtrl.moveItemFromOrders.length > 1) {
              $('#moveItemFromOrderModal').modal('show');    
            } else {
              orderCtrl.moveItemFromOrder = orderCtrl.moveItemFromOrders[0];
              orderCtrl.moveItemFromTable = orderCtrl.moveItemFromOrders[0].tableNo;
              copyQuantityOrderDetail(orderCtrl.moveItemFromOrder);
              $('#moveItemModal').modal('show');
            }
          } else {
            toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
          }
      });
    };        

    orderCtrl.selectHoldOrder = function(holdOrder) {
      orderCtrl.order = holdOrder;
      sumOrderAmount(orderCtrl.order);
      $('#holdOrderModal').modal('hide');
    };

    orderCtrl.loadPageHoldOrder = function(page) {
      orderCtrl.holdOrders.length = 0;
      orderCtrl.pageHoldOrder = page;
      orderCtrl.linkHoldOrders = {};
      OrderService.getByTableIdStatusCreatedDate(orderCtrl.pageHoldOrder, 6, orderCtrl.order.tableNo.id, Constants.orderStatus.hold, null, null).then(
        function(response) {
          if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
            orderCtrl.linkHoldOrders = ParseLinks.parse(response.headers('link'));
            orderCtrl.holdOrders = response.data;
          } else {
            toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
          }
      });
    };        

    orderCtrl.openHoldOrder = function() {
      var tableId = null;
      if (!Utils.isUndefinedOrNull(orderCtrl.order) 
          && !Utils.isUndefinedOrNull(orderCtrl.order.tableNo)) {
        tableId = orderCtrl.order.tableNo.id;
      }
      orderCtrl.holdOrders.length = 0;
      orderCtrl.pageHoldOrder = 1;
      orderCtrl.linkHoldOrders = {};
    OrderService.getByTableIdStatusCreatedDate(orderCtrl.pageHoldOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
        function(response) {
          if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
            orderCtrl.linkHoldOrders = ParseLinks.parse(response.headers('link'));
            orderCtrl.holdOrders = response.data;
            $('#holdOrderModal').modal('show');
          } else {
            toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
          }
      });
    };

    orderCtrl.nextItem = function() {
      orderCtrl.endItemIndex += Constants.perItem;
    };

    orderCtrl.prevItem = function() {
      orderCtrl.endItemIndex -= Constants.perItem;
    };        

    orderCtrl.nextCategory = function() {
      orderCtrl.endCategoryIndex += Constants.perCategory;
    };

    orderCtrl.prevCategory = function() {
      orderCtrl.endCategoryIndex -= Constants.perCategory;
    };

    orderCtrl.cancelOrder = function() {
      var message = $filter('translate')('order.messages.info.order.cancel');
    	if (Utils.isUndefinedOrNull(orderCtrl.order.id)) {
        toaster.pop('success', message);
    		init();
    	} else {
    		orderCtrl.order.status = Constants.orderStatus.cancel;
    		OrderService.updateOrder(orderCtrl.order).then(function(response) {
          toaster.pop('success', message);                        
				  init();
        });        		
    	} 
    };
    
    orderCtrl.holdOrder = function() {
      var message = $filter('translate')('order.messages.info.order.hold');
      handleOrder(message, Constants.orderStatus.hold);
    };

    orderCtrl.payOrder = function() {
      var message = $filter('translate')('order.messages.info.order.pay');
      handleOrder(message, Constants.orderStatus.payment);
    };

    function handleOrder(message, status) {
      orderCtrl.order.status = status;
      if (Utils.isUndefinedOrNull(orderCtrl.order.id)) {
        OrderService.createOrder(orderCtrl.order).then(function(response) {
          var orderId = null;
          if (!Utils.isUndefinedOrNull(response) && !Utils.isUndefinedOrNull(response.data)) {
              orderId = response.data.id;
          }
          processOrder(message, status, orderId);
        });
      } else {
        OrderService.updateOrder(orderCtrl.order).then(function(response) {
            processOrder(message, status, orderCtrl.order.id);
        });             
      }            
    };        

    function processOrder(message, status, orderId) {
      toaster.pop('success', message);                        
      init();
      if (Constants.orderStatus.payment === status) {
        var url = $window.location.origin;
        $window.open(url + '/blank#/print/' + orderId);            
      }
    }      
    
    function sumOrderAmount(order) {
    	order.amount = 0;
      order.quantity = 0;
      order.discountAmount = 0;
      order.taxAmount = 0;
      order.receivableAmount = 0;
    	angular.forEach(order.details, function(orderDetail) {
    	order.amount += orderDetail.amount;
        order.quantity += orderDetail.quantity;
        // for function addOrderDetail
        orderDetail.itemId = orderDetail.item.id;
    	});
      order.discountAmount = order.discount * order.amount;
      order.taxAmount = order.tax * order.amount;
    	order.receivableAmount = order.amount - order.discountAmount + order.taxAmount;
    };
    
    orderCtrl.quantityBlur = function(index) {
    	var orderDetail = orderCtrl.order.details[index];
        if (!Utils.isUndefinedOrNull(orderDetail.quantity) && angular.isNumber(orderDetail.quantity)) {
          orderDetail.amount = orderDetail.item.price * orderDetail.quantity;
          sumOrderAmount(orderCtrl.order);
        }
    };
    
    orderCtrl.removeOrderDetail = function(index) {
    	orderCtrl.order.details.splice(index, 1);
    	sumOrderAmount(orderCtrl.order);
    };        
    
    orderCtrl.addOrderDetail = function(selectedItem) {
    	var orderDetail = _.findWhere(orderCtrl.order.details, {itemId: selectedItem.id});
    	if (Utils.isUndefinedOrNull(orderDetail)) {
    		orderDetail = {
      		item: selectedItem,
      		quantity: 1,
      		amount: 1 * selectedItem.price,
      		itemId: selectedItem.id
      	};
        orderCtrl.order.details.push(angular.copy(orderDetail));        		
    	} else {
    		orderDetail.quantity += 1;
    		orderDetail.amount = orderDetail.quantity * selectedItem.price;
    	}
    	sumOrderAmount(orderCtrl.order)
    };
    
    orderCtrl.getItemByCategoryId = function(itemCategoryId) {
      if (Utils.isUndefinedOrNull(itemCategoryId)) {
        Item.query(function(result, headers) {
          orderCtrl.items = result;
        });
      } else {
        ItemService.getByCategory(itemCategoryId).then(function(data) {
          orderCtrl.items = data;
        });
      }
    };
    
    function init() {
      orderCtrl.order = {
        quantity: 0,
      	amount: 0,
        discount: 0,
        tax: 0,
        discountAmount: 0,
        taxAmount: 0,
        receivableAmount: 0,
      	details: []
      };       	
    };

    orderCtrl.loadAll = function() {
    	init();
      Principal.identity().then(function(account) {
        orderCtrl.account = account;
        orderCtrl.isAuthenticated = Principal.isAuthenticated;
      });
      // load all item category
      ItemCategory.query(function(result, headers) {
        orderCtrl.itemCategories = result;
        if (!Utils.isUndefinedOrNull(orderCtrl.itemCategories) && orderCtrl.itemCategories.length > 0) {
          var itemCategory = {
              name: $filter('translate')('common.all')
          };
          orderCtrl.itemCategories.unshift(itemCategory);
        }
      });
      // load all item
      Item.query(function(result, headers) {
        orderCtrl.items = result;
      });            
    };

    orderCtrl.loadAll();        

  }]);
