'use strict';

angular.module('posApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('user', {
                parent: 'account',
                url: '/users',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'user.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/management/users.html',
                        controller: 'UserController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('user');
                        $translatePartialLoader.addPart('settings');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('user.detail', {
                parent: 'account',
                url: '/user/{id}',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'user.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/management/user-detail.html',
                        controller: 'UserDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('user');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'user', function($stateParams, User) {
                        return User.get({id : $stateParams.id});
                    }]
                }
            })
            .state('user.new', {
                parent: 'user',
                url: '/new',
                data: {
                    roles: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$modal', 'Authority', 'User', 
                    function($stateParams, $state, $modal, Authority, User) {
                    $modal.open({
                        /* Issue https://github.com/angular-ui/bootstrap/issues/3849 */
                        animation: false,
                        templateUrl: 'scripts/app/account/management/user-dialog.html',
                        controller: 'UserDialogController',
                        size: 'lg',
                        resolve: {                            
                            entity: function () {
                                return {login: null, id: null};
                            },
                            authorities: function () {
                                return Authority.query(function(result) {
                                    return result;
                                });
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('user', null, { reload: true });
                    }, function() {
                        $state.go('user');
                    })
                }]
            })
            .state('user.edit', {
                parent: 'user',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/account/management/user-dialog.html',
                        controller: 'UserDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['User', function(user) {
                                return User.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('user', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
