function feedbackController ($scope, $http){

	var urlBase= contextPath + "/ws";
	$scope.doFeedback = function doFeedback() {
		var resp = $http({
		  method  : 'POST',
		  url     : urlBase + '/doFeedback',
		  data    : $scope.sugestion, 
		  headers : { 'Content-Type': 'application/json' }
		 });
		resp.success(function(data) {
			$scope.msgFeedback = true;
			$scope.feedbackMessage="";
			$scope.error = false;
			if(data.status == 'fail') {
				$scope.feedbackMessage = "Error: " + data.errorMsg;
			} else {
				$scope.feedbackMessage="Successfully submitted.";
			}
			$scope.sugestion='';
			var elem = document.getElementById("sugestion");
			elem.style.display = 'none';

			$("#feedbackMessage").show();
			$("#feedbackMessage").fadeOut(7000);
		});
	}
};