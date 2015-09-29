'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('orderDetail', {
                parent: 'entity',
                url: '/orderDetail',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'posApp.orderDetail.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/orderDetail/orderDetails.html',
                        controller: 'OrderDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('orderDetail');
                        return $translate.refresh();
                    }]
                }
            })
            .state('orderDetailDetail', {
                parent: 'entity',
                url: '/orderDetail/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'posApp.orderDetail.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/orderDetail/orderDetail-detail.html',
                        controller: 'OrderDetailDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('orderDetail');
                        return $translate.refresh();
                    }]
                }
            });
    });
