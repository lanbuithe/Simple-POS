'use strict';

angular.module('coffeeApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('order', {
                parent: 'site',
                url: '/order',
                data: {
                    roles: [],
                    pageTitle: 'order.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/order/order.html',
                        controller: 'OrderController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('order');
                        return $translate.refresh();
                    }]
                }
            });
    });
