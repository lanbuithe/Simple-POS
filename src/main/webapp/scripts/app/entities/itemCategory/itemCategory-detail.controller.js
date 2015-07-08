'use strict';

angular.module('posApp')
    .controller('ItemCategoryDetailController', function ($scope, $stateParams, ItemCategory) {
        $scope.itemCategory = {};
        $scope.load = function (id) {
            ItemCategory.get({id: id}, function(result) {
              $scope.itemCategory = result;
            });
        };
        $scope.load($stateParams.id);
    });
