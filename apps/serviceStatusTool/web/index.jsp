<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Service Status Tool</title>
		<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/css/default.css' />
		<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/css/default-layout.css' />
		<script src="<%=request.getContextPath()%>/js/angular.js"></script>
		<script src="<%=request.getContextPath()%>/js/angular-route.js"></script>
		<script>
			var contextPath = "<%=request.getContextPath()%>";
		</script>
		<script src="<%=request.getContextPath()%>/app.js"></script>
		<script src="<%=request.getContextPath()%>/controllers/SearchController.js"></script>
		<script src="<%=request.getContextPath()%>/controllers/ServiceDataController.js"></script>
	</head>
	<body id="ng-app" ng-app="sstApp">
		<table cellspacing="0" cellpadding="5" align="center" width="980" border="0">
			<tr>
				<td>
					<div>
						<table border="0" cellpadding="0" cellspacing="0" width="100%">
							<tr>
								<td valign="top" rowspan="2">
									<img src="<%=request.getContextPath()%>/images/layout_logo.gif"  alt=""/> 
								</td>
								<td valign="top" align="right">
									<img src="<%=request.getContextPath()%>/images/title.jpg"  alt=""/>
								</td>
							</tr>
							<tr>
								<td align="right">
									<div class="appmenu">
									</div>
								</td>
							</tr>
							<tr>
								<td colspan="2" align="right">
									<img src="<%=request.getContextPath()%>/images/Colt_lightbar.jpg"  alt=""/>
								</td>
							</tr>
						</table>
					</div>
					<div class="marginTop" id="ng-view" ng-view></div>
					<div id="footer">
						&#169; 2015 Colt Technology Services Group Limited. The Colt name and logos are trade marks. All rights reserved <br/>
						<a href="javascript:;">Accessibility</a> | <a href="javascript:;">Terms of Use &amp; Privacy Statement</a>
					</div>
				</td>
			</tr>
		</table>
	</body>
</html>