var SearchController = function ($scope, $http){

	var urlBase="/serviceStatusTool/ws";
	$scope.doSearch = function doSearch() {
		if(!($scope.customer != "" && ($scope.order == "" || $scope.service == "") &&
				( ($scope.address != "" && $scope.city != "") || ($scope.address2 != "" && $scope.city2 != "")) || (($scope.address != "" && $scope.city != "") && ($scope.address2 != "" && $scope.city2 != "") ) )  ) {
			alert("Erro customer");
		} else if(($scope.order != "" &&  $scope.service != "") && $scope.customer == "" &&  $scope.address == "" && $scope.city == "" && $scope.address2 == "" && $scope.city2 == "" ){
			alert("Erro Order e Service");
		} else {
			 $http({
			  method  : 'POST',
			  url     : urlBase + '/getCircuits',
			  data    : JSON.stringify($scope.search), 
			  headers : { 'Content-Type': 'application/json' }
			 })
			.success(function(data) {
				$scope.circuits = data;
				$scope.isHidden = true;
			});
		}
	};
};

angular.module('sstApp').controller('SearchController',SearchController);