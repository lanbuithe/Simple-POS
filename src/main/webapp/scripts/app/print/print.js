'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('print', {
                parent: 'site',
                url: '/print/{id}',
                data: {
                    roles: [],
                    pageTitle: 'print.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/print/print.html',
                        controller: 'PrintController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('order');
                        $translatePartialLoader.addPart('print');
                        return $translate.refresh();
                    }]
                }
            });
    });
