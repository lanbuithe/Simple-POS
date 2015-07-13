'use strict';

angular.module('posApp')
    .controller('UserController', function ($scope, User, Account, ParseLinks) {
        $scope.users = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            User.query({page: $scope.page, per_page: 20}, function(result, headers) {
            //Account.get({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.users = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Account.get({id: id}, function(result) {
                $scope.user = result;
                $('#deleteUserConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Account.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUserConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.user = {name: null, id: null};
        };
    });
