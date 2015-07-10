function findField() {
	var serviceElem = document.getElementById('Service');
	var orderElem = document.getElementById('Order');
	var customerElem = document.getElementById('customer');
	var addressElem = document.getElementById('address');
	var cityElem = document.getElementById('city');
	var address2Elem = document.getElementById('address2');
	var city2Elem = document.getElementById('city2');
	var param = null;
	if(serviceElem.value) {
		param = serviceElem;
	} else if(orderElem.value) {
		param = orderElem;
	} else if(customerElem.value) {
		param = customerElem;
	} else if(addressElem.value) {
		param = addressElem;
	} else if(cityElem.value) {
		param = cityElem;
	} else if(address2Elem.value) {
		param = address2Elem;
	} else if(city2Elem.value) {
		param = city2Elem;
	}
	return param;
}

function disableFields(elem) {
	var elemWithValeu = findField();
	if(elemWithValeu != null && !(elem === elemWithValeu)) {
		elem = elemWithValeu;
	}
	var serviceElem = document.getElementById('Service');
	var orderElem = document.getElementById('Order');
	var customerElem = document.getElementById('customer');
	var addressElem = document.getElementById('address');
	var cityElem = document.getElementById('city');
	var address2Elem = document.getElementById('address2');
	var city2Elem = document.getElementById('city2');

	if((elem === serviceElem || elem === orderElem) && elem.value == "") {
		if(elem === serviceElem) {
			if (orderElem != undefined && orderElem.disabled == true) {
				orderElem.disabled = false;
			}
		}

		if(elem === orderElem) {
			if (serviceElem != undefined && serviceElem.disabled == true) {
				serviceElem.disabled = false;
			}
		}

		if (customerElem != undefined && customerElem.disabled == true) {
			customerElem.disabled = false;
		}

		if (addressElem != undefined && addressElem.disabled == true) {
			addressElem.disabled = false;
		}

		if (cityElem != undefined && cityElem.disabled == true) {
			cityElem.disabled = false;
		}

		if (address2Elem != undefined && address2Elem.disabled == true) {
			address2Elem.disabled = false;
		}

		if (city2Elem != undefined && city2Elem.disabled == true) {
			city2Elem.disabled = false;
		}
	}
	if((elem === customerElem || elem === addressElem || elem === cityElem || elem === address2Elem || elem === city2Elem) 
			&& customerElem.value == "" && addressElem.value == "" && cityElem.value == "" && address2Elem.value == "" && city2Elem.value == "") {
		if (serviceElem != undefined && serviceElem.disabled == true) {
			serviceElem.disabled = false;
		}
	
		if (orderElem != undefined && orderElem.disabled == true) {
			orderElem.disabled = false;
		}
	}

	if((elem === serviceElem || elem === orderElem) && elem.value != "") {
		if(elem === serviceElem) {
			if (orderElem != undefined && orderElem.disabled == false) {
				orderElem.disabled = true;
			}
		}

		if(elem === orderElem) {
			if (serviceElem != undefined && serviceElem.disabled == false) {
				serviceElem.disabled = true;
			}
		}

		if (customerElem != undefined && customerElem.disabled == false) {
			customerElem.disabled = true;
		}

		if (addressElem != undefined && addressElem.disabled == false) {
			addressElem.disabled = true;
		}

		if (cityElem != undefined && cityElem.disabled == false) {
			cityElem.disabled = true;
		}

		if (address2Elem != undefined && address2Elem.disabled == false) {
			address2Elem.disabled = true;
		}

		if (city2Elem != undefined && city2Elem.disabled == false) {
			city2Elem.disabled = true;
		}
	}

	if((elem === customerElem || elem === addressElem || elem === cityElem || elem === address2Elem || elem === city2Elem) && elem.value != "") {
		if (serviceElem != undefined && serviceElem.disabled == false) {
			serviceElem.disabled = true;
		}

		if (orderElem != undefined && orderElem.disabled == false) {
			orderElem.disabled = true;
		}
	}
}

