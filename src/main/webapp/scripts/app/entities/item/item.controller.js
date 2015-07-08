'use strict';

angular.module('posApp')
    .controller('ItemController', function ($scope, Item, ItemCategory, ParseLinks) {
        $scope.items = [];
        $scope.itemcategorys = ItemCategory.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Item.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.items.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.items = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Item.update($scope.item,
                function () {
                    $scope.reset();
                    $('#saveItemModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Item.get({id: id}, function(result) {
                $scope.item = result;
                $('#saveItemModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Item.get({id: id}, function(result) {
                $scope.item = result;
                $('#deleteItemConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Item.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteItemConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.item = {name: null, description: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
