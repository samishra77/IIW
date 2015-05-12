<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<div class="row clearfix" style="margin-top:10px;">
	<div class="col-md-3 column">
		<div class="tabbable font-smaller" id="tabs-219456">
			<a href="#"><spring:message code="trail.home"></spring:message></a> &gt; <spring:message code="trail.search"></spring:message>
		</div>
	</div>
</div>
<div class="row clearfix marginTop">
	<div class="col-md-12 column">
		<label class="font-medium"><spring:message code="searchForm.usageinfo"></spring:message></label>
	</div>
</div>

<div class="row clearfix marginTop">
	<div class="col-md-4 column">
		<h2 class="app-title"><spring:message code="searchForm.subTitle"></spring:message></h2>
	</div>
</div>

<div class="row clearfix">
		<div class="col-md-12 column">
		<form class="form-horizontal" role="form">
			<br/>
			<div id="msg" class="message messageInfo" ng-if="error">{{messageError}}</div>
			<label class="font-smaller"><spring:message code="searchForm.wildcard"></spring:message></label>
			<div class="table-responsive" data-pattern="priority-columns">
				<table class="table table-small-font">
					<tbody>
						<tr>
							<td><b><spring:message code="searchForm.ohsOrder"></spring:message>:<input type="text" ng-model="search.order" id="Order" class="form-control"/></b></td>
							<td><b><spring:message code="searchForm.service"></spring:message>:<input type="text" ng-model="search.service" id="Service" class="form-control" /></b></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="table-responsive" data-pattern="priority-columns">
				<table class="table table-small-font">
					<tbody>
						<tr>	
							<td><b><spring:message code="searchForm.customer"></spring:message>:<input type="text" ng-model="search.customer" id="customer" class="form-control" /></b></td>
							<td></td>
						</tr>
						<tr>
							<td><b><spring:message code="searchForm.site1Address"></spring:message>:<input type="text" ng-model="search.address"  class="form-control" id="address" /></b></td>
							<td><b><spring:message code="searchForm.site1City"></spring:message>:<input type="text" ng-model="search.city" class="form-control" id="city"/></b></td>
						</tr>
						<tr>	
							<td><b><spring:message code="searchForm.site2Address"></spring:message>:<input type="text" ng-model="search.address2" class="form-control" id="address2" /></b></td>
							<td><b><spring:message code="searchForm.site2City"></spring:message>:<input type="text" ng-model="search.city2" class="form-control" id="city2"/></b></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="row">
				<div class="col-md-6 column">
				</div>
				<div class="col-md-4 column">
					<button  class="btn btn-primary" ng-click="doSearch()"><spring:message code="global.search"></spring:message></button>
				</div>
				<div class="col-md-2 column">
				</div>
			</div>
		</form>
	</div>
</div>
<div class="row clearfix">
	<div class="col-md-12 column">
		<h2 class="app-title" ng-if="isHidden">
			<div style="display: inline;"><spring:message code="searchForm.subTitle.outputServiceList"></spring:message></div>
			<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showLoading"/ >
		</h2>
		<div class="table-responsive marginTop" data-pattern="priority-columns">
			<table class="table table-small-font" ng-if="isHidden">
				<thead>
					<tr class="rich-table-header">
						<th><spring:message code="searchForm.circuitId"></spring:message></th>
						<th><spring:message code="searchForm.orderNumber"></spring:message></th>
						<th><spring:message code="searchForm.customer"></spring:message></th>
						<th><spring:message code="searchForm.productType"></spring:message></th>
						<th><spring:message code="searchForm.aSideSiteInformation"></spring:message></th>
						<th><spring:message code="searchForm.zSideSiteInformation"></spring:message></th>
					</tr>
					<tr ng-if="msg">
						<td colspan="6" class="alignMiddle">{{message}}</td>
					</tr>
				</thead>
				<tbody ng-if="showResult">
					<tr ng-repeat="circuit in circuits">
						<td>
							<div class="row clearfix">
								<div class="col-md-12 column">
									<a href="#/ServiceData/{{circuit.circPathInstID}}">{{circuit.circuitID}}</a>
								</div>
							</div>
						</td>
						<td>{{circuit.orderNumber}}</td>
						<td>{{circuit.customer}}</td>
						<td>{{circuit.productType}}</td>
						<td>{{circuit.aSideSite}}</td>
						<td>{{circuit.zSideSite}}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>