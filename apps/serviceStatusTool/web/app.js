

var sstApp = angular.module('sstApp',['ngRoute']);

sstApp.config(function($routeProvider){
    $routeProvider
    .when('/',
          {
        controller:'SearchController',
        templateUrl:'pages/SearchForm.html'
    })
   .when('/ServiceData/:circuitID',
          {
        controller:'ServiceDataController',
        templateUrl:'pages/ServiceData.html'
    })     
    .otherwise({redirectTo:'/'});
   
});