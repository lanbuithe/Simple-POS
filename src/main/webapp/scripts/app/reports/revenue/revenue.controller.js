'use strict';

angular.module('posApp')
  .controller('RevenueController', ['$scope', 'moment', 'ParseLinks', 'Constants', 'Utils', 'OrderService', 
    function ($scope, moment, ParseLinks, Constants, Utils, OrderService) {

    var revenue = this;

    revenue.from = moment().set('date', 1).toDate();
    revenue.to = moment().toDate();

    revenue.orders = [];
    revenue.page = 1;
    revenue.totalAmount = 0;

    revenue.downloadRevenueReport = function() {
      var parameters = {};
      parameters['from'] = moment(revenue.from).toISOString();
      parameters['to'] = moment(revenue.to).toISOString();
      Utils.downloadReport('/revenue', parameters);
    };

    revenue.loadAll = function() {
      getPaymentOrder();
      OrderService.getSumReceivableAmountByStatusCreatedDate(Constants.orderStatus.payment, revenue.from, revenue.to).then(
          function(response) {
              if (!Utils.isUndefinedOrNull(response.data)) {
                  revenue.totalAmount = response.data;
              }
      });            
    };

    revenue.search = function() {
      revenue.page = 1;
      revenue.orders = [];
      revenue.loadAll();
    };

    revenue.loadPage = function(page) {
      revenue.page = page;
      getPaymentOrder();
    };

    function getPaymentOrder() {
      var tableId = null;
      OrderService.getByTableIdStatusCreatedDate(revenue.page, 6, tableId, Constants.orderStatus.payment, revenue.from, revenue.to).then(
          function(response) {
              if (!Utils.isUndefinedOrNull(response.data) && 
                  response.data.length !== 0) {
                  revenue.links = ParseLinks.parse(response.headers('link'));
                  for (var i = 0; i < response.data.length; i++) {
                      revenue.orders.push(response.data[i]);
                  }
              }
      });
    };

    revenue.loadAll();

  }]);
