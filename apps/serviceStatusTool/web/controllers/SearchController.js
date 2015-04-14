var SearchController = function ($scope, $http){

	var urlBase= contextPath + "/ws";
	$scope.doSearch = function doSearch() {
		$scope.error = false;
		if( ($scope.search == undefined)) {
			$scope.error = true;
			$scope.messageError = "OHS Order/Service or Customer/Site Address/Site City is required";
		// service and order <> null
		} else if(
				((($scope.search.service != "" &&  $scope.search.service != undefined)  && 
				  ($scope.search.order != "" && $scope.search.order != undefined)) &&
					(($scope.search.address != undefined && $scope.search.address != "") ||  
					  ($scope.search.city != undefined && $scope.search.city != "") || 
					  ($scope.search.address2 != undefined && $scope.search.address2 != "") || 
					  ($scope.search.city2 != undefined && $scope.search.city2 != "") ||
					  ($scope.search.customer != undefined && $scope.search.customer != "")
					 )
				 ) ) {
				
				$scope.error = true;
				$scope.messageError = "Only OHS Order or Service is required ";
		} else if (	
					!(
						($scope.search.service != "" &&  $scope.search.service != undefined)  &&
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
						var result = "Only ";
						if ($scope.search.order != "" &&  $scope.search.order != undefined ) {
							result += "OHS Order or Customer and Site Address and Site City is required.";
						} else if ($scope.search.service != "" &&  $scope.search.service != undefined) {
							result += "Service or Customer and Site Address and Site City is required.";
						}
						$scope.error = true;
						$scope.messageError = result;
		} else if ( ($scope.search.service == "" ||  $scope.search.service == undefined) && ($scope.search.order == "" ||  $scope.search.order == undefined) ) {
			var msg = "Site1 Address/Site1 City or Site2 Address/Site2 City is required";
			if($scope.search.customer == undefined || $scope.search.customer == "") {
				$scope.error = true;
				$scope.messageError = "Customer is required";
			//C1
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.address != undefined && $scope.search.address != "" && 
						($scope.search.city == undefined || $scope.search.city == "") && 
						($scope.search.address2 == undefined || $scope.search.address2 == "") && ($scope.search.city2 == undefined || $scope.search.city2 == "") ) {
					$scope.error = true;
					$scope.messageError = "Site1 City  is required";
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.address != undefined && $scope.search.address != "" && 
					($scope.search.city == undefined || $scope.search.city == "") && 
					($scope.search.address2 != undefined && $scope.search.address2 != "") && ($scope.search.city2 == undefined || $scope.search.city2 == "") ) {
				$scope.error = true;
				$scope.messageError = msg;
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.address != undefined && $scope.search.address != "" && 
						($scope.search.city == undefined || $scope.search.city == "") && 
						($scope.search.address2 == undefined || $scope.search.address2 == "") && ($scope.search.city2 != undefined && $scope.search.city2 != "") ) {
					$scope.error = true;
					$scope.messageError = msg;
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.address != undefined && $scope.search.address != "" && 
					($scope.search.city == undefined || $scope.search.city == "") && 
					($scope.search.address2 != undefined && $scope.search.address2 != "") && ($scope.search.city2 != undefined && $scope.search.city2 != "") ) {
				$scope.error = true;
				$scope.messageError = msg;
			//A1	
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.city != undefined && $scope.search.city != "" && 
					($scope.search.address == undefined || $scope.search.address == "") && 
					($scope.search.address2 == undefined || $scope.search.address2 == "") && ($scope.search.city2 == undefined || $scope.search.city2 == "") ) {
				$scope.error = true;
				$scope.messageError = "Site1 Address is required";
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.city != undefined && $scope.search.city != "" && 
					($scope.search.address == undefined || $scope.search.address == "") && 
					($scope.search.address2 != undefined && $scope.search.address2 != "") && ($scope.search.city2 == undefined || $scope.search.city2 == "") ) {
				$scope.error = true;
				$scope.messageError = msg;
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.city != undefined && $scope.search.city != "" && 
						($scope.search.address == undefined || $scope.search.address == "") && 
						($scope.search.address2 == undefined || $scope.search.address2 == "") && ($scope.search.city2 != undefined && $scope.search.city2 != "") ) {
					$scope.error = true;
					$scope.messageError = msg;
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.city != undefined && $scope.search.city != "" && 
					($scope.search.address == undefined || $scope.search.address == "") && 
					($scope.search.address2 != undefined && $scope.search.address2 != "") && ($scope.search.city2 != undefined && $scope.search.city2 != "") ) {
			$scope.error = true;
			$scope.messageError = msg;
			//C2
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.address2 != undefined && $scope.search.address2 != "" && 
						($scope.search.city2 == undefined || $scope.search.city2 == "") && 
						($scope.search.address == undefined || $scope.search.address == "") && ($scope.search.city == undefined || $scope.search.city == "") ) {
					$scope.error = true;
					$scope.messageError = "Site2 City  is required";
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.address2 != undefined && $scope.search.address2 != "" && 
					($scope.search.city2 == undefined || $scope.search.city2 == "") && 
					($scope.search.address != undefined && $scope.search.address != "") && ($scope.search.city == undefined || $scope.search.city == "") ) {
				$scope.error = true;
				$scope.messageError = msg;
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.address2 != undefined && $scope.search.address2 != "" && 
						($scope.search.city2 == undefined || $scope.search.city2 == "") && 
						($scope.search.address == undefined || $scope.search.address == "") && ($scope.search.city != undefined && $scope.search.city != "") ) {
					$scope.error = true;
					$scope.messageError = msg;
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.address2 != undefined && $scope.search.address2 != "" && 
					($scope.search.city2 == undefined || $scope.search.city2 == "") && 
					($scope.search.address != undefined && $scope.search.address != "") && ($scope.search.city != undefined && $scope.search.city != "") ) {
				$scope.error = true;
				$scope.messageError = msg;
			//A2
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.city2 != undefined && $scope.search.city2 != "" && 
					($scope.search.address2 == undefined || $scope.search.address2 == "") && 
					($scope.search.address == undefined || $scope.search.address == "") && ($scope.search.city == undefined || $scope.search.city == "") ) {
				$scope.error = true;
				$scope.messageError = "Site2 Address is required";
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.city2 != undefined && $scope.search.city2 != "" && 
					($scope.search.address2 == undefined || $scope.search.address2 == "") && 
					($scope.search.address != undefined && $scope.search.address != "") && ($scope.search.city == undefined || $scope.search.city == "") ) {
				$scope.error = true;
				$scope.messageError = msg;
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.city2 != undefined && $scope.search.city2 != "" && 
						($scope.search.address2 == undefined || $scope.search.address2 == "") && 
						($scope.search.address == undefined || $scope.search.address == "") && ($scope.search.city != undefined && $scope.search.city != "") ) {
					$scope.error = true;
					$scope.messageError = msg;
			} else if( $scope.search.customer != undefined && $scope.search.customer != "" && $scope.search.city2 != undefined && $scope.search.city2 != "" && 
					($scope.search.address2 == undefined || $scope.search.address2 == "") && 
					($scope.search.address != undefined && $scope.search.address != "") && ($scope.search.city != undefined && $scope.search.city != "") ) {
			$scope.error = true;
			$scope.messageError = msg;
			} else if( $scope.search.address != undefined && $scope.search.address != "" && $scope.search.city != undefined && $scope.search.city != "" && 
						$scope.search.address2 != undefined && $scope.search.address2 != "" && $scope.search.city2 != undefined && $scope.search.city2 != "" ) {
					$scope.error = true;
					$scope.messageError = msg;
			} else if( ( (($scope.search.address == undefined || $scope.search.address == "") && ($scope.search.city == undefined || $scope.search.city == "")) && 
						(($scope.search.address2 == undefined || $scope.search.address2 == "") && ($scope.search.city2 == undefined || $scope.search.city2 == "")) ) ) {
				$scope.error = true;
				$scope.messageError = msg;
			}
		}
		
		if(!$scope.error) {
			var resp = $http({
			  method  : 'POST',
			  url     : urlBase + '/getCircuits',
			  data    : $scope.search, 
			  headers : { 'Content-Type': 'application/json' }
			 });
			resp.success(function(data) {
				$scope.isHidden = true;
				$scope.msg = false;
				$scope.error = false;
				if(data.status == 'fail') {
					if(data.errorCode == '2') {
						$scope.error = true;
						$scope.messageError = data.errorMsg;
					} else {
						$scope.msg = true;
						$scope.message = data.errorMsg;
					}
				} else {
					$scope.circuits = data.result;
				}
			});
		}
	};
};

angular.module('sstApp').controller('SearchController',SearchController);