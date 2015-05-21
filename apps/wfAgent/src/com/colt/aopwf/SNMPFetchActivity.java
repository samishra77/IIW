package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class SNMPFetchActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(SNMPFetchActivity.class);

	public String[] process(Map<String,Object> input) {
		return snmpFetch(input);
	}

	private String[] snmpFetch(Map<String,Object> input) {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		if(input != null && input.containsKey("deviceDetails")) {
			DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
			try {
				Integer snmpVersion = (Integer) input.get("snmpVersion");
				if (snmpVersion != null) {
					SNMPUtil snmp = new SNMPUtil(snmpVersion);
					Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(deviceDetails.getCircuitID(), deviceDetails.getIp(), deviceDetailsResponse);
					snmp.retrieveInterfaceName(ifAliasMap, deviceDetails.getIp(), deviceDetailsResponse);
					snmp.retrieveInterfaceLastStatusChange(ifAliasMap, deviceDetails.getIp(), deviceDetailsResponse);
					snmp.retrieveInterfaceIpAddress(ifAliasMap, deviceDetails.getIp(), deviceDetailsResponse);
					snmp.retrieveInterfaceOperStatus(ifAliasMap, deviceDetails.getIp(), deviceDetailsResponse);
					String sysUpTime = snmp.retrieveInterfaceSysUpTime(deviceDetails.getIp(), deviceDetailsResponse);
					if(input.containsKey("l3DeviceDetails")) {
						L3DeviceDetailsResponse l3DeviceDetails = (L3DeviceDetailsResponse) input.get("l3DeviceDetails");
						if(l3DeviceDetails.getDeviceDetails() == null) {
							l3DeviceDetails.setDeviceDetails(new DeviceDetail());
						}

						if(sysUpTime != null && !"".equals(sysUpTime)) {
							l3DeviceDetails.getDeviceDetails().setTime(sysUpTime);
						}
						if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
							for(String key : ifAliasMap.keySet()) {
								l3DeviceDetails.getDeviceDetails().getInterfaces().add(ifAliasMap.get(key));
							}
						}
						l3DeviceDetails.setErrorResponse(deviceDetailsResponse.getErrorResponse());
					}
				}
			} catch (Exception e) {
				log.error(e,e);
				input.put("exception", e);
			}
			resp = new String[] {"SENDRESPONSE"};
		}
		return resp;
	}
}
