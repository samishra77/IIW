<div class="row clearfix" style="margin-top:10px;">
	<div class="col-md-3 column">
		<div class="tabbable font-smaller" id="tabs-219456">
			<a href="#">Home</a> &gt;  TCS Service Status Tool
		</div>
	</div>
</div>
<div id="msg" class="message messageInfo marginTop" ng-if="error">{{messageError}}</div>
<div class="row clearfix marginTop">
	<div class="col-md-4 column">
		<h2 class="app-title">
			<div style="display: inline;">Service details</div>
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
						<td class="font-strong">Circuit Id:</td>
						<td>{{circuit.circuitID}}</td>
						<td class="font-strong">Status:</td>
						<td>{{circuit.status}}</td>
						<td class="font-strong">In Service since:</td>
						<td>{{circuit.inServiceSince}}</td>
					</tr>
					<tr>
						<td class="font-strong">Customer:</td>
						<td>{{circuit.customer}}</td>
						<td class="font-strong">Category:</td>
						<td>{{circuit.category}}</td>
						<td class="font-strong">Bandwidth:</td>
						<td>{{circuit.bandWidth}}</td>
					</tr>
					<tr>
						<td class="font-strong">Legal Customer OCN:</td>
						<td>{{circuit.customerOCN}}</td>
						<td class="font-strong">AMN SLM (PAM) Status:</td>
						<td>{{circuit.pamStatus}}</td>
						<td class="font-strong">Management Team:</td>
						<td>{{circuit.managementTeam}}</td>
					</tr>
					<tr>
						<td class="font-strong">Related Order Number:</td>
						<td>{{circuit.relatedOrderNumber}}</td>
						<td class="font-strong">Performance Monitoring</td>
						<td>{{circuit.performanceMonitoring}}</td>
						<td class="font-strong">Trunk Group</td>
						<td>{{circuit.trunkGroup}}</td>
					</tr>
					<tr>
						<td class="font-strong">Order</td>
						<td>{{circuit.orderNumber}}</td>
						<td class="font-strong">Service Menu:</td>
						<td colspan="3">{{circuit.serviceMenu}}</td>
					</tr>
					<tr>
						<td class="font-strong">Product Name:</td>
						<td colspan="5">{{circuit.productName}} </td>
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
				<div style="display: inline;">Open Tickets Information</div>
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
							<th align="center">COLT Reference</th>
							<th align="center">Customer Reference</th>
							<th align="center">Status</th>
							<th align="center">Ticket Description</th>
							<th align="center">Opened Date/Time</th>
							<th align="center">Restored Date/Time</th>
							<th align="center">Type</th>
							<th align="center">Reported By</th>
							<th align="center">Priority</th>
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
</div>