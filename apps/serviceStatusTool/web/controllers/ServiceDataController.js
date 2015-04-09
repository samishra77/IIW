var ServiceDataController = function ($scope,$routeParams) {

	$scope.circuit= {	circuitID:'HAM-LE-111805',	Status:'Live',	InServiceSince:'16-Dec-2014', Customer:'Market Prizm',Category:	'LANLINK'};

	$scope.circuitID=$routeParams.circuitID;

	$scope.tickets=[{ticketID:'ticket1',ticketTitle:'ticket title 1'},{ticketID:'ticket2',ticketTitle:'ticket title 2'},{ticketID:'ticket3',ticketTitle:'ticket title 3'},{ticketID:'ticket4',ticketTitle:'ticket title 4'}];

};
angular.module('sstApp').controller('ServiceDataController',ServiceDataController);

     