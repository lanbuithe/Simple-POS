'use strict';

angular.module('posApp')
    .controller('OrderController', function ($scope, $filter, Principal, toaster, TableNo, 
        ItemCategory, Item, ItemService, OrderNo, Constants, Utils, OrderService, ParseLinks) {
    	
    	$scope.tables = TableNo.query();
        $scope.itemCategories = [];
        $scope.items = [];
        $scope.endCategoryIndex = 4;
        $scope.endItemIndex = 23;
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
        	if (Utils.isUndefinedOrNull($scope.order.status)) {
                var message = $filter('translate')('order.messages.info.order.cancel');
                toaster.pop('success', message);
        		init();
        	} else if ($scope.order.details.length > 0) {
        		$scope.order.status = Constants.orderStatus.cancel;
        		OrderNo.update($scope.order,
	                function () {
                        var message = $filter('translate')('order.messages.info.order.cancel');
                        toaster.pop('success', message);                        
        				init();
	                });        		
        	} 
        };
        
        $scope.holdOrder = function() {
        	if ($scope.order.details.length > 0) {
        		$scope.order.status = Constants.orderStatus.hold;
        		OrderNo.update($scope.order,
	                function () {
                        var message = $filter('translate')('order.messages.info.order.hold');
                        toaster.pop('success', message);                        
        				init();
	                });        		
        	}
        };

        $scope.payOrder = function() {
            if ($scope.order.details.length > 0) {
                $scope.order.status = Constants.orderStatus.payment;
                OrderNo.update($scope.order,
                    function () {
                        var message = $filter('translate')('order.messages.info.order.pay');
                        toaster.pop('success', message);                         
                        init();
                    });             
            }
        };        
        
        function sumOrderAmount() {
        	$scope.order.amount = 0;
        	$scope.total.count = 0;
        	angular.forEach($scope.order.details, function(orderDetail) {
        		$scope.order.amount += orderDetail.amount;
        		$scope.total.count += orderDetail.quantity;
                // for function addOrderDetail
                orderDetail.itemId = orderDetail.item.id;
        	});
        	$scope.total.payable = 0;
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
        	ItemService.getByCategory(itemCategoryId).then(function(data) {
        		$scope.items = data;
        	});
        };
        
        function init() {
            $scope.order = {
            	amount: 0,
            	details: []
            };

            $scope.total = {
            	count: 0,
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

            ItemCategory.query(function(result, headers) {
                $scope.itemCategories = result;
            });

            Item.query(function(result, headers) {
                $scope.items = result;
            });            
        };

        $scope.loadAll();        

    });
