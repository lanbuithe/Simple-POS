'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('orderNo', {
                parent: 'entity',
                url: '/orderNo',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'posApp.orderNo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/orderNo/orderNos.html',
                        controller: 'OrderNoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('orderNo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('orderNoDetail', {
                parent: 'entity',
                url: '/orderNo/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'posApp.orderNo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/orderNo/orderNo-detail.html',
                        controller: 'OrderNoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('orderNo');
                        return $translate.refresh();
                    }]
                }
            });
    });
