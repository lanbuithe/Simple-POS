'use strict';

angular.module('posApp')
    .factory('OrderNo', function ($resource) {
        return $resource('api/orderNos/:id', {}, {
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
            },
            getById: function(id) {
                var url = 'api/orders/' + id;
                return $http.get(url);
            },
            createOrder: function(order) {
                return $http.post('/api/orders', order);
            }            
        };
    });
