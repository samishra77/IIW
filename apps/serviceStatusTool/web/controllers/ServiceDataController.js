var ServiceDataController = function ($scope,$routeParams,$http) {

	var urlBase="/serviceStatusTool/ws";
	var resp = $http({
	  method  : 'POST',
	  url     : urlBase + '/getServiceDetail',
	  data    : $routeParams.circuitID, 
	  headers : { 'Content-Type': 'application/json' }
	 });
	resp.success(function(data) {
		if(data.status == 'fail') {
			if(data.errorCode == '2') {
				$scope.error = true;
				$scope.messageError = data.errorMsg;
			} else {
				$scope.msg = true;
				$scope.message = data.errorMsg;
			}
		} else {
			$scope.msgTicket = false;
			$scope.msg = false;
			$scope.error = false;
			$scope.hasServiceDetail = true;
			$scope.circuit = data.result;
			if($scope.circuit != null) {
				var resp2 = $http({
				  method  : 'POST',
				  url     : urlBase + '/getTickets',
				  data    : $scope.circuit, 
				  headers : { 'Content-Type': 'application/json' }
				 });
				resp2.success(function(data) {
					if(data.status == 'fail') {
						if(data.errorCode == '2') {
							$scope.error = true;
							$scope.messageError = data.errorMsg;
						} else {
							$scope.msgTicket = true;
							$scope.messageTicket = data.errorMsg;
						}
					} else {
						$scope.tickets = data.result;
					}
				});
			}
		}
	});
};
angular.module('sstApp').controller('ServiceDataController',ServiceDataController);