'use strict';

angular.module('posApp')
    .factory('ChartService', ['$http', '$q', '$window', '$cookies', 'localStorageService', 'CLOUD', 'Constants',
        function ($http, $q, $window, $cookies, localStorageService, CLOUD, Constants) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        var obj;
        return obj = {
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
            },
            connect: function () {
                //building absolute path so that websocket doesnt fail when deploying with a context path
                var loc = $window.location;
                var url = '//' + loc.host + loc.pathname + 'websocket/chart';
                var token = localStorageService.get('token');
                if (token && token.expires_at && token.expires_at > new Date().getTime()) {
                    url += '?access_token=' + token.access_token;
                }
                var socket = new SockJS(url);
                stompClient = Stomp.over(socket);
                var headers = {};
                headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
                stompClient.connect(headers, function(frame) {
                    connected.resolve("success");
                    obj.subscribe();
                });
            },
            subscribe: function() {
                connected.promise.then(function() {
                    subscriber = stompClient.subscribe("/topic/chart", function(data) {
                        listener.notify(JSON.parse(data.body));
                    });
                }, null, null);
            },
            unsubscribe: function() {
                if (subscriber != null) {
                    subscriber.unsubscribe();
                }
            },
            receive: function() {
                return listener.promise;
            },
            disconnect: function() {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            },
            sendMail: function () {
                return $http.get('/api/charts/mail');
            }                        
        };
    }]);
