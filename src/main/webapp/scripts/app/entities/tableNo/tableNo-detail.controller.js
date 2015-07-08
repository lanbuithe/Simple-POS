'use strict';

angular.module('posApp')
    .controller('TableNoDetailController', function ($scope, $stateParams, TableNo) {
        $scope.tableNo = {};
        $scope.load = function (id) {
            TableNo.get({id: id}, function(result) {
              $scope.tableNo = result;
            });
        };
        $scope.load($stateParams.id);
    });
