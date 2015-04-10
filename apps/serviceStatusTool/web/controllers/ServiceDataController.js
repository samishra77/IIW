var ServiceDataController = function ($scope,$routeParams) {

//	$scope.circuit= {	circuitID:'HAM-LE-111805',	Status:'Live',	InServiceSince:'16-Dec-2014', Customer:'Market Prizm',Category:	'LANLINK'};
//
//	$scope.circuitID = $routeParams.circuitID;
//
//	$scope.tickets=[{ticketID:'ticket1',ticketTitle:'ticket title 1'},{ticketID:'ticket2',ticketTitle:'ticket title 2'},{ticketID:'ticket3',ticketTitle:'ticket title 3'},{ticketID:'ticket4',ticketTitle:'ticket title 4'}];
	var urlBase="/serviceStatusTool/ws";
	var resp = $http({
	  method  : 'GET',
	  url     : urlBase + '/getServiceDetail',
	  data    : JSON.stringify($routeParams.circuitID), 
	  headers : { 'Content-Type': 'application/json' }
	 });
	resp.success(function(data) {
		if(data.status == 'fail') {
			$scope.message = data.errorMsg;
		} else {
			$scope.circuit = data.result;
		}
	});
	resp.error(function(data, status, headers, config) {
		alert( "failure message: " + JSON.stringify({config: config}));
	});
};
angular.module('sstApp').controller('ServiceDataController',ServiceDataController);