'use strict';

angular.module('coffeeApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('report', {
                abstract: true,
                parent: 'site'
            });
    });
