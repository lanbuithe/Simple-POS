'use strict';

angular.module('posApp')
    .controller('ItemDetailController', function ($scope, $stateParams, Item, ItemCategory) {
        $scope.item = {};
        $scope.load = function (id) {
            Item.get({id: id}, function(result) {
              $scope.item = result;
            });
        };
        $scope.load($stateParams.id);
    });
