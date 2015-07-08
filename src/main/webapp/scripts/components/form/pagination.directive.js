/* globals $ */
'use strict';

angular.module('coffeeApp')
    .directive('coffeeAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
