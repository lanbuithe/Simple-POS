'use strict';

angular.module('coffeeApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
