<div id="trail">
	<div id="trail-container"><a href="<%=request.getContextPath()%>/">Home</a> &gt;  TCS Service Status Tool</div>
</div>
<div>
	<h2 class="app-title">
		<div style="display: inline;">Service details</div>
		<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showDetailsLoading"/ >
	</h2>
	<div id="msg" class="message messageInfo" ng-if="error">{{messageError}}</div>
	<table border="0" cellspacing="0" cellpadding="3" class="marginTop tableService fullWidth" ng-show="hasServiceDetail">
		<tr class="label-without-width">
			<td class="font-strong">Circuit Id:</td>
			<td>{{circuit.circuitID}}</td>
			<td class="font-strong">Status:</td>
			<td>{{circuit.status}}</td>
			<td class="font-strong">In Service since:</td>
			<td>{{circuit.inServiceSince}}</td>
		</tr>
		<tr class="label-without-width">
			<td class="font-strong">Customer:</td>
			<td>{{circuit.customer}}</td>
			<td class="font-strong">Category:</td>
			<td>{{circuit.category}}</td>
			<td class="font-strong">Bandwidth:</td>
			<td>{{circuit.bandWidth}}</td>
		</tr>
		<tr class="label-without-width">
			<td class="font-strong">Legal Customer OCN:</td>
			<td>{{circuit.customerOCN}}</td>
			<td class="font-strong">AMN SLM (PAM) Status:</td>
			<td>{{circuit.pamStatus}}</td>
			<td class="font-strong">Management Team:</td>
			<td>{{circuit.managementTeam}}</td>
		</tr>
		<tr class="label-without-width">
			<td class="font-strong">Related Order Number:</td>
			<td>{{circuit.relatedOrderNumber}}</td>
			<td class="font-strong">Performance Monitoring</td>
			<td>{{circuit.performanceMonitoring}}</td>
			<td class="font-strong">Trunk Group</td>
			<td>{{circuit.trunkGroup}}</td>
		</tr>
		<tr class="label-without-width">
			<td class="font-strong">Order</td>
			<td>{{circuit.orderNumber}}</td>
			<td class="font-strong">Service Menu:</td>
			<td colspan="3">{{circuit.serviceMenu}}</td>
		</tr>
		<tr class="label-without-width" >
			<td class="font-strong">Product Name:</td>
			<td colspan="5">{{circuit.productName}} </td>
		</tr>
	</table>
	<table border="0" cellspacing="0" cellpadding="3" class="marginTop tableService fullWidth" ng-if="msg">
		<tr class="label-without-width" >
			<td>{{message}}</td>
		</tr>
	</table>
	<div class="marginTop">
		<h2 class="app-title">
			<div style="display: inline;">Open Tickets Information</div>
			<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showTicketLoading"/ >
		</h2>
		<table border="1" cellspacing="0" cellpadding="0" class="fullWidth marginTop">
			<tr class="rich-table-header">
				<td align="center">Colt Reference</td>
				<td align="center">Customer Reference</td>
				<td align="center">Status</td>
				<td align="center">Ticket Description</td>
				<td align="center">Opened Date/Time</td>
				<td align="center">Restored Date/Time</td>
				<td align="center">Type</td>
				<td align="center">Reported By</td>
				<td align="center">Priority</td>
			</tr>
			<tr ng-if="msgTicket">
				<td colspan="9" class="alignMiddle">{{messageTicket}}</td>
			</tr>
			<tr ng-repeat="ticket in tickets" ng-if="!msgTicket">
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
		</table>
	</div>
</div>