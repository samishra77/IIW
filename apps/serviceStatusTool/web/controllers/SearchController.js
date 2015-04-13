var SearchController = function ($scope, $http){

	var urlBase= contextPath + "/ws";
	$scope.doSearch = function doSearch() {
		if(!($scope.customer != "" && ($scope.order == "" || $scope.service == "") &&
				( ($scope.address != "" && $scope.city != "") || ($scope.address2 != "" && $scope.city2 != "")) || (($scope.address != "" && $scope.city != "") && ($scope.address2 != "" && $scope.city2 != "") ) )  ) {
			alert("Erro customer");
		} else if(($scope.order != "" &&  $scope.service != "") && $scope.customer == "" &&  $scope.address == "" && $scope.city == "" && $scope.address2 == "" && $scope.city2 == "" ){
			alert("Erro Order e Service");
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