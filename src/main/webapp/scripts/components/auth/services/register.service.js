'use strict';

angular.module('coffeeApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


