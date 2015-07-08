'use strict';

angular.module('posApp')
    .controller('TableController', function ($scope, TableNo, ParseLinks) {
        $scope.tableNos = [];

        $scope.loadAll = function() {
            TableNo.query(function(result, headers) {
                for (var i = 0; i < result.length; i++) {
                    $scope.tableNos.push(result[i]);
                }
            });
        };

        $scope.loadAll();

    });
