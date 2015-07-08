'use strict';

angular.module('coffeeApp')
    .controller('OrderDetailDetailController', function ($scope, $stateParams, OrderDetail, OrderNo, Item) {
        $scope.orderDetail = {};
        $scope.load = function (id) {
            OrderDetail.get({id: id}, function(result) {
              $scope.orderDetail = result;
            });
        };
        $scope.load($stateParams.id);
    });
