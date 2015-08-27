'use strict';

angular.module('posApp')
    .factory('OrderService', function ($http) {
        return {
            createOrder: function(order) {
                return $http.post('/api/orders', order);
            },
            updateOrder: function(order) {
                return $http.put('/api/orders', order);
            },
            getById: function(id) {
                var url = 'api/orders/' + id;
                return $http.get(url);
            },            
            getByTableIdStatusCreatedDate: function (page, perPage, tableId, status, from, to) {
                return $http.get('api/orders', { 
                    params: {
                        page: page, 
                        per_page: perPage,
                        tableId: tableId,
                        status: status,
                        from: from,
                        to: to
                    }
                });
            },
            getSumReceivableAmountByStatusCreatedDate: function (status, from, to) {
                return $http.get('api/orders/receivable-amount', { 
                    params: {
                        status: status,
                        from: from,
                        to: to
                    }
                });
            },
            moveItem: function(orders) {
                return $http.post('/api/orders/move', orders);
            }            
        };
    });
