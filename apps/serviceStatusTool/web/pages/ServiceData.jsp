<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<div class="row clearfix" style="margin-top:10px;" ng-show="!showPopUp">
	<div class="col-md-3 column">
		<div class="tabbable font-smaller" id="tabs-219456">
			<a href="#/"><spring:message code="trail.home"></spring:message></a> &gt;  <spring:message code="trail.serviceData"></spring:message>
		</div>
	</div>
</div>
<div id="msg" class="message messageInfo marginTop" ng-if="error">{{messageError}}</div>
<div class="row clearfix marginTop" ng-show="showPopUp">
	<div class="col-md-12 column">
		<h2 class="app-title">
			<div style="display: inline;"><spring:message code="serviceDataPopUp.search.subTitle.relatedOrderServiceList"></spring:message></div>
			<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showLoading" />
		</h2>
		<div class="table-responsive marginTop" data-pattern="priority-columns" style="max-height: 250px;overflow-y: auto;">
			<table class="table table-small-font">
				<thead>
					<tr class="rich-table-header">
						<th><spring:message code="serviceDataPopUp.search.circuitId"></spring:message></th>
						<th><spring:message code="serviceDataPopUp.search.orderNumber"></spring:message></th>
						<th><spring:message code="serviceDataPopUp.search.customer"></spring:message></th>
						<th><spring:message code="serviceDataPopUp.search.productType"></spring:message></th>
						<th><spring:message code="serviceDataPopUp.search.aSideSiteInformation"></spring:message></th>
						<th><spring:message code="serviceDataPopUp.search.zSideSiteInformation"></spring:message></th>
					</tr>
					<tr ng-if="msg">
						<td colspan="6" class="alignMiddle">{{message}}</td>
					</tr>
				</thead>
				<tbody ng-if="showResult">
					<tr ng-repeat="circuit in circuitList">
						<td>
							<div class="row clearfix">
								<div class="col-md-12 column">
									<a href="" ng-click="findCircuitByCircPathInstID(circuit.circPathInstID)">{{circuit.circuitID}}</a>
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
<div class="row clearfix marginTop" ng-show="viewServiceData">
	<div class="col-md-4 column">
		<h2 class="app-title">
			<div style="display: inline;"><spring:message code="serviceData.subTitle.serviceDetails"></spring:message></div>
			<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showDetailsLoading"/ >
		</h2>
	</div>
