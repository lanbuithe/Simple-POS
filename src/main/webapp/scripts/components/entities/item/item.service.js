'use strict';

angular.module('coffeeApp')
    .factory('Item', function ($resource) {
        return $resource('api/items/:id', {}, {
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
    .factory('ItemService', function ($http) {
        return {
            getByCategory: function (itemCategoryId) {
                return $http.get('api/items/category', {params: {
                		id: itemCategoryId
                	}
                }).then(function (response) {
                    return response.data;
                });
            }
        };
    });
