var SearchController = function ($scope, $http){

	var urlBase="/serviceStatusTool/ws";
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
			  data    : JSON.stringify($scope.search), 
			  headers : { 'Content-Type': 'application/json' }
			 });
			resp.success(function(data) {
				if(data.status == 'fail') {
					$scope.msg = true;
					$scope.message = data.errorMsg;
				} else {
					$scope.circuits = data.result;
					$scope.isHidden = true;
				}
			});
			resp.error(function(data, status, headers, config) {
				alert( "failure message: " + JSON.stringify({config: config}));
			});
		}
	};
};

angular.module('sstApp').controller('SearchController',SearchController);