'use strict';

angular.module('posApp')
    .factory('ChartService', function ($http) {
        return {
            getSaleItemByStatusCreatedDateBetween: function (status, from, to) {
                return $http.get('/api/charts/item', { 
                    params: {
                        status: status,
                        from: from,
                        to: to
                    }
                });
            },
            getSaleByStatusCreatedDateBetween: function (status, from, to) {
                return $http.get('/api/charts/sale', { 
                    params: {
                        status: status,
                        from: from,
                        to: to
                    }
                });
            }            
        };
    });
