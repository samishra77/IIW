package com.colt.aopwf;

import java.util.Map;

import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class SNMPFetchActivity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		return snmpFetch(input);
	}

	private String[] snmpFetch(Map<String,Object> input) {
		String[] resp = null;
		DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
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
				SNMPUtil snmp = new SNMPUtil(snmpVersion, "CPE", deviceDetails.getServiceType());
				if(input.containsKey("community")) {
					snmp.setCommunity((String) input.get("community"));
				}
				Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(deviceDetailsResponse.getCircuitID(), deviceDetailsResponse.getDeviceIP(), deviceDetailsResponse);
				snmp.retrieveInterfaceName(ifAliasMap,deviceDetailsResponse.getDeviceIP(), deviceDetailsResponse);
				snmp.retrieveInterfaceLastStatusChange(ifAliasMap, deviceDetailsResponse.getDeviceIP(), deviceDetailsResponse);
				snmp.retrieveInterfaceIpAddress(ifAliasMap, deviceDetailsResponse.getDeviceIP(), deviceDetailsResponse);
				snmp.retrieveInterfaceOperStatus(ifAliasMap, deviceDetailsResponse.getDeviceIP(), deviceDetailsResponse);
				String sysUpTime = snmp.retrieveInterfaceSysUpTime(deviceDetailsResponse.getDeviceIP(), deviceDetailsResponse);
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
