var SearchController = function ($scope, $http){

	var urlBase= contextPath + "/ws";
	$scope.doSearch = function doSearch() {
		
		if( ($scope.search == undefined)) {
			$scope.error = true;
			$scope.messageError = "Please fill in the required fields OHS Order/Service or Customer/Site Address/Site City";
		}
		else  // service and order <> null
			if(
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
				$scope.messageError = "Please fill only OHS Order and Service.";
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
						var result = "Please fill only ";
						if ($scope.search.order != "" &&  $scope.search.order != undefined ) {
							result += "OHS Order or Customer and Site Address and Site City.";
						} else if ($scope.search.service != "" &&  $scope.search.service != undefined) {
							result += "Service or Customer and Site Address and Site City.";
						}
						$scope.error = true;
						$scope.messageError = result;
		} else if ($scope.search.city != undefined && $scope.search.city != "") {
			if ( (
					(
						($scope.search.customer == "" || $scope.search.customer == undefined) &&  
						($scope.search.address == "" || $scope.search.address == undefined)
					 ) ||
					 (
						($scope.search.customer != "" || $scope.search.customer != undefined) && 
						($scope.search.address == "" || $scope.search.address == undefined)
					 ) ||
					 (
						($scope.search.customer == "" || $scope.search.customer == undefined) && 
						($scope.search.address != "" || $scope.search.address != undefined)
					 )
				 )
			) {	
				alert("city1");
			}
			if ((
					(
						($scope.search.customer != "" && $scope.search.customer != undefined) &&  
						($scope.search.address != "" && $scope.search.address != undefined)
					)
				 ) && 
				 (
					 ($scope.search.city2 != "" && $scope.search.city2 != undefined) || 
					 ($scope.search.address2 != "" && $scope.search.address2 != undefined) ||
					 ($scope.search.order != "" && $scope.search.order != undefined) ||
					 ($scope.search.service != "" && $scope.search.service != undefined)
				 )
				) {
				alert("aaaaaaaaa");
			}
		} else {
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