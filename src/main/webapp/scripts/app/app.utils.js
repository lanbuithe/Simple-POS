'use strict';

angular.module('posApp')
    
	.factory('Utils', [function() {

		var utils = {
			isUndefinedOrNull: function(obj) {
				return angular.isUndefined(obj) || obj === null;
		    },
			isInt: function(n) {
			    return Number(n) === n && n % 1 === 0;
			},
			isFloat: function(n) {
			    return n === Number(n) && n % 1 !== 0;
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
        perPage: 6
    });

angular.module('posApp')
  	.run(function ($rootScope, Constants) {
    	$rootScope.constants = Constants;
   	}); 
