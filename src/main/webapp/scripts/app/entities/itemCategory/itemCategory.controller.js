'use strict';

angular.module('coffeeApp')
    .controller('ItemCategoryController', function ($scope, ItemCategory, ParseLinks) {
        $scope.itemCategorys = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            ItemCategory.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.itemCategorys.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.itemCategorys = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            ItemCategory.update($scope.itemCategory,
                function () {
                    $scope.reset();
                    $('#saveItemCategoryModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            ItemCategory.get({id: id}, function(result) {
                $scope.itemCategory = result;
                $('#saveItemCategoryModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            ItemCategory.get({id: id}, function(result) {
                $scope.itemCategory = result;
                $('#deleteItemCategoryConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ItemCategory.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteItemCategoryConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.itemCategory = {name: null, description: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
