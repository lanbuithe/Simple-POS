'use strict';

angular.module('posApp')
    .controller('PrintController', function ($scope, $filter, $stateParams, toaster, 
         Principal, Constants, Utils, OrderNo, OrderService) {

        $scope.order = {};

        $scope.load = function(id) {
            OrderService.getById(id).then(function(response) {
                if (!Utils.isUndefinedOrNull(response)) {
                    $scope.order = response.data;
                }
            });
        };

        $scope.load($stateParams.id); 

    });
