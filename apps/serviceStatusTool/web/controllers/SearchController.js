var SearchController = function ($scope, $http){

	var urlBase="http://localhost:8080/serviceStatusTool/ws";
	
	$scope.isHidden = false;  
	$scope.teste = function() {
		$scope.isHidden = true;
		$http.get(urlBase+'/circuits')
		.success(function(data) {
			$scope.circuits = data;
		});
	};

	$scope.search = function search() {
		if(!($scope.customer != "" && ($scope.order == "" || $scope.service == "") &&
				( ($scope.address != "" && $scope.city != "") || ($scope.address2 != "" && $scope.city2 != "")) || (($scope.address != "" && $scope.city != "") && ($scope.address2 != "" && $scope.city2 != "") ) )  ) {
			alert("Erro customer");
		} else if(($scope.order != "" &&  $scope.service != "") && $scope.customer == "" &&  $scope.address == "" && $scope.city == "" && $scope.address2 == "" && $scope.city2 == "" ){
			alert("Erro Order e Service");
		} else {
			
			formData.nome
//			if($scope.customer != null && $scope.customer != "") {
//				param+= 'customer=' + $scope.customer + ';';
//			}
//
//			if($scope.order != null && $scope.order != "") {
//				param+= 'order=' + $scope.order + ';';
//			}
//
//			if($scope.service != null && $scope.service != "") {
//				param+= 'service=' + $scope.service + ';';
//			}
//
//			if($scope.address != null && $scope.address != "") {
//				param+= 'address=' + $scope.address + ';';
//			}
//
//			if($scope.city != null && $scope.city != "") {
//				param+= 'city=' + $scope.city + ';';
//			}
//
//			if($scope.address2 != null && $scope.address2 != "") {
//				param+= 'address2=' + $scope.address2 + ';';
//			}
//
//			if($scope.city2 != null && $scope.city2 != "") {
//				param+= 'city2=' + $scope.city2 + ';';
//			}

			
			$http.post(urlBase + '/getCircuits/'+Jsto.asdf(formData)).
			success(function(data) {
				$scope.circuits = data;
			});
		}
	};
};

angular.module('sstApp').controller('SearchController',SearchController);