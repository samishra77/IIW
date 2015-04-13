

var sstApp = angular.module('sstApp',['ngRoute']);

sstApp.config(function($routeProvider){
    $routeProvider
    .when('/',
          {
        controller:'SearchController',
        templateUrl:'pages/SearchForm.jsp'
    })
   .when('/ServiceData/:circuitID',
          {
        controller:'ServiceDataController',
        templateUrl:'pages/ServiceData.jsp'
    })     
    .otherwise({redirectTo:'/'});
   
});