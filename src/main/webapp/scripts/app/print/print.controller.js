'use strict';

angular.module('posApp')
    .controller('PrintController', function ($scope, $filter, $stateParams, $window, $timeout, 
         toaster, Principal, Constants, Utils, OrderService) {

        $scope.order = {};

        $scope.load = function(id) {
            OrderService.getById(id).then(function(response) {
                if (!Utils.isUndefinedOrNull(response)) {
                    $scope.order = response.data;
                    $timeout(function() {
                        $window.print();
                    }, 3000);
                }
            });
        };

        $scope.load($stateParams.id); 

    });
