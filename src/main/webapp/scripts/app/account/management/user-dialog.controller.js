'use strict';

angular.module('posApp').controller('UserDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Account', 'authorities', 'Utils', 'User', 
        function($scope, $stateParams, $modalInstance, entity, Account, authorities, Utils, User) {

        $scope.user = entity;
        $scope.authorities = authorities;

        $scope.load = function(id) {
            Account.get({id : id}, function(result) {
                $scope.user = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('posApp:userUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if (!Utils.isUndefinedOrNull($scope.user.id)) {
                User.update($scope.user, onSaveFinished);
            } else {
                User.save($scope.user, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);