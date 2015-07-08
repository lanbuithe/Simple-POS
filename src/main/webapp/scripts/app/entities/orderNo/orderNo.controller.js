'use strict';

angular.module('posApp')
    .controller('OrderNoController', function ($scope, OrderNo, TableNo, ParseLinks) {
        $scope.orderNos = [];
        $scope.tablenos = TableNo.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            OrderNo.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.orderNos = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            OrderNo.update($scope.orderNo,
                function () {
                    $scope.loadAll();
                    $('#saveOrderNoModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            OrderNo.get({id: id}, function(result) {
                $scope.orderNo = result;
                $('#saveOrderNoModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            OrderNo.get({id: id}, function(result) {
                $scope.orderNo = result;
                $('#deleteOrderNoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            OrderNo.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteOrderNoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.orderNo = {amount: null, status: null, createdDate: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
