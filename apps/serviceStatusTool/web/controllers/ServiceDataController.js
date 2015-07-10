var callRefreshCount = 0;
var callZSideCount = 0;
var callASideCount = 0;
var ServiceDataController = function ($scope,$routeParams,$http) {
	var urlBase = contextPath + "/ws";
	var orderNumber = findOrderNumberByURL();
	if (orderNumber) {
		$scope.showPopUp = true;
		var resp = $http({
			method  : 'GET',
			url     : urlBase + '/getCircuitsByOrderNumber?username=' + username + "&orderNumber=" + orderNumber,
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
		resp.error(function(data, status) {
			var errorMsg = getErrorMsg(status);
			$scope.error = true;
			$scope.messageError = errorMsg;
			$scope.showLoading = false;
		});
	} else {
		$scope.viewServiceData = true;
		$scope.showPopUp = false;
		serviceDetailsAndTickets();
	}

	function getErrorMsg(status) {
		var errorMsg;
		if (status == 0) {
			 errorMsg = "Connection to server failed.";
		} else {
			errorMsg = "Connection to server failed with status: " + status;
		}
		return errorMsg;
	}

	function serviceDetailsAndTickets() {
		$scope.showDetailsLoading = true;
		$scope.showTicketLoading = true;
		$scope.viewSideInformation = false;
		$scope.viewLanLinkZSideInformation = false;
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
					resp2.error(function(data, status) {
						var errorMsg = getErrorMsg(status);
						$scope.error = true;
						$scope.messageError = errorMsg;
						$scope.showTicketLoading = false;
					});
				}
			}
		});
		resp.error(function(data, status) {
			var errorMsg = getErrorMsg(status);
			$scope.error = true;
			$scope.messageError = errorMsg;
			$scope.showDetailsLoading = false;
			$scope.showTicketLoading = false;
		});
	}

	$scope.openXperdrawPopup = function() {
		var URL = "http://lonxng67/xperdraw/circuit.php?circuit_id=" + encodeURIComponent($scope.circuit.circuitID) + "&action=return";
		window.open(URL, '_blank');
	}

	$scope.doSideInformation = function doSideInformation() {
		$scope.showSideLoading = true;
		$scope.sideInformationError = false;
		$scope.viewSideInformation = true;
		if ($scope.circuit.productType.indexOf("LANLINK") > -1) {
			$scope.viewLanLinkZSideInformation = true;
		} else {
			$scope.viewLanLinkZSideInformation = false;
		}
		$scope.showRefreshASideLoading = true;
		$scope.showRefreshZSideLoading = true;
		$scope.showButtonRefreshASide = false;
		$scope.showButtonRefreshZSide = false;
		$scope.showDeviceErrorASide = false;
		$scope.showDeviceErrorZSide = false;
		resetSideInformation();
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
				if ( ($scope.sideInformation == null) || ($scope.sideInformation.aSideInformation == null && $scope.sideInformation.zSideInformation == null) ) {
					$scope.viewSideInformation = false;
					$scope.viewLanLinkZSideInformation = false;
					$scope.sideInformationError = true;
					$scope.sideInformationErrorMessage = "Status Information not found.";
				} else {
					if ($scope.sideInformation.aSideInformation != null && $scope.sideInformation.zSideInformation == null) {
						$scope.sideInformationError = true;
						$scope.sideInformationErrorMessage = "Z Side Status Information not found.";
					} else if ($scope.sideInformation.aSideInformation == null && $scope.sideInformation.zSideInformation != null) {
						$scope.sideInformationError = true;
						$scope.sideInformationErrorMessage = "A Side Status Information not found.";
					}
					sideInformationFromDevice();
				}
			}
			$scope.showSideLoading = false;
		});
		resp.error(function(data, status) {
			var errorMsg = getErrorMsg(status);
			$scope.sideInformationError = true;
			$scope.sideInformationErrorMessage = errorMsg;
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

	var conTimeout = 30000;
	function sideInformationFromDevice() {
		var urlWorkFlow = workFlowAgentUrlBase + "/ws";
		$scope.showDeviceErrorASide = false;
		$scope.showDeviceErrorZSide = false;
		$scope.showZEndPhysicalInterface = true;
		var errorMsg = "Error receiving device details.";
		if (workFlowAgentUrlBase) {
			if ($scope.sideInformation) {
				var startTimeA = new Date().getTime();
				if ($scope.sideInformation.aSideInformation) {
					if ($scope.sideInformation.aSideInformation.deviceName) {
						callASideCount++;
						var deviceDetailsAside;
						if ($scope.circuit.productType.indexOf("LANLINK") > -1) {
							deviceDetailsAside = {
									'requestID'	: callASideCount,
									'seibelUserID'	: username,
									'name'       	: $scope.sideInformation.aSideInformation.deviceName,
									'serviceType' : $scope.circuit.productType,
									'deviceType'	: {
										'vendor'	: $scope.sideInformation.aSideInformation.vendor,
										'model'  	: $scope.sideInformation.aSideInformation.model
									},
									'type': 'LANLink',
									"portName" 	: $scope.sideInformation.aSideInformation.port,
									"ocn" : $scope.circuit.customerOCN,
									'circuitID': $scope.circuit.circuitID,
									'ip': $scope.sideInformation.aSideInformation.ip
							};
						} else {
							deviceDetailsAside = {
									'requestID'	: callASideCount,
									'seibelUserID'	: username,
									'name'       	: $scope.sideInformation.aSideInformation.deviceName,
									'serviceType' : $scope.circuit.productType,
									'deviceType'	: {
										'vendor'	: $scope.sideInformation.aSideInformation.vendor,
										'model'  	: $scope.sideInformation.aSideInformation.model
									},
									'type': 'CPE',
									'circuitID': $scope.circuit.circuitID,
									'ip': $scope.sideInformation.aSideInformation.ip
							};
						}
						var respAgentASide = $http({
							method  : 'POST',
							url     : urlWorkFlow + '/getDeviceDetails',
							data    : deviceDetailsAside,
							headers : { 'Content-Type': 'application/json' },
							timeout : conTimeout
						});
						respAgentASide.success(function(data) {
							var l3DeviceDetails = data;
							if (l3DeviceDetails) {
								if (l3DeviceDetails.responseID == callASideCount) {
									if (l3DeviceDetails.deviceDetails) {
										$scope.aSideInterfaces = l3DeviceDetails.deviceDetails.interfaces;
										$scope.aSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
										$scope.aSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
									} else {
										$scope.showDeviceErrorASide = true;
										$scope.deviceMessageASideError = errorMsg;
									}
									if(l3DeviceDetails.errorResponse) {
										$scope.showDeviceErrorASide = true;
										$scope.deviceMessageASideError = l3DeviceDetails.errorResponse.message;
									}
									$scope.aSideManagementIPAddress = l3DeviceDetails.deviceIP;
								}
							} else {
								$scope.showDeviceErrorASide = true;
								$scope.deviceMessageASideError = errorMsg;
							}
							$scope.showButtonRefreshASide = true;
							$scope.showRefreshASideLoading = false;
						});
						respAgentASide.error(function(data, status) {
							$scope.showButtonRefreshASide = true;
							$scope.showRefreshASideLoading = false;
							$scope.showDeviceErrorASide = true;
							if (status == 0) {
								var respTime = new Date().getTime() - startTimeA;
								console.info(respTime+":"+startTimeA);
								if(respTime >= conTimeout) {
									errorMsg = "Connection timeout.";
								} else {
									errorMsg = "Connection to server failed.";
								}
							} else {
								errorMsg = "Connection to server failed with status: " + status;
							}
							$scope.deviceMessageASideError = errorMsg;
						});
					} else {
						$scope.showButtonRefreshASide = true;
						$scope.showRefreshASideLoading = false;
					}
				} else {
					$scope.showButtonRefreshASide = true;
					$scope.showRefreshASideLoading = false;
				}
				if ($scope.circuit.productType.indexOf("LANLINK") > -1) {
					if ($scope.sideInformation.zSideInformation) {
						var startTimeZ = new Date().getTime();
						if ($scope.sideInformation.zSideInformation.deviceName) {
							callZSideCount++;
							var deviceDetailsZside = {
									'requestID'	: callZSideCount,
									'seibelUserID'	: username,
									'name'       	: $scope.sideInformation.zSideInformation.deviceName,
									'serviceType' : $scope.circuit.productType,
									'deviceType'	: {
										'vendor'	: $scope.sideInformation.zSideInformation.vendor,
										'model'  	: $scope.sideInformation.zSideInformation.model
									},
									'type': 'LANLink',
									"portName" 	: $scope.sideInformation.zSideInformation.port,
									"ocn" : $scope.circuit.customerOCN,
									'circuitID': $scope.circuit.circuitID,
									'ip': $scope.sideInformation.zSideInformation.ip
							};
							var respAgentZSide = $http({
								method  : 'POST',
								url     : urlWorkFlow + '/getDeviceDetails',
								data    : deviceDetailsZside,
								headers : { 'Content-Type': 'application/json' },
								timeout : conTimeout
							});
							respAgentZSide.success(function(data) {
								var l3DeviceDetails = data;
								if (l3DeviceDetails) {
									if (l3DeviceDetails.responseID == callZSideCount) {
										if (l3DeviceDetails.deviceDetails) {
											$scope.zSideInterfaces = l3DeviceDetails.deviceDetails.interfaces;
											$scope.zSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
											$scope.zSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
										} else {
											$scope.showDeviceErrorZSide = true;
											$scope.deviceMessageZSideError = errorMsg;
										}
										if(l3DeviceDetails.errorResponse) {
											$scope.showDeviceErrorZSide = true;
											$scope.deviceMessageZSideError = l3DeviceDetails.errorResponse.message;
										}
										$scope.zSideManagementIPAddress = l3DeviceDetails.deviceIP;
									}
								} else {
									$scope.showDeviceErrorZSide = true;
									$scope.deviceMessageZSideError = errorMsg;
								}
								$scope.showButtonRefreshZSide = true;
								$scope.showRefreshZSideLoading = false;
							});
							respAgentZSide.error(function(data, status) {
								$scope.showButtonRefreshZSide = true;
								$scope.showRefreshZSideLoading = false;
								$scope.showDeviceErrorZSide = true;
								if (status == 0) {
									var respTime = new Date().getTime() - startTimeZ;
									if(respTime >= conTimeout) {
										errorMsg = "Connection timeout.";
									} else {
										errorMsg = "Connection to server failed.";
									}
								} else {
									errorMsg = "Connection to server failed with status: " + status;
								}
								$scope.deviceMessageZSideError = errorMsg;
							});
						} else {
							$scope.showButtonRefreshZSide = true;
							$scope.showRefreshZSideLoading = false;
						}
					} else {
						$scope.showButtonRefreshZSide = true;
						$scope.showRefreshZSideLoading = false;
					}
				} else {
					if ($scope.sideInformation.zSideInformation) {
						var startTimeZ = new Date().getTime();
						if ($scope.sideInformation.zSideInformation.deviceName) {
							callZSideCount++;
							var deviceDetailsZside = {
									'requestID'	: callZSideCount,
									'seibelUserID'	: username,
									'name'       	:  $scope.sideInformation.zSideInformation.deviceName,
									'serviceType' : $scope.circuit.productType,
									'serviceId'		:$scope.circuit.serviceId,
									'deviceType'	: {
										'vendor'	: $scope.sideInformation.zSideInformation.vendor,
										'model'  	: $scope.sideInformation.zSideInformation.model
									},
									'type': 'PE',
									'circuitID': $scope.circuit.circuitID
							};
							if ($scope.sideInformation.aSideInformation && $scope.sideInformation.aSideInformation.deviceName) {
								deviceDetailsZside.associatedDevice = $scope.sideInformation.aSideInformation.deviceName;
							}
							var respAgentZSide = $http({
								method  : 'POST',
								url     : urlWorkFlow + '/getDeviceDetails',
								data    : deviceDetailsZside,
								headers : { 'Content-Type': 'application/json' },
								timeout : conTimeout
							});
							respAgentZSide.success(function(data) {
								var l3DeviceDetails = data;
								if (l3DeviceDetails) {
									if (l3DeviceDetails.responseID == callZSideCount) {
										if (l3DeviceDetails.os && l3DeviceDetails.os == "erx") {
											$scope.showZEndPhysicalInterface = false;
											if (l3DeviceDetails.deviceName) {
												var routerId = l3DeviceDetails.deviceName;
												var deviceName = "lo0-" + routerId + ".router.colt.net";
												$scope.sideInformation.zSideInformation.xngDeviceName = l3DeviceDetails.deviceName;
												$scope.sideInformation.zSideInformation.deviceName = deviceName;
											}
										}
										$scope.zSideManagementIPAddress = l3DeviceDetails.deviceIP;
										if (l3DeviceDetails.deviceDetails) {
											if (l3DeviceDetails.deviceDetails.interfaces) {
												for (var i = 0; i < l3DeviceDetails.deviceDetails.interfaces.length; i++) {
													if (l3DeviceDetails.deviceDetails.interfaces[i].name.indexOf(".") != -1 || l3DeviceDetails.deviceDetails.interfaces[i].name.indexOf(":") != -1) {
														//logical
														$scope.zSideInterfaceLogicals.push(l3DeviceDetails.deviceDetails.interfaces[i]);
													} else {
														//physical
														$scope.zSidePhysicalInterfaces.push(l3DeviceDetails.deviceDetails.interfaces[i]);
													}
													if (l3DeviceDetails.deviceDetails.interfaces[i].name.indexOf(":") != -1 && l3DeviceDetails.deviceDetails.interfaces[i].name.indexOf(".") == -1) {
														//physical
														$scope.zSidePhysicalInterfaces.push(l3DeviceDetails.deviceDetails.interfaces[i]);	
													}
												}
											}
											$scope.zSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
											$scope.zSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
										} else {
											$scope.showDeviceErrorZSide = true;
											$scope.deviceMessageZSideError = errorMsg;
										}
										if (l3DeviceDetails.errorResponse) {
											$scope.showDeviceErrorZSide = true;
											$scope.deviceMessageZSideError = l3DeviceDetails.errorResponse.message;
										}
										$scope.zSideAssociatedDeviceIp = l3DeviceDetails.associatedDeviceIp;
	
									}
								} else {
									$scope.showDeviceErrorZSide = true;
									$scope.deviceMessageZSideError = errorMsg;
								}
								$scope.showButtonRefreshZSide = true;
								$scope.showRefreshZSideLoading = false;
							});
							respAgentZSide.error(function(data, status) {
								$scope.showButtonRefreshZSide = true;
								$scope.showRefreshZSideLoading = false;
								$scope.showDeviceErrorZSide = true;
								if (status == 0) {
									var respTime = new Date().getTime() - startTimeZ;
									if(respTime >= conTimeout) {
										errorMsg = "Connection timeout.";
									} else {
										errorMsg = "Connection to server failed.";
									}
								} else {
									errorMsg = "Connection to server failed with status: " + status;
								}
								$scope.deviceMessageZSideError = errorMsg;
							});
						} else {
							$scope.showButtonRefreshZSide = true;
							$scope.showRefreshZSideLoading = false;
						}
					} else {
						$scope.showButtonRefreshZSide = true;
						$scope.showRefreshZSideLoading = false;
					}
				}
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
		var errorMsg = "Error receiving device details.";
		if (workFlowAgentUrlBase) {
			var deviceDetails;
			if ($scope.sideInformation) {
				if (type == "aside") {
					$scope.showDeviceErrorASide = false;
					if ($scope.sideInformation.aSideInformation) {
						if ($scope.aSideManagementIPAddress) {
							deviceDetails = new Object();
							deviceDetails.ip = $scope.aSideManagementIPAddress;
						} else if ($scope.sideInformation.aSideInformation.deviceName) {
							deviceDetails = new Object();
							deviceDetails.name = $scope.sideInformation.aSideInformation.deviceName;
						}
						if (deviceDetails) {
							deviceDetails.requestID = callRefreshCount;
							deviceDetails.seibelUserID = username;
							deviceDetails.serviceType = $scope.circuit.productType; 
							deviceDetails.deviceType = new Object();
							deviceDetails.deviceType.vendor = $scope.sideInformation.aSideInformation.vendor;
							deviceDetails.deviceType.model = $scope.sideInformation.aSideInformation.model;
							if ($scope.circuit.productType.indexOf("LANLINK") > -1) {
								deviceDetails.type = 'LANLink';
								deviceDetails.portName = $scope.sideInformation.aSideInformation.port;
								deviceDetails.ocn = $scope.circuit.customerOCN;
							} else {
								deviceDetails.type = 'CPE';
							}
							deviceDetails.circuitID = $scope.circuit.circuitID;
						}
					} else {
						$scope.showRefreshASideLoading = false;
						resetASideOrZSideInformation("aside");
					}
				} else if (type == "zside") {
					$scope.showDeviceErrorZSide = false;
					if ($scope.sideInformation.zSideInformation) {
						if ($scope.zSideManagementIPAddress) {
							deviceDetails = new Object();
							deviceDetails.ip = $scope.zSideManagementIPAddress;
							deviceDetails.name = $scope.sideInformation.zSideInformation.deviceName;
						} else if ($scope.sideInformation.zSideInformation.deviceName) {
							deviceDetails = new Object();
							deviceDetails.name = $scope.sideInformation.zSideInformation.deviceName;
						}
						if (deviceDetails) {
							deviceDetails.requestID = callRefreshCount;
							deviceDetails.seibelUserID = username;
							deviceDetails.serviceType = $scope.circuit.productType;
							deviceDetails.serviceId = $scope.circuit.serviceId;
							deviceDetails.deviceType = new Object();
							deviceDetails.deviceType.vendor = $scope.sideInformation.zSideInformation.vendor;
							deviceDetails.deviceType.model = $scope.sideInformation.zSideInformation.model;
							if ($scope.sideInformation.aSideInformation && $scope.sideInformation.aSideInformation.deviceName) {
								deviceDetails.associatedDevice = $scope.sideInformation.aSideInformation.deviceName;
							}
							if ($scope.zSideAssociatedDeviceIp) {
								deviceDetails.associatedDeviceIp = $scope.zSideAssociatedDeviceIp;
							}
							if ($scope.circuit.productType.indexOf("LANLINK") > -1) {
								deviceDetails.type = 'LANLink';
								deviceDetails.portName = $scope.sideInformation.zSideInformation.port;
								deviceDetails.ocn = $scope.circuit.customerOCN;
							} else {
								deviceDetails.type = 'PE';
							}
							deviceDetails.circuitID = $scope.circuit.circuitID;
						}
					} else {
						$scope.showRefreshZSideLoading = false;
						resetASideOrZSideInformation("zside");
					}
				}
				if (deviceDetails) {
					var startTime = new Date().getTime();
					var respAgent = $http({
						method  : 'POST',
						url     : urlWorkFlow + '/getDeviceDetails',
						data    : deviceDetails,
						headers : { 'Content-Type': 'application/json' },
						timeout : conTimeout
					});
					respAgent.success(function(data) {
						var l3DeviceDetails = data;
						if (l3DeviceDetails) {
							if (l3DeviceDetails.responseID == callRefreshCount) {
								if (type == "aside") {
									resetASideOrZSideInformation("aside");
									$scope.aSideManagementIPAddress = l3DeviceDetails.deviceIP;
								} else {
									resetASideOrZSideInformation("zside");
									if (l3DeviceDetails.os && l3DeviceDetails.os == "erx") {
										$scope.showZEndPhysicalInterface = false;
									} else {
										$scope.showZEndPhysicalInterface = true;
									}
									$scope.zSideManagementIPAddress = l3DeviceDetails.deviceIP;
									$scope.zSideAssociatedDeviceIp = l3DeviceDetails.associatedDeviceIp;
								}
								if (l3DeviceDetails.deviceDetails) {
									if (type == "aside") {
										$scope.aSideInterfaces = l3DeviceDetails.deviceDetails.interfaces;
										$scope.aSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
										$scope.aSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
									} else if (type == "zside") {
										if ($scope.circuit.productType.indexOf("LANLINK") > -1) {
											$scope.zSideInterfaces = l3DeviceDetails.deviceDetails.interfaces;
										} else {
											if (l3DeviceDetails.os && l3DeviceDetails.os == "erx") {
												if (l3DeviceDetails.deviceName) {
													var routerId = l3DeviceDetails.deviceName;
													var deviceName = "lo0-" + routerId + ".router.colt.net";
													$scope.sideInformation.zSideInformation.xngDeviceName = l3DeviceDetails.deviceName;
													$scope.sideInformation.zSideInformation.deviceName = deviceName;
												}
											}
											for (var i = 0; i < l3DeviceDetails.deviceDetails.interfaces.length; i++) {
												if (l3DeviceDetails.deviceDetails.interfaces[i].name.indexOf(".") != -1 || l3DeviceDetails.deviceDetails.interfaces[i].name.indexOf(":") != -1) {
													//logical
													$scope.zSideInterfaceLogicals.push(l3DeviceDetails.deviceDetails.interfaces[i]);
												} else {
													//physical
													$scope.zSidePhysicalInterfaces.push(l3DeviceDetails.deviceDetails.interfaces[i]);
												}
												if (l3DeviceDetails.deviceDetails.interfaces[i].name.indexOf(":") != -1 && l3DeviceDetails.deviceDetails.interfaces[i].name.indexOf(".") == -1) {
													//physical
													$scope.zSidePhysicalInterfaces.push(l3DeviceDetails.deviceDetails.interfaces[i]);	
												}
											}
										}
										$scope.zSideDeviceStatus = l3DeviceDetails.deviceDetails.status;
										$scope.zSideDeviceUpTime = l3DeviceDetails.deviceDetails.time;
									}
								} else {
									if (type == "aside") {
										$scope.showDeviceErrorASide = true;
										$scope.deviceMessageASideError = errorMsg;
									}  else if (type == "zside") {
										$scope.showDeviceErrorZSide = true;
										$scope.deviceMessageZSideError = errorMsg;
									}
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
							}
						} else {
							if (type == "aside") {
								$scope.showDeviceErrorASide = true;
								$scope.deviceMessageASideError = errorMsg;
							}  else if (type == "zside") {
								$scope.showDeviceErrorZSide = true;
								$scope.deviceMessageZSideError = errorMsg;
							}
						}
						hideLoadingRefresh(type);
					});
					respAgent.error(function(data, status) {
						if (type == "aside") {
							$scope.showButtonRefreshASide = true;
							$scope.showRefreshASideLoading = false;
							$scope.showDeviceErrorASide = true;
						} else {
							$scope.showButtonRefreshZSide = true;
							$scope.showRefreshZSideLoading = false;
							$scope.showDeviceErrorZSide = true;
						}
						if (status == 0) {
							var respTime = new Date().getTime() - startTime;
							if(respTime >= conTimeout) {
								errorMsg = "Connection timeout.";
							} else {
								errorMsg = "Connection to server failed.";
							}
						} else {
							errorMsg = "Connection to server failed with status: " + status;
						}
						$scope.deviceMessageZSideError = errorMsg;
					});
				} else {
					hideLoadingRefresh(type);
				}
			}
		} else {
			hideLoadingRefresh(type);
		}
	}

	function hideLoadingRefresh(type) {
		if (type == "aside") {
			$scope.showRefreshASideLoading = false;
		}  else if (type == "zside") {
			$scope.showRefreshZSideLoading = false;
		}
	}
	function resetASideOrZSideInformation(type) {
		if (type == "aside") {
			$scope.aSideInterfaces = "";
			$scope.aSideDeviceStatus = "";
			$scope.aSideDeviceUpTime = "";
			$scope.aSideManagementIPAddress = "";
			
		} else if (type == "zside") {
			$scope.zSideInterfaceLogicals = [];
			$scope.zSideInterfaces = "";
			$scope.zSidePhysicalInterfaces = [];
			$scope.zSideDeviceStatus = "";
			$scope.zSideDeviceUpTime = "";
			$scope.zSideManagementIPAddress	= "";
			$scope.zSideAssociatedDeviceIp = "";
		}
	}
	function resetSideInformation() {
		$scope.sideInformation = "";
		$scope.aSideInterfaces = "";
		$scope.aSideDeviceStatus = "";
		$scope.aSideDeviceUpTime = "";
		$scope.aSideManagementIPAddress = "";
		$scope.zSideInterfaceLogicals = [];
		$scope.zSideInterfaces = "";
		$scope.zSidePhysicalInterfaces = [];
		$scope.zSideDeviceStatus = "";
		$scope.zSideDeviceUpTime = "";
		$scope.zSideManagementIPAddress	= "";
	}
};
angular.module('sstApp').controller('ServiceDataController',ServiceDataController);
