var ServiceDataController = function ($scope,$routeParams,$http) {
	var urlBase = contextPath + "/ws";
	var orderNumber = findOrderNumberByURL();
	if (orderNumber) {
		$scope.showPopUp = true;
		var resp = $http({
			method  : 'POST',
			url     : urlBase + '/getCircuitsByOrderNumber?username=' + username,
			data    : orderNumber,
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
				$scope.showLoading = false;
			} else {
				$scope.showLoading = false;
				$scope.showResult = true;
				$scope.circuitList = data.result;
			}
		});
	} else {
		$scope.viewServiceData = true;
		$scope.showPopUp = false;
		serviceDetailsAndTickets();
	}

	function serviceDetailsAndTickets() {
		$scope.showDetailsLoading = true;
		$scope.showTicketLoading = true;
		$scope.viewSideInformation = false;
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
	}

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
	$scope.doRelatedOrderNumber = function doRelatedOrderNumber() {
		var URL = contextPath + "/popUp.jsp?username=" + username + "&orderNumber=" + encodeURIComponent($scope.circuit.relatedOrderNumber) + '#/ServiceData/' + $scope.circuit.circPathInstID;
		window.open(URL, "_blank");
	}

	function findOrderNumberByURL() {
		var orderNumber = "";
		var url = location.href;
		if (url.indexOf("?") > 0) {
			var param = url.split("#/");
			var query = param[0].split("?");
			var param = query[1].split("&");
			for (var i= 0; i < param.length; i++) {
				var v = param[i].split("=");
				if (v[0] == "orderNumber") {
					orderNumber = orderNumber = v[1];
					break;
				}
			}
		}
		return orderNumber;
	}
	$scope.findCircuitByCircPathInstID = function findCircuitByCircPathInstID(circPathInstID) {
		$scope.showPopUp = true;
		$scope.viewServiceData = true;
		$routeParams.circuitID = circPathInstID;
		serviceDetailsAndTickets();
	}
};
angular.module('sstApp').controller('ServiceDataController',ServiceDataController);
