var SearchController = function ($scope, $http){

	var urlBase= contextPath + "/ws";
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
			  url     : urlBase + '/getCircuits',
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
				
			});
		} else {
			$scope.isHidden = false;
		}
	};
};

angular.module('sstApp').controller('SearchController',SearchController);