'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('itemCategory', {
                parent: 'entity',
                url: '/itemCategory',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'posApp.itemCategory.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/itemCategory/itemCategorys.html',
                        controller: 'ItemCategoryController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('itemCategory');
                        return $translate.refresh();
                    }]
                }
            })
            .state('itemCategoryDetail', {
                parent: 'entity',
                url: '/itemCategory/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'posApp.itemCategory.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/itemCategory/itemCategory-detail.html',
                        controller: 'ItemCategoryDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('itemCategory');
                        return $translate.refresh();
                    }]
                }
            });
    });
