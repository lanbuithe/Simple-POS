'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('revenue', {
                parent: 'report',
                url: '/revenue',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'revenue.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/reports/revenue/revenue.html',
                        controller: 'RevenueController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('revenue');
                        return $translate.refresh();
                    }]
                }
            });
    });
