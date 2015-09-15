'use strict';

angular.module('posApp')
    
	.factory('Utils', ['$location', '$window', 'AuthServerProvider', function($location, $window, AuthServerProvider) {

		var utils = {
			isUndefinedOrNull: function(obj) {
				return angular.isUndefined(obj) || obj === null;
		    },
			isInt: function(n) {
			    return Number(n) === n && n % 1 === 0;
			},
			isFloat: function(n) {
			    return n === Number(n) && n % 1 !== 0;
			},
            openTab: function(uri) {
                var endIndex = $location.absUrl().length - $location.path().length;
                var url = $location.absUrl().substring(0, endIndex);
                url = url.concat(uri);
                $window.open(url, '_blank');
            },
            downloadReport: function(uri) {
                var endIndex = $location.absUrl().length - $location.path().length;
                var url = $location.absUrl().substring(0, endIndex - 1);
                url = url.concat('api/report').concat(uri);
                
                url = url.concat('?access_token=').concat(AuthServerProvider.getToken().access_token);
                $window.open(url, '_blank');
            }		    								
		};
		return utils;
	}])

    .constant('Constants', {
        orderStatus: {
        	cancel: 'CANCEL',
            hold: 'HOLD',
            payment: 'PAYMENT'
        },
        dateTimePattern: 'DD/MM/YYYY HH:mm:ss',
        fractionSize: 2,
        perPage: 6,
        all: 'ALL',
        perCategory: 5,
        perItem: 24,
        openShift: 'openshift'
    });

angular.module('posApp')
  	.run(function ($rootScope, Constants) {
    	$rootScope.constants = Constants;
   	}); 
