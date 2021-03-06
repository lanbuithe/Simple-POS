'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('order', {
                parent: 'site',
                url: '/order',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'order.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/order/order.html',
                        controller: 'OrderController',
                        controllerAs: 'orderCtrl'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('order');
                        return $translate.refresh();
                    }]
                }
            });
    });
