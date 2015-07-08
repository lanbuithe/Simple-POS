'use strict';

angular.module('coffeeApp')
    .controller('OrderNoDetailController', function ($scope, $stateParams, OrderNo, TableNo) {
        $scope.orderNo = {};
        $scope.load = function (id) {
            OrderNo.get({id: id}, function(result) {
              $scope.orderNo = result;
            });
        };
        $scope.load($stateParams.id);
    });
