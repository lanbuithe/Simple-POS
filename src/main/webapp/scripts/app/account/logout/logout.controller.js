'use strict';

angular.module('posApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
