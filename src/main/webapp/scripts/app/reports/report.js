'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('report', {
                abstract: true,
                parent: 'site'
            });
    });
