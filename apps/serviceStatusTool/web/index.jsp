<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!--[if lt IE 7 ]> <html lang="en" class="no-js lt-ie10 lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js lt-ie10 lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js lt-ie10 lt-ie9"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js lt-ie10"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"> <!--<![endif]-->
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, minimal-ui">
		<title>Service Status Tool</title>
		<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/css/default.css' />
		<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/css/rwd-table.min.css?v=5.0.4' />
		<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/css/bootstrap.min.css' />
		<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
		<!--[if lt IE 9]>
		    <script src="<%=request.getContextPath()%>/js/html5shiv.js"></script>
		<![endif]-->
		<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
		<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
		<script src="<%=request.getContextPath()%>/js/angular.js"></script>
		<script src="<%=request.getContextPath()%>/js/angular-route.js"></script>
		<script src="<%=request.getContextPath()%>/js/rwd-table.js"></script>

		<script>
			var contextPath = "<%=request.getContextPath()%>";
		</script>
		<script src="<%=request.getContextPath()%>/app.js"></script>
		<script src="<%=request.getContextPath()%>/controllers/SearchController.js"></script>
		<script src="<%=request.getContextPath()%>/controllers/ServiceDataController.js"></script>
	</head>
	<body id="ng-app" ng-app="sstApp">
		<div class="container">
			<div class="row clearfix">
				<div class="col-md-2 column">
					<img src="<%=request.getContextPath()%>/images/layout_logo.gif"  alt=""/> 
				</div>
				<div class="col-md-8 column">
				</div>
				<div class="col-md-2 column">
					<h5 class="text-right">
						<b>TCS Service Status Tool</b>
					</h5>
				</div>
			</div>
			<div id="ng-view" ng-view></div>
			<div id="footer">
				&#169; 2015 Colt Technology Services Group Limited. The Colt name and logos are trade marks. All rights reserved <br/>
				<a href="javascript:;">Accessibility</a> | <a href="javascript:;">Terms of Use &amp; Privacy Statement</a>
			</div>
		</div>
	</body>
</html>