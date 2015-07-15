'use strict';

angular.module('posApp')
    .controller('OrderController', function ($scope, $filter, $window, Principal, toaster, TableNo, 
        ItemCategory, Item, ItemService, OrderNo, Constants, Utils, OrderService, ParseLinks) {
    	
    	$scope.tables = TableNo.query();
        $scope.itemCategories = [];
        $scope.items = [];
        $scope.endCategoryIndex = 4;
        $scope.endItemIndex = 23;
        $scope.order = {};
        $scope.holdOrders = [];
        $scope.pageHoldOrder = 1;

        $scope.selectHoldOrder = function(holdOrder) {
            $scope.order = holdOrder;
            sumOrderAmount();
            $('#holdOrderModal').modal('hide');
        };

        $scope.loadPageHoldOrder = function(page) {
            $scope.holdOrders.length = 0;
            $scope.pageHoldOrder = page;
            OrderService.getByStatus($scope.pageHoldOrder, 6, Constants.orderStatus.hold).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data)) {
                        $scope.linkHoldOrders = ParseLinks.parse(response.headers('link'));
                        $scope.holdOrders = response.data;
                    }
            });
        };        

        $scope.openHoldOrder = function() {
            $scope.holdOrders.length = 0;
            $scope.pageHoldOrder = 1;
            OrderService.getByStatus($scope.pageHoldOrder, 6, Constants.orderStatus.hold).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data)) {
                        $scope.linkHoldOrders = ParseLinks.parse(response.headers('link'));
                        $scope.holdOrders = response.data;
                        $('#holdOrderModal').modal('show');
                    }
            });
        };

        $scope.nextItem = function() {
            $scope.endItemIndex += 24;
        };

        $scope.prevItem = function() {
            $scope.endItemIndex -= 24;
        };        

        $scope.nextCategory = function() {
            $scope.endCategoryIndex += 5;
        };

        $scope.prevCategory = function() {
            $scope.endCategoryIndex -= 5;
        };

        $scope.cancelOrder = function() {
            var message = $filter('translate')('order.messages.info.order.cancel');
        	if (Utils.isUndefinedOrNull($scope.order.id)) {
                toaster.pop('success', message);
        		init();
        	} else {
        		$scope.order.status = Constants.orderStatus.cancel;
        		OrderNo.update($scope.order,
	                function () {
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

        /*function handleOrder(message, status) {
            $scope.order.status = status;
            if (Utils.isUndefinedOrNull($scope.order.id)) {
                OrderNo.save($scope.order,
                    function () {
                        toaster.pop('success', message);                        
                        init();
                    });
            } else {
                OrderNo.update($scope.order,
                    function () {
                        toaster.pop('success', message);                        
                        init();
                    });             
            }            
        }*/

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
                OrderNo.update($scope.order,
                    processOrder(message, status, $scope.order.id));             
            }            
        }        

        var processOrder = function printOrder(message, status, orderId) {
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
