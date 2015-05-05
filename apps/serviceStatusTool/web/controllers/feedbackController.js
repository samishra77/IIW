function feedbackController ($scope, $http){

	var urlBase= contextPath + "/ws";
	$scope.doFeedback = function doFeedback() {
		var resp = $http({
		  method  : 'POST',
		  url     : urlBase + '/doFeedback?username=' + username,
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
			feedbackshow = !feedbackshow;
			$("#sugestion").slideUp('slow');

			$("#feedbackMessage").show();
			$("#feedbackMessage").fadeOut(7000);
		});
	}
};