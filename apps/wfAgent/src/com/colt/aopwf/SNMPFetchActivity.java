package com.colt.aopwf;

import java.util.Map;

import com.colt.util.SNMPUtil;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class SNMPFetchActivity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		return snmpFetch(input);
	}

	private String[] snmpFetch(Map<String,Object> input) {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = null;
		if(input.containsKey("deviceDetailsResponse")) {
			if(input.get("deviceDetailsResponse") instanceof L3DeviceDetailsResponse) {
				deviceDetailsResponse = (L3DeviceDetailsResponse) input.get("deviceDetailsResponse");
			} else {
				//L2 Cast
			}
		}
		if(input != null && input.containsKey("deviceDetails")) {
			Integer snmpVersion = (Integer) input.get("snmpVersion");
			if (snmpVersion != null) {
				SNMPUtil snmp = new SNMPUtil(snmpVersion);
				Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(deviceDetailsResponse.getCircuitID(), deviceDetailsResponse.getWanIP(), deviceDetailsResponse);
				snmp.retrieveInterfaceName(ifAliasMap,deviceDetailsResponse.getWanIP(), deviceDetailsResponse);
				snmp.retrieveInterfaceLastStatusChange(ifAliasMap, deviceDetailsResponse.getWanIP(), deviceDetailsResponse);
				snmp.retrieveInterfaceIpAddress(ifAliasMap, deviceDetailsResponse.getWanIP(), deviceDetailsResponse);
				snmp.retrieveInterfaceOperStatus(ifAliasMap, deviceDetailsResponse.getWanIP(), deviceDetailsResponse);
				String sysUpTime = snmp.retrieveInterfaceSysUpTime(deviceDetailsResponse.getWanIP(), deviceDetailsResponse);
				if(sysUpTime != null && !"".equals(sysUpTime)) {
					deviceDetailsResponse.getDeviceDetails().setTime(sysUpTime);
				}
				if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
					for(String key : ifAliasMap.keySet()) {
						deviceDetailsResponse.getDeviceDetails().getInterfaces().add(ifAliasMap.get(key));
					}
				}
			}
			resp = new String[] {"SENDRESPONSE"};
		}
		return resp;
	}
}
