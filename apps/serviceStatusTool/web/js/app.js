var managerModule = angular.module('managerApp', ['ngAnimate']);

managerModule.controller('managerController', function ($scope,$http) {
	
	var urlBase="http://localhost:8080/serviceStatusTool/ws";
	$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
	
	//get all tasks and display initially
	$http.get(urlBase+'/models').
    	success(function(data) {
	        $scope.models = data;
    });
});

//Angularjs Directive for confirm dialog box
managerModule.directive('ngConfirmClick', [
	function(){
         return {
             link: function (scope, element, attr) {
                 var msg = attr.ngConfirmClick || "Are you sure?";
                 var clickAction = attr.confirmedClick;
                 element.bind('click',function (event) {
                     if ( window.confirm(msg) ) {
                         scope.$eval(clickAction);
                     }
                 });
             }
         };
 }]);