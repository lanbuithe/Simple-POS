'use strict';

angular.module('posApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


