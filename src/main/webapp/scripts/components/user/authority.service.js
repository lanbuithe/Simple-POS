'use strict';

angular.module('posApp')
    .factory('Authority', function ($resource) {
        return $resource('api/authorities', {}, {
                'query': {method: 'GET', isArray: true},
                'get': {
                    method: 'GET',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        return data;
                    }
                }
            });
        });
