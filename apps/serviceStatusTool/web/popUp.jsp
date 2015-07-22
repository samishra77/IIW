<%
response.setHeader("X-UA-Compatible","IE=Edge");
%><!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.colt.util.SstConfig" %>
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
			<% } else { %>
				window.location.href = "<%=SstConfig.getDefaultInstance().getProperty("siebel.url.login.page")%>";
			<%}%>
		</script>
		<script src="<%=request.getContextPath()%>/app.js"></script>
		<script src="<%=request.getContextPath()%>/controllers/ServiceDataController.js"></script>
	</head>
	<body id="ng-app" ng-app="sstApp">
		<div class="container">
			<div id="ng-view" ng-view></div>
		</div>
	</body>
</html>