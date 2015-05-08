<%
response.setHeader("X-UA-Compatible","IE=Edge");
%><!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!--[if lt IE 7 ]> <html lang="en" class="no-js lt-ie10 lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js lt-ie10 lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js lt-ie10 lt-ie9"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js lt-ie10"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"> <!--<![endif]-->
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, minimal-ui">
		<title><spring:message code="index.title"></spring:message></title>
		<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/css/default.css' />
		<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/css/rwd-table.min.css?v=5.0.4' />
		<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/css/bootstrap.min.css' />
		<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
		<!-- respond, support of min/max-width for IE 6-8 -->
		<!--[if lt IE 9]>
			<script src="<%=request.getContextPath()%>/js/html5shiv.js"></script>
			<script src="<%=request.getContextPath()%>/js/respond.js"></script>
		<![endif]-->
		<script src="<%=request.getContextPath()%>/js/jquery-1.11.2.min.js"></script>
		<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
		<script src="<%=request.getContextPath()%>/js/angular.js"></script>
		<script src="<%=request.getContextPath()%>/js/angular-route.js"></script>
		<script src="<%=request.getContextPath()%>/js/rwd-table.js"></script>

		<script>
			var contextPath = "<%=request.getContextPath()%>";
			var searchScopeBak = {};
			var username = "";
			<% String username = request.getParameter("username");
				if (username != null && !"".equals(username)) {
			%>
				username = "<%=URLEncoder.encode(username, "UTF-8")%>";
			<% } %>
		</script>
		<script src="<%=request.getContextPath()%>/app.js"></script>
		<script src="<%=request.getContextPath()%>/controllers/SearchController.js"></script>
		<script src="<%=request.getContextPath()%>/controllers/ServiceDataController.js"></script>
		<script src="<%=request.getContextPath()%>/controllers/feedbackController.js"></script>
	</head>
	<body id="ng-app" ng-app="sstApp">
	<script>
	var feedbackshow = false;
	function showHide() {
		var elem = document.getElementById("sugestion");
		if (feedbackshow) {
			$("#sugestion").slideUp('slow');
		} else {
			elem.style.display = '';
			$("#sugestion").hide().slideDown('slow');
		}
		feedbackshow = !feedbackshow;
	}
	</script>
		<div class="container">
			<div ng-controller="feedbackController">
			<div class="row clearfix">
				<div class="col-md-6 column">
					<img src="<%=request.getContextPath()%>/images/layout_logo.gif" alt=""/> 
				</div>
				<div class="col-md-6 column">
					<h5 style="text-align: right">
						<b><spring:message code="index.app"></spring:message></b><br>
						<button class="btn btn-primary" onclick="showHide()"><spring:message code="global.feedback"></spring:message></button><br>
						<b id="feedbackMessage" style="display:none">{{feedbackMessage}}</b>
					</h5>
				</div>
			</div>
			<div style="margin-top:10px" class="feedbackPop">
				<div id="sugestion" style="display:none;border:5px solid #ccc;border-radius:5px">
					<div style="background-color:#ccc;border-bottom:1px solid #aaa;padding:0px 0px 3px 10px;font-weight:bold">
						<spring:message code="global.message.developer.team"></spring:message>
						<div style="float:right;padding-right:10px"><a href="#" onclick="showHide();return false;" style="text-decoration: none">X</a></div>
					</div>
					<form class="form-horizontal" style="padding:10px">
						<textarea style="resize: none;width: 100%;" ng-model="sugestion" ng-maxlength="1000" ng-trim="true" rows="4" cols="50"></textarea>
						<br/><button class="btn btn-primary" style="margin-top:5px" ng-click="doFeedback()"><spring:message code="global.send"></spring:message></button>
					</form>
				</div>
			</div>
			</div>
			<div id="ng-view" ng-view></div>
			<div id="footer" class="clear">
				&#169;<spring:message code="index.footer"></spring:message> <br/>
				<a href="javascript:;"><spring:message code="index.accessibility"></spring:message></a> | <a href="javascript:;"><spring:message code="index.termsOfUse"></spring:message></a>
			</div>
		</div>
	</body>
</html>