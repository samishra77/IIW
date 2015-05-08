var ServiceDataController = function ($scope,$routeParams,$http) {
	$scope.showDetailsLoading = true;
	$scope.showTicketLoading = true;
	$scope.viewSideInformation = false;
	var urlBase = contextPath + "/ws";
	var resp = $http({
	  method  : 'POST',
	  url     : urlBase + '/getServiceDetail?username=' + username,
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
			$scope.showDetailsLoading = false;
		} else {
			$scope.msgTicket = false;
			$scope.msg = false;
			$scope.error = false;
			$scope.hasServiceDetail = true;
			$scope.showDetailsLoading = false;
			$scope.circuit = data.result;
			if($scope.circuit != null) {
				var resp2 = $http({
				  method  : 'POST',
				  url     : urlBase + '/getTickets?username=' + username,
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
						$scope.showTicketLoading = false;
					} else {
						$scope.tickets = data.result;
						$scope.showTicketLoading = false;
					}
				});
			}
		}
	});
	$scope.openXperdrawPopup = function() {
		var URL = "http://lonxng67/xperdraw/circuit.php?circuit_id=" + encodeURIComponent($scope.circuit.circuitID) + "&action=return";
		window.open(URL, '_blank');
	}

	$scope.doSideInformation = function doSideInformation() {
		$scope.error = false;
		$scope.viewSideInformation = true;
		$scope.showSideLoading = true;
		var resp = $http({
			  method  : 'POST',
			  url     : urlBase + '/getSideInformation?username=' + username,
			  data    : $scope.circuit,
			  headers : { 'Content-Type': 'application/json' }
			 });
		resp.success(function(data) {
			$scope.showSideLoading = false;
			if(data.status == 'fail') {
				$scope.error = true;
				$scope.messageError = data.errorMsg;
			} else {
				$scope.sideInformation = data.result;
			}
		});
	}
};
angular.module('sstApp').controller('ServiceDataController',ServiceDataController);
