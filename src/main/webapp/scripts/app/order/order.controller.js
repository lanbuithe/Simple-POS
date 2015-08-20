'use strict';

angular.module('posApp')
    .controller('OrderController', function ($scope, $filter, $window, Principal, toaster, TableNo, 
        ItemCategory, Item, ItemService, Constants, Utils, OrderService, ParseLinks) {
    	
    	$scope.tables = TableNo.query();
        $scope.itemCategories = [];
        $scope.items = [];
        $scope.endCategoryIndex = Constants.perCategory - 1;
        $scope.endItemIndex = Constants.perItem - 1;
        $scope.order = {};
        // for hold order
        $scope.holdOrders = [];
        $scope.pageHoldOrder = 1;
        $scope.linkHoldOrders = [];
        // for move item from order
        $scope.moveItemFromTable = {};
        $scope.moveItemFromOrders = [];
        $scope.pageMoveItemFromOrder = 1;
        $scope.linkMoveItemFromOrders = [];
        $scope.moveItemFromOrder = {};
        // for move item to order
        $scope.moveItemToTable = {};
        $scope.moveItemToOrders = [];
        $scope.pageMoveItemToOrder = 1;
        $scope.linkMoveItemToOrders = [];
        $scope.moveItemToOrder = {};

        $scope.moveItem = function() {

        };

        $scope.changeMoveItemToTable = function() {
            $scope.pageMoveItemToOrder = 1;
            var tableId = null;
            if (!Utils.isUndefinedOrNull($scope.moveItemToTable) && 
                !Utils.isUndefinedOrNull($scope.moveItemToTable.id)) {
                tableId = $scope.moveItemToTable.id;
            }
            OrderService.getByTableIdStatusCreatedDate($scope.pageMoveItemToOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
                        $scope.linkMoveItemToOrders = ParseLinks.parse(response.headers('link'));
                        $scope.moveItemToOrders = response.data;
                        if ($scope.moveItemToOrders.length > 1) {
                            $('#moveItemToOrderModal').modal('show');    
                        } else {
                            $scope.moveItemToOrder = $scope.moveItemToOrders[0];
                        }                        
                    } else {
                        toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
                    }
            });
        };

        $scope.selectMoveItemToOrder = function(moveItemToOrder) {
            $scope.moveItemToOrder = moveItemToOrder;
            $('#moveItemToOrderModal').modal('hide');
        };

        $scope.loadPageMoveItemToOrder = function(page) {
            $scope.moveItemToOrders.length = 0;
            $scope.pageMoveItemToOrder = page;
            var tableId = null;
            if (!Utils.isUndefinedOrNull($scope.moveItemToTable) && 
                !Utils.isUndefinedOrNull($scope.moveItemToTable.id)) {
                tableId = $scope.moveItemToTable.id;
            }           
            OrderService.getByTableIdStatusCreatedDate($scope.pageMoveItemToOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
                        $scope.linkMoveItemToOrders = ParseLinks.parse(response.headers('link'));
                        $scope.moveItemToOrders = response.data;
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
                });
            }
        }; 

        $scope.changeMoveItemFromTable = function() {
            $scope.pageMoveItemFromOrder = 1;
            var tableId = null;
            if (!Utils.isUndefinedOrNull($scope.moveItemFromTable) && 
                !Utils.isUndefinedOrNull($scope.moveItemFromTable.id)) {
                tableId = $scope.moveItemFromTable.id;
            }
            OrderService.getByTableIdStatusCreatedDate($scope.pageMoveItemFromOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
                        $scope.linkMoveItemFromOrders = ParseLinks.parse(response.headers('link'));
                        $scope.moveItemFromOrders = response.data;
                    } else {
                        toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
                    }
            });
        };

        $scope.selectMoveItemFromOrder = function(moveItemFromOrder) {
            $scope.moveItemFromOrder = moveItemFromOrder;
            copyQuantityOrderDetail($scope.moveItemFromOrder);
            $('#moveItemFromOrderModal').modal('hide');
            $('#moveItemModal').modal('show');
        };

        $scope.loadPageMoveItemFromOrder = function(page) {
            $scope.moveItemFromOrders.length = 0;
            $scope.pageMoveItemFromOrder = page;
            var tableId = null;
            if (!Utils.isUndefinedOrNull($scope.moveItemFromTable) && 
                !Utils.isUndefinedOrNull($scope.moveItemFromTable.id)) {
                tableId = $scope.moveItemFromTable.id;
            }           
            OrderService.getByTableIdStatusCreatedDate($scope.pageMoveItemFromOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
                        $scope.linkMoveItemFromOrders = ParseLinks.parse(response.headers('link'));
                        $scope.moveItemFromOrders = response.data;
                    } else {
                        toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
                    }
            });
        };

        $scope.openMoveItem = function() {
            $scope.pageMoveItemOrder = 1;
            $scope.moveItemFromTable = $scope.order.tableNo;
            var tableId = null;
            if (!Utils.isUndefinedOrNull($scope.moveItemFromTable) && 
                !Utils.isUndefinedOrNull($scope.moveItemFromTable.id)) {
                tableId = $scope.moveItemFromTable.id;
            }
            OrderService.getByTableIdStatusCreatedDate($scope.pageHoldOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
                        $scope.linkMoveItemFromOrders = ParseLinks.parse(response.headers('link'));
                        $scope.moveItemFromOrders = response.data;
                        $scope.moveItemToTable = {};
                        if ($scope.moveItemFromOrders.length > 1) {
                            $('#moveItemFromOrderModal').modal('show');    
                        } else {
                            $scope.moveItemFromOrder = $scope.moveItemFromOrders[0];
                            copyQuantityOrderDetail($scope.moveItemFromOrder);
                            $('#moveItemModal').modal('show');
                        }
                    } else {
                        toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
                    }
            });
        };        

        $scope.selectHoldOrder = function(holdOrder) {
            $scope.order = holdOrder;
            sumOrderAmount();
            $('#holdOrderModal').modal('hide');
        };

        $scope.loadPageHoldOrder = function(page) {
            $scope.holdOrders.length = 0;
            $scope.pageHoldOrder = page;
            OrderService.getByTableIdStatusCreatedDate($scope.pageHoldOrder, 6, $scope.order.tableNo.id, Constants.orderStatus.hold, null, null).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
                        $scope.linkHoldOrders = ParseLinks.parse(response.headers('link'));
                        $scope.holdOrders = response.data;
                    } else {
                        toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
                    }
            });
        };        

        $scope.openHoldOrder = function() {
            var tableId = null;
            if (!Utils.isUndefinedOrNull($scope.order) 
                && !Utils.isUndefinedOrNull($scope.order.tableNo)) {
                tableId = $scope.order.tableNo.id;
            }
            $scope.holdOrders.length = 0;
            $scope.pageHoldOrder = 1;
            OrderService.getByTableIdStatusCreatedDate($scope.pageHoldOrder, 6, tableId, Constants.orderStatus.hold, null, null).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data) && response.data.length > 0) {
                        $scope.linkHoldOrders = ParseLinks.parse(response.headers('link'));
                        $scope.holdOrders = response.data;
                        $('#holdOrderModal').modal('show');
                    } else {
                        toaster.pop('warning', $filter('translate')('common.messages.info.data.notFound'));
                    }
            });
        };

        $scope.nextItem = function() {
            $scope.endItemIndex += Constants.perItem;
        };

        $scope.prevItem = function() {
            $scope.endItemIndex -= Constants.perItem;
        };        

        $scope.nextCategory = function() {
            $scope.endCategoryIndex += Constants.perCategory;
        };

        $scope.prevCategory = function() {
            $scope.endCategoryIndex -= Constants.perCategory;
        };

        $scope.cancelOrder = function() {
            var message = $filter('translate')('order.messages.info.order.cancel');
        	if (Utils.isUndefinedOrNull($scope.order.id)) {
                toaster.pop('success', message);
        		init();
        	} else {
        		$scope.order.status = Constants.orderStatus.cancel;
        		OrderService.updateOrder($scope.order).then(function(response) {
                    toaster.pop('success', message);                        
    				init();
                });        		
        	} 
        };
        
        $scope.holdOrder = function() {
            var message = $filter('translate')('order.messages.info.order.hold');
            handleOrder(message, Constants.orderStatus.hold);
        };

        $scope.payOrder = function() {
            var message = $filter('translate')('order.messages.info.order.pay');
            handleOrder(message, Constants.orderStatus.payment);
        };

        function handleOrder(message, status) {
            $scope.order.status = status;
            if (Utils.isUndefinedOrNull($scope.order.id)) {
                OrderService.createOrder($scope.order).then(function(response) {
                    var orderId = null;
                    if (!Utils.isUndefinedOrNull(response) && !Utils.isUndefinedOrNull(response.data)) {
                        orderId = response.data.id;
                    }
                    processOrder(message, status, orderId);
                });
            } else {
                OrderService.updateOrder($scope.order).then(function(response) {
                    processOrder(message, status, $scope.order.id);
                });             
                
            }            
        }        

        function processOrder(message, status, orderId) {
            toaster.pop('success', message);                        
            init();
            if (Constants.orderStatus.payment === status) {
                var url = $window.location.origin;
                $window.open(url + '/blank#/print/' + orderId);            
            }
        }      
        
        function sumOrderAmount() {
        	$scope.order.amount = 0;
            $scope.order.quantity = 0;
            $scope.total.payable = 0;
        	angular.forEach($scope.order.details, function(orderDetail) {
        		$scope.order.amount += orderDetail.amount;
                $scope.order.quantity += orderDetail.quantity;
                // for function addOrderDetail
                orderDetail.itemId = orderDetail.item.id;
        	});
        	$scope.total.payable = $scope.order.amount - $scope.total.discount + $scope.total.tax;
        };
        
        $scope.quantityBlur = function(index) {
        	var orderDetail = $scope.order.details[index];
            if (!Utils.isUndefinedOrNull(orderDetail.quantity) && angular.isNumber(orderDetail.quantity)) {
                orderDetail.amount = orderDetail.item.price * orderDetail.quantity;
                sumOrderAmount();
            }
        };
        
        $scope.removeOrderDetail = function(index) {
        	$scope.order.details.splice(index, 1);
        	sumOrderAmount();
        };        
        
        $scope.addOrderDetail = function(selectedItem) {
        	var orderDetail = _.findWhere($scope.order.details, {itemId: selectedItem.id});
        	if (Utils.isUndefinedOrNull(orderDetail)) {
        		orderDetail = {
            		item: selectedItem,
            		quantity: 1,
            		amount: 1 * selectedItem.price,
            		itemId: selectedItem.id
            	};
                $scope.order.details.push(angular.copy(orderDetail));        		
        	} else {
        		orderDetail.quantity += 1;
        		orderDetail.amount = orderDetail.quantity * selectedItem.price;
        	}
        	sumOrderAmount()
        };
        
        $scope.getItemByCategoryId = function(itemCategoryId) {
            if (Utils.isUndefinedOrNull(itemCategoryId)) {
                Item.query(function(result, headers) {
                    $scope.items = result;
                });
            } else {
                ItemService.getByCategory(itemCategoryId).then(function(data) {
                    $scope.items = data;
                });
            }
        };
        
        function init() {
            $scope.order = {
                quantity: 0,
            	amount: 0,
            	details: []
            };

            $scope.total = {
            	discount: 0,
            	tax: 0,
            	payable: 0
            };        	
        };

        $scope.loadAll = function() {
        	init();
	        Principal.identity().then(function(account) {
	            $scope.account = account;
	            $scope.isAuthenticated = Principal.isAuthenticated;
	        });
            // load all item category
            ItemCategory.query(function(result, headers) {
                $scope.itemCategories = result;
                if (!Utils.isUndefinedOrNull($scope.itemCategories) && $scope.itemCategories.length > 0) {
                    var itemCategory = {
                        name: $filter('translate')('common.all')
                    };
                    $scope.itemCategories.unshift(itemCategory);
                }
            });
            // load all item
            Item.query(function(result, headers) {
                $scope.items = result;
            });            
        };

        $scope.loadAll();        

    });
