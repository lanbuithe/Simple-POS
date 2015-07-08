'use strict';

angular.module('coffeeApp')
    .factory('OrderNo', function ($resource) {
        return $resource('api/orderNos/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    var createdDateFrom = data.createdDate.split("-");
                    data.createdDate = new Date(new Date(createdDateFrom[0], createdDateFrom[1] - 1, createdDateFrom[2]));
                    var lastModifiedDateFrom = data.lastModifiedDate.split("-");
                    data.lastModifiedDate = new Date(new Date(lastModifiedDateFrom[0], lastModifiedDateFrom[1] - 1, lastModifiedDateFrom[2]));
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    })
    .factory('OrderService', function ($http) {
        return {
            /*getByStatus: function (page, perPage, status) {
                return $http.get('api/orders', { 
                    params: {
                        page: page, 
                        per_page: perPage,
                        status: status
                    }
                }).then(function (response) {
                    return response.data;
                });
            }*/
            getByStatus: function (page, perPage, status, from, to) {
                return $http.get('api/orders', { 
                    params: {
                        page: page, 
                        per_page: perPage,
                        status: status,
                        from: from,
                        to: to
                    }
                });
            },
            getSumAmountByStatusCreatedDate: function (status, from, to) {
                return $http.get('api/orders/amount', { 
                    params: {
                        status: status,
                        from: from,
                        to: to
                    }
                });
            }           
        };
    });