var SearchController = function ($scope, $http){

	var urlBase= contextPath + "/ws";
	if (searchScopeBak.circuits) {
		$scope.circuits = searchScopeBak.circuits;
		$scope.search = searchScopeBak.search;
		$scope.showResult = true;
		$scope.isHidden = true;
	}
	$scope.doSearch = function doSearch() {
		$scope.error = false;
		$scope.msg = false;
		$scope.showResult = false;
		$scope.showLoading = true;
		var msg = "Incorrect Combination. Please, enter the Customer Name and Site Address/City or independent criteria service or order.";
		if( ($scope.search == undefined)) {
			$scope.error = true;
			$scope.messageError = msg;
		// service and order <> null
		} else if(($scope.search.service != "" && $scope.search.service != undefined) &&
				($scope.search.order != "" && $scope.search.order != undefined)) {

				$scope.error = true;
				$scope.messageError = msg;
		} else if (
					!(
						($scope.search.service != "" &&  $scope.search.service != undefined) &&
						($scope.search.order != "" && $scope.search.order != undefined)
					)
					&&
					(
						(
							($scope.search.service != "" &&  $scope.search.service != undefined) && 
							( 
							  ($scope.search.order != undefined && $scope.search.order != "") ||
							  ($scope.search.address != undefined && $scope.search.address != "") ||  
							  ($scope.search.city != undefined && $scope.search.city != "") || 
							  ($scope.search.address2 != undefined && $scope.search.address2 != "") ||
							  ($scope.search.city2 != undefined && $scope.search.city2 != "") ||
							  ($scope.search.customer != undefined && $scope.search.customer != "")
						    )
						 ) ||
						 //order not null
						 ( ($scope.search.order != "" &&  $scope.search.order != undefined ) && 
							( 
							  ($scope.search.service != undefined && $scope.search.service != "") ||
							  ($scope.search.address != undefined && $scope.search.address != "") ||  
							  ($scope.search.city != undefined && $scope.search.city != "") || 
							  ($scope.search.address2 != undefined && $scope.search.address2 != "") || 
							  ($scope.search.city2 != undefined && $scope.search.city2 != "") ||
							  ($scope.search.customer != undefined && $scope.search.customer != "")
							 )
						 )
					) 
					) {
						$scope.error = true;
						$scope.messageError = msg;
		} else if ( ($scope.search.service == "" ||  $scope.search.service == undefined) && ($scope.search.order == "" ||  $scope.search.order == undefined) ) {
			if($scope.search.customer == undefined || $scope.search.customer == "") {
				$scope.error = true;
				$scope.messageError = msg;
			//A1 && C1 && A2 && C2 is empty
			} else if( ( (($scope.search.address == undefined || $scope.search.address == "") && ($scope.search.city == undefined || $scope.search.city == "")) && 
						(($scope.search.address2 == undefined || $scope.search.address2 == "") && ($scope.search.city2 == undefined || $scope.search.city2 == "")) ) ) {
				$scope.error = true;
				$scope.messageError = msg;
			}
		}

		if(!$scope.error) {
			$scope.isHidden = true;
			var resp = $http({
			  method  : 'POST',
			  url     : urlBase + '/getCircuits?username=' + username,
			  data    : $scope.search, 
			  headers : { 'Content-Type': 'application/json' }
			 });
			resp.success(function(data) {
				$scope.error = false;
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
					$scope.circuits = data.result;
					$scope.showResult = true;
				}
				searchScopeBak.circuits = $scope.circuits;
				searchScopeBak.search = $scope.search;
			});
			resp.error(function(data, status) {
				var errorMsg = getErrorMsg(status);
				$scope.error = true;
				$scope.messageError = errorMsg;
			});
		} else {
			$scope.isHidden = false;
			searchScopeBak.circuits = null;
		}
	};
};

function getErrorMsg(status) {
	var errorMsg;
	if (status == 0) {
		 errorMsg = "Connection to server failed.";
	} else {
		errorMsg = "Connection to server failed with status: " + status;
	}
	return errorMsg;
}

angular.module('sstApp').controller('SearchController',SearchController);