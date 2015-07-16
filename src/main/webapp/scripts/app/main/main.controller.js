'use strict';

angular.module('posApp')
    .controller('MainController', function ($scope, Principal, Constants, Utils, ChartService) {

    	$scope.saleItems = [];

        $scope.getSaleItemPieChart = function(orderStatus, fromDate, toDate) {
	        ChartService.getSaleItemByStatusCreatedDateBetween(orderStatus, fromDate, toDate)
	        	.then(function(response) {
	        		if (!Utils.isUndefinedOrNull(response) && !Utils.isUndefinedOrNull(response.data)) {
	        			$scope.saleItems = response.data;
	        		}
	        	});
        };

		$scope.xFunction = function() {
		    return function(d) {
		        return d.key;
		    };
		};

		$scope.yFunction = function() {
			return function(d){
				return d.y;
			};
		};

		 $scope.margin = function() {
		    return {left:0,top:0,bottom:0,right:0};
		 };				        

        $scope.loadAll = function() {
	        Principal.identity().then(function(account) {
	            $scope.account = account;
	            $scope.isAuthenticated = Principal.isAuthenticated;
	        });
	        $scope.getSaleItemPieChart(Constants.orderStatus.payment, null, null);
        };

        $scope.loadAll();

    });
