/* globals $ */
'use strict';

angular.module('posApp')
    .directive('posAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
