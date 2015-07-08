'use strict';

angular.module('coffeeApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('item', {
                parent: 'entity',
                url: '/item',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'coffeeApp.item.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/item/items.html',
                        controller: 'ItemController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('item');
                        return $translate.refresh();
                    }]
                }
            })
            .state('itemDetail', {
                parent: 'entity',
                url: '/item/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'coffeeApp.item.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/item/item-detail.html',
                        controller: 'ItemDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('item');
                        return $translate.refresh();
                    }]
                }
            });
    });
