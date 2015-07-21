'use strict';

angular.module('posApp')
    .controller('MainController', function ($scope, Principal, moment, Constants, Utils, ChartService) {

    	$scope.sales = [];
    	$scope.saleItems = [];

        ChartService.receive().then(null, null, function(chart) {
            showChart(chart);
        });

        function showChart(chart) {
        	$scope.saleItems = chart.pies;
			$scope.sales = [];
			var result = chart.lines;
			var el = {
	 			'key': Constants.orderStatus.payment,
				'values': []
			};
			angular.forEach(result, function(sale) {
				el.values.push(angular.copy(sale.values));
			});
			$scope.sales.push(el);        	
        };    	

        $scope.getSaleLineChart = function(orderStatus, fromDate, toDate) {
	        ChartService.getSaleByStatusCreatedDateBetween(orderStatus, fromDate, toDate)
	        	.then(function(response) {
	        		if (!Utils.isUndefinedOrNull(response) && 
	        				!Utils.isUndefinedOrNull(response.data && 
	        					response.data.length > 0)) {
	        			$scope.sales = [];
	        			var result = response.data;
	        			var el = {
				 			'key': orderStatus,
							'values': []
						};
	        			angular.forEach(result, function(sale) {
	        				el.values.push(angular.copy(sale.values));
	        			});
	        			$scope.sales.push(el);
	        		}
	        	});
        };

        $scope.xAxisTickFormatFunction = function() {
            return function(d) {
                return d3.time.format('%d/%m/%Y')(moment(d).toDate());
            }
        };

        $scope.yAxisTickFormatFunction = function() {

        };

		$scope.xLineChartFunction = function() {
			return function(d) {
				return d[0];
			};
		};

		$scope.yLineChartFunction = function() {
			return function(d) {
				return d[1];
			};
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
			return function(d) {
				return d.y;
			};
		};

		 $scope.margin = function() {
		    return { left:0, top:0, bottom:0, right:0 };
		 };				        

        $scope.loadAll = function() {
	        Principal.identity().then(function(account) {
	            $scope.account = account;
	            $scope.isAuthenticated = Principal.isAuthenticated;
	        });
	        $scope.getSaleLineChart(Constants.orderStatus.payment, null, null);
	        $scope.getSaleItemPieChart(Constants.orderStatus.payment, null, null);
	        ChartService.connect();
        };

        $scope.loadAll();

    });
