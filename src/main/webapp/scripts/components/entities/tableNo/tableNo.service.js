'use strict';

angular.module('posApp')
    .factory('TableNo', function ($resource) {
        return $resource('api/tableNos/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    })
    .factory('TableService', function ($http) {
        return {
            findByOrder: function (orderStatus) {
                return $http.get('api/tableNos/order', {params: {
                        status: orderStatus
                    }
                }).then(function (response) {
                    return response.data;
                });
            }
        };
    });
