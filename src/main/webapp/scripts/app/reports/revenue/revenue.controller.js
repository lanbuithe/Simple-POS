'use strict';

angular.module('posApp')
    .controller('RevenueController', function ($scope, ParseLinks, moment, Constants, Utils, OrderService) {
        $scope.from = moment().set('date', 1).toDate();
        $scope.to = moment().toDate();

        $scope.orders = [];
        $scope.page = 1;
        $scope.totalAmount = 0;

        $scope.loadAll = function() {
            getPaymentOrder();
            OrderService.getSumAmountByStatusCreatedDate(Constants.orderStatus.payment, $scope.from, $scope.to).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data)) {
                        $scope.totalAmount = response.data;
                    }
            });            
        };

        $scope.search = function() {
            $scope.page = 1;
            $scope.orders = [];
            $scope.loadAll();
        };

        $scope.loadPage = function(page) {
            $scope.page = page;
            getPaymentOrder();
        };

        function getPaymentOrder() {
            var tableId = null;
            OrderService.getByTableIdStatusCreatedDate($scope.page, 6, tableId, Constants.orderStatus.payment, $scope.from, $scope.to).then(
                function(response) {
                    if (!Utils.isUndefinedOrNull(response.data) && 
                        response.data.length !== 0) {
                        $scope.links = ParseLinks.parse(response.headers('link'));
                        for (var i = 0; i < response.data.length; i++) {
                            $scope.orders.push(response.data[i]);
                        }
                    }
            });
        };

        $scope.loadAll();

    });
