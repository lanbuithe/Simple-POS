'use strict';

angular.module('coffeeApp')
    .controller('TableNoController', function ($scope, TableNo, ParseLinks) {
        $scope.tableNos = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            TableNo.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.tableNos.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.tableNos = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            TableNo.update($scope.tableNo,
                function () {
                    $scope.reset();
                    $('#saveTableNoModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            TableNo.get({id: id}, function(result) {
                $scope.tableNo = result;
                $('#saveTableNoModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            TableNo.get({id: id}, function(result) {
                $scope.tableNo = result;
                $('#deleteTableNoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            TableNo.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteTableNoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.tableNo = {name: null, description: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