</div>
<style>
.tdwrap td {
	white-space: normal !important;
}
</style>
<div class="marginTop">
	<div class="row clearfix" ng-show="hasServiceDetail">
		<div class="col-md-12 column">
			<div class="table-responsive"  data-pattern="priority-columns">
				<table class="table table-small-font tdwrap">
					<tr>
						<td class="font-strong"><spring:message code="serviceData.circuitId"></spring:message>:</td>
						<td>{{circuit.circuitID}}</td>
						<td class="font-strong"><spring:message code="serviceData.status"></spring:message>:</td>
						<td>{{circuit.status}}</td>
						<td class="font-strong"><spring:message code="serviceData.inServiceSince"></spring:message>:</td>
						<td><spring:message code="serviceData.notAvailable"/></td>
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
						<td>
							<label ng-repeat="relatedOrderNumber in circuit.relatedOrderNumberList" style="font-weight: normal;">
								<a href="" ng-click="doRelatedOrderNumber(relatedOrderNumber)">{{relatedOrderNumber}}</a><label ng-if="!$last" style="font-weight: normal;">,</label>
							</label>
						</td>		
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
	<div class="row clearfix" ng-show="viewServiceData">
		<div class="col-md-4 column">
			<h2 class="app-title">
				<div style="display: inline;"><spring:message code="serviceData.subTitle.openTicketsInformation"></spring:message></div>
				<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showTicketLoading"/ >
			</h2>
		</div>
	</div>
	<div class="row clearfix marginTop" ng-show="viewServiceData">
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
	<div class="marginTop" ng-show="viewServiceData">
		<div class="table-responsive" data-pattern="priority-columns" style="border: 0">
			<table style="width: 50%" cellspacing="10">
				<tbody>
					<tr>
						<td><button class="btn btn-primary" ng-click="doSideInformation()"><spring:message code="global.showStatus"></spring:message></button></td>
						<td><button class="btn btn-primary" ng-click="openXperdrawPopup();"><spring:message code="global.showPath"></spring:message></button></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>

	<div class="message messageInfo marginTop" ng-show="sideInformationError">{{sideInformationErrorMessage}}</div>

	<div class="marginTop" ng-if="viewSideInformation">
		<div class="table-responsive" style="width: 50%;float: left;min-width: 200px;">
			<table class="panel-sideInformation">
				<tr class="rich-table-header">
					<th><label class="labelData"><spring:message code="serviceData.aSide.information.title"></spring:message></label>
						<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showSideLoading" />
					</th>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font sideInformation" >
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.siteType"></spring:message></td>
								<td>{{sideInformation.aSideInformation.type}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.deviceName"></spring:message></td>
								<td>{{sideInformation.aSideInformation.deviceName}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.vendor"></spring:message> / <spring:message code="serviceData.aSide.model"></spring:message> </td>
								<td ng-if="!isMisMatchA" ><label style="font-weight: normal">{{sideInformation.aSideInformation.vendor}}</label> / <label style="font-weight: normal;"> {{sideInformation.aSideInformation.model}}</label></td>
								<td ng-if="isMisMatchA" >
									<label style="font-weight: normal">{{sideInformation.aSideInformation.vendor}}</label> / <label style="font-weight: normal;"> {{sideInformation.aSideInformation.model}}</label>
									 =&gt; 
									<label style="font-weight: normal">{{vendorDeviceA}}</label> / <label style="font-weight: normal;"> {{modelDeviceA}}</label>
								</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.xngDeviceName"></spring:message></td>
								<td>{{sideInformation.aSideInformation.xngDeviceName}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.managementIPAddress"></spring:message></td>
								<td>{{aSideManagementIPAddress}}</td>
							</tr>
							<tr>
								<td ng-if="!aViewPortLanLinkAtrica" class="font-strong"><spring:message code="serviceData.aSide.portName"></spring:message></td>
								<td ng-if="!aViewPortLanLinkAtrica">{{sideInformation.aSideInformation.port}}</td>
								<td ng-if="aViewPortLanLinkAtrica" class="font-strong"><spring:message code="serviceData.aSide.portSlot"></spring:message></td>
								<td ng-if="aViewPortLanLinkAtrica">{{sideInformation.aSideInformation.portSlot}}</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr ng-show="showDeviceErrorASide">
					<td class="msg-red" >{{deviceMessageASideError}}</td>
				</tr>
				<tr>
					<td style="padding: 8px;">
						<button class="button button-primary" ng-click="doDeviceRefresh('aside')" ng-if="showButtonRefreshASide"><spring:message code="global.refresh"></spring:message></button>
						<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showRefreshASideLoading"/ >
					</td>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font" style="border :0;" >
							<tr>
								<td class="font-strong" style="border-top: 0;"><spring:message code="serviceData.aSide.deviceStatus"></spring:message>:</td>
								<td style="border-top: 0" >{{aSideDeviceStatus}}</td>
							</tr>
							<tr>
								<td ng-if="!aViewPortLanLinkAtrica" class="font-strong" style="border-top: 0;">
									<label ng-if="isAccedianOvertureA" class="msgInconsistencie">*</label>
									<spring:message code="serviceData.aSide.deviceUptime"></spring:message>:
								</td>
								<td ng-if="!aViewPortLanLinkAtrica" style="border-top: 0">{{aSideDeviceUpTime}}</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<label class="labelData"><spring:message code="serviceData.aSide.customerInterfaces"></spring:message></label>
					</td>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font sideInformation" >
							<thead>
								<tr>
									<td><spring:message code="serviceData.aSide.customerInterfaces.title.name"></spring:message></td>
									<td ng-if="!viewLanLinkZSideInformation"><spring:message code="serviceData.aSide.customerInterfaces.title.ipAddress"></spring:message></td>
									<td ng-if="!viewLanLinkZSideInformation"><spring:message code="serviceData.aSide.customerInterfaces.title.status"></spring:message></td>
									<td ng-if="viewLanLinkZSideInformation"><spring:message code="serviceData.aSide.customerInterfaces.title.opStatus"></spring:message></td>
									<td ng-if="viewLanLinkZSideInformation"><spring:message code="serviceData.aSide.customerInterfaces.title.adminStatus"></spring:message></td>
									<td ng-if="!aViewPortLanLinkAtrica">
										<label ng-if="isAccedianOvertureA" class="msgInconsistencie">*</label>
										<spring:message code="serviceData.aSide.customerInterfaces.title.lastStateChange">
									</spring:message></td>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="asideInferf in aSideInterfaces">
									<td>{{asideInferf.name}}</td>
									<td ng-if="!viewLanLinkZSideInformation">{{asideInferf.ipaddress}}</td>
									<td ng-if="!viewLanLinkZSideInformation">{{asideInferf.status}}</td>
									<td ng-if="viewLanLinkZSideInformation">{{asideInferf.opStatus}}</td>
									<td ng-if="viewLanLinkZSideInformation">{{asideInferf.adminStatus}}</td>
									<td ng-if="!aViewPortLanLinkAtrica">{{asideInferf.lastChgTime}}</td>
								</tr>
							</tbody>
						</table>
						<label ng-if="isAccedianOvertureA" class="msgInconsistencie"><spring:message code="serviceData.inconsistencie.sysUpTime"></spring:message></label>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<!-- lanlink service -->
	<div class="marginTop" ng-if="viewLanLinkZSideInformation">
		<div class="table-responsive"  style="width: 50%;float: left;min-width: 200px">
			<table class="panel-sideInformation">
				<tr class="rich-table-header">
					<th><label class="labelData"><spring:message code="serviceData.zSide.information.title"></spring:message></label>
						<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showSideLoading" />
					</th>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font sideInformation" >
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.siteType"></spring:message></td>
								<td>{{sideInformation.zSideInformation.type}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.deviceName"></spring:message></td>
								<td>{{sideInformation.zSideInformation.deviceName}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.vendor"></spring:message> / <spring:message code="serviceData.aSide.model"></spring:message> </td>
								<td ng-if="!isMisMatchZ"><label style="font-weight: normal">{{sideInformation.zSideInformation.vendor}}</label> / <label style="font-weight: normal;"> {{sideInformation.zSideInformation.model}}</label></td>
								<td ng-if="isMisMatchZ">
									<label style="font-weight: normal">{{sideInformation.zSideInformation.vendor}}</label> / <label style="font-weight: normal;"> {{sideInformation.zSideInformation.model}}</label>
									 =&gt; 
									<label style="font-weight: normal">{{vendorDeviceZ}}</label> / <label style="font-weight: normal;"> {{modelDeviceZ}}</label>
								</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.xngDeviceName"></spring:message></td>
								<td>{{sideInformation.zSideInformation.xngDeviceName}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.aSide.managementIPAddress"></spring:message></td>
								<td>{{zSideManagementIPAddress}}</td>
							</tr>
							<tr>
								<td ng-if="!zViewPortLanLinkAtrica" class="font-strong"><spring:message code="serviceData.aSide.portName"></spring:message></td>
								<td ng-if="!zViewPortLanLinkAtrica">{{sideInformation.zSideInformation.port}}</td>
								<td ng-if="zViewPortLanLinkAtrica" class="font-strong"><spring:message code="serviceData.zSide.portSlot"></spring:message></td>
								<td ng-if="zViewPortLanLinkAtrica">{{sideInformation.zSideInformation.portSlot}}</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr ng-show="showDeviceErrorZSide">
					<td class="msg-red" >{{deviceMessageZSideError}}</td>
				</tr>
				<tr>
					<td style="padding: 8px;">
						<button class="button button-primary" ng-click="doDeviceRefresh('zside')" ng-if="showButtonRefreshZSide"><spring:message code="global.refresh"></spring:message></button>
						<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showRefreshZSideLoading"/ >
					</td>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font" style="border :0;" >
							<tr>
								<td class="font-strong" style="border-top: 0;"><spring:message code="serviceData.aSide.deviceStatus"></spring:message>:</td>
								<td style="border-top: 0" >{{zSideDeviceStatus}}</td>
							</tr>
							<tr>
								<td ng-if="!zViewPortLanLinkAtrica" class="font-strong" style="border-top: 0;">
									<label ng-if="isAccedianOvertureZ" class="msgInconsistencie">*</label>
									<spring:message code="serviceData.aSide.deviceUptime"></spring:message>:
								</td>
								<td ng-if="!zViewPortLanLinkAtrica" style="border-top: 0">{{zSideDeviceUpTime}}</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<label class="labelData"><spring:message code="serviceData.aSide.customerInterfaces"></spring:message></label>
					</td>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font sideInformation" >
							<thead>
								<tr>
									<td><spring:message code="serviceData.aSide.customerInterfaces.title.name"></spring:message></td>
									<td><spring:message code="serviceData.aSide.customerInterfaces.title.opStatus"></spring:message></td>
									<td><spring:message code="serviceData.aSide.customerInterfaces.title.adminStatus"></spring:message></td>
									<td ng-if="!zViewPortLanLinkAtrica">
										<label ng-if="isAccedianOvertureZ" class="msgInconsistencie">*</label>
										<spring:message code="serviceData.aSide.customerInterfaces.title.lastStateChange">
									</spring:message></td>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="inferf in zSideInterfaces">
									<td>{{inferf.name}}</td>
									<td>{{inferf.opStatus}}</td>
									<td>{{inferf.adminStatus}}</td>
									<td ng-if="!zViewPortLanLinkAtrica">{{inferf.lastChgTime}}</td>
								</tr>
							</tbody>
						</table>
						<label ng-if="isAccedianOvertureZ" class="msgInconsistencie"><spring:message code="serviceData.inconsistencie.sysUpTime"></spring:message></label>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<!-- lanlink service -->
	<!-- not lanlink service -->
	<div class="marginTop" ng-if="!viewLanLinkZSideInformation && viewSideInformation">
		<div class="table-responsive"  style="width: 50%;float: left;min-width: 200px">
			<table class="panel-sideInformation">
				<tr class="rich-table-header">
					<th>
						<label class="labelData"><spring:message code="serviceData.zSide.information.title"></spring:message></label>
						<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showSideLoading" />
					</th>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font sideInformation">
							<tr>
								<td class="font-strong"><spring:message code="serviceData.zSide.siteType"></spring:message></td>
								<td>{{sideInformation.zSideInformation.type}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.zSide.vendor"></spring:message></td>
								<td>{{sideInformation.zSideInformation.vendor}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.zSide.managementIPAddress"></spring:message></td>
								<td>{{zSideManagementIPAddress}}</td>
							</tr>
							<tr>
								<td class="font-strong"><spring:message code="serviceData.zSide.xngDeviceName"></spring:message></td>
								<td>{{sideInformation.zSideInformation.xngDeviceName}}</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr ng-show="showDeviceErrorZSide">
					<td class="msg-red">{{deviceMessageZSideError}}</td>
				</tr>
				<tr>
					<td style="padding: 8px;">
						<button class="button button-primary" ng-click="doDeviceRefresh('zside')" ng-if="showButtonRefreshZSide"><spring:message code="global.refresh"></spring:message></button>
							<img src="<%=request.getContextPath()%>/images/loading.gif"  alt="" ng-if="showRefreshZSideLoading" />
					</td>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font" style="border :0;">
							<tr>
								<td class="font-strong" style="border-top: 0"><spring:message code="serviceData.zSide.deviceStatus"></spring:message>:</td>
								<td style="border-top: 0">{{zSideDeviceStatus}}</td>
							</tr>
							<tr>
								<td class="font-strong" style="border-top: 0"><spring:message code="serviceData.zSide.deviceUptime"></spring:message>:</td>
								<td style="border-top: 0">{{zSideDeviceUpTime}}</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<label class="labelData"><spring:message code="serviceData.zSide.customerLogicalInterface"></spring:message></label>
					</td>
				</tr>
				<tr>
					<td>
						<table class="table table-small-font sideInformation" >
							<thead>
								<tr>
									<td><spring:message code="serviceData.zSide.customerLogicalInterface.name"></spring:message></td>
									<td><spring:message code="serviceData.zSide.customerLogicalInterface.ipAddress"></spring:message></td>
									<td><spring:message code="serviceData.zSide.customerLogicalInterface.status"></spring:message></td>
									<td><spring:message code="serviceData.zSide.customerLogicalInterface.lastStateChange"></spring:message></td>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="interf in zSideInterfaceLogicals" >
									<td>{{interf.name}}</td>
									<td>{{interf.ipaddress}}</td>
									<td>{{interf.status}}</td>
									<td>{{interf.lastChgTime}}</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<tr ng-show="showZEndPhysicalInterface">
					<td><label class="labelData"><spring:message code="serviceData.zSide.physicalInterface"></spring:message></label></td>
				</tr>
				<tr ng-show="showZEndPhysicalInterface">
					<td>
						<table class="table table-small-font sideInformation" >
							<thead>
								<tr>
									<td><spring:message code="serviceData.zSide.physicalInterface.name"></spring:message></td>
									<td><spring:message code="serviceData.zSide.physicalInterface.status"></spring:message></td>
									<td><spring:message code="serviceData.zSide.physicalInterface.lastStateChange"></spring:message></td>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="interf in zSidePhysicalInterfaces">
									<td>{{interf.name}}</td>
									<td>{{interf.status}}</td>
									<td>{{interf.lastChgTime}}</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<!-- not lanlink service -->
</div>