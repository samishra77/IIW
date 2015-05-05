<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<div class="row clearfix" style="margin-top:10px;">
	<div class="col-md-3 column">
		<div class="tabbable font-smaller" id="tabs-219456">
			<a href="#/"><spring:message code="trail.home"></spring:message></a> &gt;  <spring:message code="trail.serviceData"></spring:message>
		</div>
	</div>
</div>
<div id="msg" class="message messageInfo marginTop" ng-if="error">{{messageError}}</div>
<div class="row clearfix marginTop">
	<div class="col-md-4 column">
		<h2 class="app-title">
			<div style="display: inline;"><spring:message code="serviceData.subTitle.serviceDetails"></spring:message></div>
			<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showDetailsLoading"/ >
		</h2>
	</div>
</div>
<div class="marginTop">
	<div class="row clearfix" ng-show="hasServiceDetail">
		<div class="col-md-12 column">
			<div class="table-responsive"  data-pattern="priority-columns">
				<table class="table table-small-font">
					<tr>
						<td class="font-strong"><spring:message code="serviceData.circuitId"></spring:message>:</td>
						<td>{{circuit.circuitID}}</td>
						<td class="font-strong"><spring:message code="serviceData.status"></spring:message>:</td>
						<td>{{circuit.status}}</td>
						<td class="font-strong"><spring:message code="serviceData.inServiceSince"></spring:message>:</td>
						<td>{{circuit.inServiceSince}}</td>
					</tr>
					<tr>
						<td class="font-strong"><spring:message code="serviceData.customer"></spring:message>:</td>
						<td>{{circuit.customer}}</td>
						<td class="font-strong"><spring:message code="serviceData.category"></spring:message>:</td>
						<td>{{circuit.category}}</td>
						<td class="font-strong"><spring:message code="serviceData.bandwidth"></spring:message>:</td>
						<td>{{circuit.bandWidth}}</td>
					</tr>
					<tr>
						<td class="font-strong"><spring:message code="serviceData.legalcustomerOCN"></spring:message>:</td>
						<td>{{circuit.customerOCN}}</td>
						<td class="font-strong"><spring:message code="serviceData.amnSLMStatus"></spring:message>:</td>
						<td>{{circuit.pamStatus}}</td>
						<td class="font-strong"><spring:message code="serviceData.managementTeam"></spring:message>:</td>
						<td>{{circuit.managementTeam}}</td>
					</tr>
					<tr>
						<td class="font-strong"><spring:message code="serviceData.relatedOrderNumber"></spring:message>:</td>
						<td>{{circuit.relatedOrderNumber}}</td>
						<td class="font-strong"><spring:message code="serviceData.resilienceType"></spring:message>:</td>
						<td>{{circuit.resilienceType}}</td>
						<td class="font-strong"><spring:message code="serviceData.trunkGroup"></spring:message>:</td>
						<td>{{circuit.trunkGroup}}</td>
					</tr>
					<tr>
						<td class="font-strong"><spring:message code="serviceData.order"></spring:message>:</td>
						<td>{{circuit.orderNumber}}</td>
						<td class="font-strong"><spring:message code="serviceData.serviceMenu"></spring:message>:</td>
						<td colspan="3">{{circuit.serviceMenu}}</td>
					</tr>
					<tr>
						<td class="font-strong"><spring:message code="serviceData.productName"></spring:message>:</td>
						<td>{{circuit.productName}} </td>
						<td class="font-strong"><spring:message code="serviceData.serviceId"></spring:message>:</td>
						<td>{{circuit.serviceId}} </td>
						<td class="font-strong"><spring:message code="serviceData.performanceMonitoring"></spring:message>:</td>
						<td>{{circuit.performanceMonitoring}}</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<table class="marginTop" ng-if="msg">
		<tr>
			<td>{{message}}</td>
		</tr>
	</table>
	<div class="row clearfix">
		<div class="col-md-4 column">
			<h2 class="app-title">
				<div style="display: inline;"><spring:message code="serviceData.subTitle.openTicketsInformation"></spring:message></div>
				<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showTicketLoading"/ >
			</h2>
		</div>
	</div>
	<div class="row clearfix marginTop">
		<div class="col-md-12 column">
			<div class="table-responsive"  data-pattern="priority-columns">
				<table class="table table-small-font">
					<thead>
						<tr class="rich-table-header">
							<th align="center"><spring:message code="serviceData.coltReference"></spring:message></th>
							<th align="center"><spring:message code="serviceData.customerReference"></spring:message></th>
							<th align="center"><spring:message code="serviceData.status"></spring:message></th>
							<th align="center"><spring:message code="serviceData.ticketDescription"></spring:message></th>
							<th align="center"><spring:message code="serviceData.openedDateTime"></spring:message></th>
							<th align="center"><spring:message code="serviceData.restoredDateTime"></spring:message></th>
							<th align="center"><spring:message code="serviceData.type"></spring:message></th>
							<th align="center"><spring:message code="serviceData.reportedBy"></spring:message></th>
							<th align="center"><spring:message code="serviceData.priority"></spring:message></th>
						</tr>
						<tr ng-if="msgTicket">
							<th colspan="9" align="center">{{messageTicket}}</th>
						</tr>
					</thead>
					<tbody ng-if="!msgTicket">
						<tr ng-repeat="ticket in tickets">
							<td>{{ticket.coltReference}}</a></td>
							<td>{{ticket.customerReference}}</a></td>
							<td>{{ticket.status}}</a></td>
							<td>{{ticket.ticketDescription}}</a></td>
							<td>{{ticket.opened}}</a></td>
							<td>{{ticket.restored}}</a></td>
							<td>{{ticket.type}}</a></td>
							<td>{{ticket.reportedBy}}</a></td>
							<td>{{ticket.priority}}</a></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="marginTop">
		<div class="table-responsive"  data-pattern="priority-columns">
			<table>
				<tbody>
					<tr>
						<td></td>
						<td></td>
						<td><button class="btn btn-primary" ng-click="openXperdrawPopup();"><spring:message code="global.showPath"></spring:message></button></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>