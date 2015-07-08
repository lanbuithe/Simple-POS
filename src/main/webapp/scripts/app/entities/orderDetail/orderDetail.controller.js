'use strict';

angular.module('posApp')
    .controller('OrderDetailController', function ($scope, OrderDetail, OrderNo, Item, ParseLinks) {
        $scope.orderDetails = [];
        $scope.ordernos = OrderNo.query();
        $scope.items = Item.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            OrderDetail.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.orderDetails = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            OrderDetail.update($scope.orderDetail,
                function () {
                    $scope.loadAll();
                    $('#saveOrderDetailModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            OrderDetail.get({id: id}, function(result) {
                $scope.orderDetail = result;
                $('#saveOrderDetailModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            OrderDetail.get({id: id}, function(result) {
                $scope.orderDetail = result;
                $('#deleteOrderDetailConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            OrderDetail.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteOrderDetailConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.orderDetail = {quantity: null, amount: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
