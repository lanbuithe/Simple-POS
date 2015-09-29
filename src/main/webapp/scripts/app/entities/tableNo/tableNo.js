'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tableNo', {
                parent: 'entity',
                url: '/tableNo',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'posApp.tableNo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tableNo/tableNos.html',
                        controller: 'TableNoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tableNo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tableNoDetail', {
                parent: 'entity',
                url: '/tableNo/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'posApp.tableNo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tableNo/tableNo-detail.html',
                        controller: 'TableNoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tableNo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('table', {
                parent: 'entity',
                url: '/table',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'posApp.tableNo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tableNo/tables.html',
                        controller: 'TableController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tableNo');
                        return $translate.refresh();
                    }]
                }
            });
    });
