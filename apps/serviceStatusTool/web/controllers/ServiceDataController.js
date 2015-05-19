var callRefreshCount = 0;
var callZSideCount = 0;
var callASideCount = 0;
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
		$scope.showSideLoading = true;
		$scope.sideInformationError = false;
		$scope.viewSideInformation = true;
		var resp = $http({
			  method  : 'POST',
			  url     : urlBase + '/getSideInformation?username=' + username,
			  data    : $scope.circuit,
			  headers : { 'Content-Type': 'application/json' }
			 });
		resp.success(function(data) {
			if(data.status == 'fail') {
				$scope.sideInformationError = true;
				$scope.sideInformationErrorMessage = data.errorMsg;
			} else {
				$scope.sideInformation = data.result;
				if ($scope.sideInformation != null && $scope.sideInformation.aSideInformation != null && $scope.sideInformation.zSideInformation) {
					sideInformationFromDevice();
				} else {
					$scope.viewSideInformation = false;
					$scope.sideInformationError = true;
					$scope.sideInformationErrorMessage = "Status Information not found.";
				}
			}
			$scope.showSideLoading = false;
		});
	}
	$scope.doRelatedOrderNumber = function doRelatedOrderNumber(relatedOrderNumber) {
		var URL = contextPath + "/popUp.jsp?username=" + username + "&orderNumber=" + encodeURIComponent(relatedOrderNumber) + '#/ServiceData/' + $scope.circuit.circPathInstID;
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

	function sideInformationFromDevice() {
		var urlWorkFlow = workFlowAgentUrlBase + "/ws";
		$scope.showDeviceErrorASide = false;
		$scope.showDeviceErrorZSide = false;
		$scope.showButtonRefreshASide = false;
		$scope.showButtonRefreshZSide = false;
		$scope.showRefreshASideLoading = true;
		$scope.showRefreshZSideLoading = true;
		if (workFlowAgentUrlBase) {
			if ($scope.sideInformation) {
				callASideCount++;
				$scope.showDeviceErrorASide = false;
				$scope.showDeviceErrorZSide = false;
				var deviceDetailsAside = {
						'requestID'	: callASideCount,
						'seibelUserID'	: username,
						'name'       	: $scope.sideInformation.aSideInformation.deviceName,
						'deviceType'	: {
							'vendor'	: $scope.sideInformation.aSideInformation.vendor,
							'model'  	: $scope.sideInformation.aSideInformation.model
						},
						'type': 'CPE',
						'circuitID': $scope.circuit.circuitID
				};
				var respAgentASide = $http({
					method  : 'POST',
					url     : urlWorkFlow + '/getDeviceDetails',
					data    : deviceDetailsAside,
					headers : { 'Content-Type': 'application/json' }
				});
				respAgentASide.success(function(data) {
					var l3DeviceDetails = data;
					if (l3DeviceDetails) {
						if (l3DeviceDetails.responseID == callASideCount) {
							if(l3DeviceDetails.errorResponse) {
								$scope.showDeviceErrorASide = true;
								$scope.deviceMessageASideError = l3DeviceDetails.errorResponse.message;
							}
							if (l3DeviceDetails.deviceDetails) {
								$scope.aSideInterfaces = l3DeviceDetails.deviceDetails.interfaces;
								$scope.aSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
								$scope.aSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
							}
							$scope.aSideManagementIPAddress = l3DeviceDetails.wanIP;
							$scope.showButtonRefreshASide = true;
						}
					} else {
						$scope.showDeviceErrorASide = true;
						$scope.deviceMessageASideError = "Error receiving device details.";
					}
					$scope.showButtonRefreshASide = true;
					$scope.showRefreshASideLoading = false;
				});
				callZSideCount++;
				var deviceDetailsZside = {
						'requestID'	: callZSideCount,
						'seibelUserID'	: username,
						'name'       	:  $scope.sideInformation.zSideInformation.xngDeviceName,
						'deviceType'	: {
							'vendor'	: $scope.sideInformation.zSideInformation.vendor,
							'model'  	: $scope.sideInformation.zSideInformation.model
						},
						'type': 'PE',
						'circuitID': $scope.circuit.circuitID
				};
				var respAgentZSide = $http({
					method  : 'POST',
					url     : urlWorkFlow + '/getDeviceDetails',
					data    : deviceDetailsZside,
					headers : { 'Content-Type': 'application/json' }
				});
				respAgentZSide.success(function(data) {
					var l3DeviceDetails = data;
					if (l3DeviceDetails.deviceDetails) {
						if (l3DeviceDetails.responseID == callZSideCount) {
							if (l3DeviceDetails) {
								$scope.zSideManagementIPAddress = l3DeviceDetails.wanIP;
								if (l3DeviceDetails.errorResponse) {
									$scope.showDeviceErrorZSide = true;
									$scope.deviceMessageZSideError = l3DeviceDetails.errorResponse.message;
								}
								if (l3DeviceDetails.deviceDetails) {
									for (var i = 0; l3DeviceDetails.deviceDetails.interfaces.length > i; i++) {
										if (l3DeviceDetails.deviceDetails.interfaces[i].ipaddress == $scope.zSideManagementIPAddress ) {
											$scope.zSideInterfaceLogical = l3DeviceDetails.deviceDetails.interfaces[i];
											l3DeviceDetails.deviceDetails.interfaces.splice(i, 1);
											$scope.zSidePhysicalInterfaces = l3DeviceDetails.deviceDetails.interfaces;
											break;
										};
									}
									$scope.zSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
									$scope.zSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
								}
							}
							$scope.showButtonRefreshZSide = true;
						}
					} else {
						$scope.showDeviceErrorZSide = true;
						$scope.deviceMessageZSideError = "Error receiving device details.";
					}
					$scope.showButtonRefreshZSide = true;
					$scope.showRefreshZSideLoading = false;
				});
			}
		} else {
			$scope.showButtonRefreshASide = true;
			$scope.showButtonRefreshZSide = true;
			$scope.showRefreshASideLoading = false;
			$scope.showRefreshZSideLoading = false;
		}
	}

	$scope.doDeviceRefresh = function doDeviceRefresh(type) {
		callRefreshCount++;
		var urlWorkFlow = workFlowAgentUrlBase + "/ws";
		if (type == "aside") {
			$scope.showRefreshASideLoading = true;
		} else if (type == "zside") {
			$scope.showRefreshZSideLoading = true;
		}
		if (workFlowAgentUrlBase) {
			var deviceDetails = {};
			if ($scope.sideInformation) {
				if (type == "aside") {
					$scope.showDeviceErrorASide = false;
					
					deviceDetails = {
							'requestID'	: callRefreshCount,
							'seibelUserID'	: username,
							'ip'       	: $scope.sideInformation.aSideManagementIPAddress,
							'deviceType'	: {
								'vendor'	: $scope.sideInformation.aSideInformation.vendor,
								'model'  	: $scope.sideInformation.aSideInformation.model
							},
							'type': 'CPE',
							'circuitID': $scope.circuit.circuitID
					};
				} else if (type == "zside") {
					$scope.showDeviceErrorZSide = false;
					deviceDetails = {
							'requestID'	: callRefreshCount,
							'seibelUserID'	: username,
							'ip'       	: $scope.sideInformation.zSideManagementIPAddress,
							'deviceType'	: {
								'vendor'	: $scope.sideInformation.zSideInformation.vendor,
								'model'  	: $scope.sideInformation.zSideInformation.model
							},
							'type': 'PE',
							'circuitID': $scope.circuit.circuitID
					};
				}
				if (deviceDetails) {
					var respAgent = $http({
						method  : 'POST',
						url     : urlWorkFlow + '/getDeviceDetails',
						data    : deviceDetails,
						headers : { 'Content-Type': 'application/json' }
					});
					respAgent.success(function(data) {
						var l3DeviceDetails = data;
						if (l3DeviceDetails) {
							if (l3DeviceDetails.responseID == callRefreshCount) {
								if (type == "aside") {
									$scope.aSideManagementIPAddress = l3DeviceDetails.wanIP;
								} else {
									$scope.zSideManagementIPAddress = l3DeviceDetails.wanIP;
								}
								if(l3DeviceDetails.errorResponse) {
									if (type == "aside") {
										$scope.showDeviceErrorASide = true;
										$scope.deviceMessageASideError = l3DeviceDetails.errorResponse.message;
									} else if (type == "zside") {
										$scope.showDeviceErrorZSide = true;
										$scope.deviceMessageZSideError = l3DeviceDetails.errorResponse.message;
									}
								}
								if (l3DeviceDetails.deviceDetails) {
									if (type == "aside") {
										$scope.aSideInterfaces = l3DeviceDetails.deviceDetails.interfaces;
										$scope.aSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
										$scope.aSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
									} else if (type == "zside") {
										for (var i = 0; l3DeviceDetails.deviceDetails.interfaces.length > i; i++) {
											if (l3DeviceDetails.deviceDetails.interfaces[i].ipaddress == $scope.zSideManagementIPAddress ) {
												$scope.zSideInterfaceLogical = l3DeviceDetails.deviceDetails.interfaces[i];
												l3DeviceDetails.deviceDetails.interfaces.splice(i, 1);
												$scope.zSidePhysicalInterfaces = l3DeviceDetails.deviceDetails.interfaces;
												break;
											};
										}
										$scope.zSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
										$scope.zSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
									}
								}
							}
						} else {
							var errorMsg = "Error receiving device details.";
							if (type == "aside") {
								$scope.showDeviceErrorASide = true;
								$scope.deviceMessageASideError = errorMsg;
							}  else if (type == "zside") {
								$scope.showDeviceErrorZSide = true;
								$scope.deviceMessageZSideError = errorMsg;
							}
						}
						if (type == "aside") {
							$scope.showRefreshASideLoading = false;
						} else if (type == "zside") {
							$scope.showRefreshZSideLoading = false;
						}
					});
				}
			}
		} else {
			if (type == "aside") {
				$scope.showRefreshASideLoading = false;
			}  else if (type == "zside") {
				$scope.showRefreshZSideLoading = false;
			}
		}
	}
};
angular.module('sstApp').controller('ServiceDataController',ServiceDataController);
