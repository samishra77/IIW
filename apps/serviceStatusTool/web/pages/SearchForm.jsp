<div id="trail">
	<div id="trail-container"><a href="#">Home</a> &gt;  TCS Service Status Tool</div>
</div>
<h2 class="app-title">Search For Services</h2>
<form role="form">
	<br/>
	<div id="msg" class="message messageInfo" ng-if="error">{{messageError}}</div>
	<label class="font-smaller">(Use % for wildcard)</label>
	<table border="0" cellspacing="0" cellpadding="3" class="marginTop">
		<tbody>
			<tr>
				<td class="label-without-width"><label class="font-strong">XNG Order:</label></td>
				<td class="label-without-width"><label class="font-strong">XNG Service:</label></td>
			</tr>
			<tr>
				<td class="value"><input type="text" ng-model="search.order" id="order"/></td>
				<td class="value"><input type="text" ng-model="search.service" id="service"/></td>
			</tr>
			<tr>
				<td class="label-without-width" colspan="2"><label class="font-strong">Customer:</label></td>
			</tr>
			<tr>
				<td class="value" colspan="2"><input type="text" ng-model="search.customer" id="customer"/></td>
			</tr>
			<tr>
				<td class="label-without-width"><label class="font-strong">Site1 Address:</label></td>
				<td class="label-without-width"><label class="font-strong">Site1 City:</label></td>
			</tr>
			<tr>
				<td class="value"><input type="text" ng-model="search.address" id="address"/></td>
				<td class="value"><input type="text" ng-model="search.city" id="city"/></td>
			</tr>
			<tr>
				<td class="label-without-width"><label class="font-strong">Site2 Address:</label></td>
				<td class="label-without-width"><label class="font-strong">Site2 City:</label></td>
			</tr>
			<tr>
				<td class="value"><input type="text" ng-model="search.address2" id="address2"/></td>
				<td class="value"><input type="text" ng-model="search.city2" id="city2"/></td>
			</tr>
			<tr>
				<td colspan="2"><button ng-click="doSearch()">Search</button></td>
			</tr>
		</tbody>
	</table>
</form>
<div class="marginTop">
	<h2 class="app-title" ng-if="isHidden">
		<div style="display: inline;">Output Service List</div>
		<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showLoading"/ >
	</h2>
	<table border="1" cellspacing="0" cellpadding="0" class="fullWidth marginTop" ng-if="isHidden">
		<tr class="rich-table-header">
			<td class="alignMiddle">Circuit ID</td>
			<td class="alignMiddle">Order Number</td>
			<td class="alignMiddle">Customer</td>
			<td class="alignMiddle">Product Type</td>
			<td class="alignMiddle">A Side Site Information</td>
			<td class="alignMiddle">Z Side Site Information</td>
		</tr>
		<tr ng-if="msg">
			<td colspan="6" class="alignMiddle">{{message}}</td>
		</tr>
		<tr ng-repeat="circuit in circuits" ng-if="showResult">
			<td><a href="#/ServiceData/{{circuit.circPathInstID}}">{{circuit.circuitID}}</a></td>
			<td>{{circuit.orderNumber}}</td>
			<td>{{circuit.customer}}</td>
			<td>{{circuit.productType}}</td>
			<td>{{circuit.aSideSite}}</td>
			<td>{{circuit.zSideSite}}</td>
		</tr>
	</table>
</div>
