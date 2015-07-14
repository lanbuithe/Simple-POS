'use strict';

angular.module('posApp')
    .controller('UserController', function ($scope, User, Account, ParseLinks) {
        $scope.users = [];
        $scope.page = 1;

        $scope.loadAll = function() {
            User.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.users = result;
            });
        };

        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };

        $scope.loadAll();

        $scope.delete = function(user) {
            $scope.user = user;
            $('#deleteUserConfirmation').modal('show');
        };

        $scope.confirmDelete = function(id) {
            User.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUserConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function() {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function() {
            $scope.user = {id: null, login: null};
        };
    });
