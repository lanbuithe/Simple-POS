'use strict';

angular.module('posApp')
    .controller('MainController', function ($scope, Principal, Constants, Utils, ChartService) {

    	$scope.sales = [];
    	$scope.saleItems = [];

        $scope.getSaleLineChart = function(orderStatus, fromDate, toDate) {
	        ChartService.getSaleByStatusCreatedDateBetween(orderStatus, fromDate, toDate)
	        	.then(function(response) {
	        		if (!Utils.isUndefinedOrNull(response) && !Utils.isUndefinedOrNull(response.data)) {
	        			$scope.sales = response.data;
	        		}
	        	});
        };

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
	        //$scope.getSaleLineChart(Constants.orderStatus.payment, null, null);
	        $scope.getSaleItemPieChart(Constants.orderStatus.payment, null, null);
	        /*$scope.sales = [{
	 "key" : "Series 1",
	"values" : [[1025409600000, 0], [1028088000000, -6.3382185140371], [1030766400000, -5.9507873460847], [1033358400000, -11.569146943813]]
}];*/
        };

        $scope.loadAll();

    });
