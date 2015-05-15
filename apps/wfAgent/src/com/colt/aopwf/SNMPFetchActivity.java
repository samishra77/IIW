package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class SNMPFetchActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(SNMPFetchActivity.class);

	public String[] process(Map<String,Object> input) {
		return snmpFetch(input);
	}

	private String[] snmpFetch(Map<String,Object> input) {
		String[] resp = null;
		if(input != null && input.containsKey("deviceDetails")) {
			DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
			try {
				Integer snmpVersion = (Integer) input.get("snmpVersion");
				SNMPUtil snmp = new SNMPUtil(snmpVersion.intValue());
				Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(deviceDetails.getCircuitID(), deviceDetails.getIp());
				snmp.retrieveInterfaceName(ifAliasMap, deviceDetails.getIp());
				snmp.retrieveInterfaceLastStatusChange(ifAliasMap, deviceDetails.getIp());
				snmp.retrieveInterfaceIpAddress(ifAliasMap, deviceDetails.getIp());
				snmp.retrieveInterfaceOperStatus(ifAliasMap, deviceDetails.getIp());
				String sysUpTime = snmp.retrieveInterfaceSysUpTime(deviceDetails.getIp());
				if(input.containsKey("l3DeviceDetails")) {
					L3DeviceDetailsResponse l3DeviceDetails = (L3DeviceDetailsResponse) input.get("l3DeviceDetails");
					if(sysUpTime != null && !"".equals(sysUpTime)) {
						l3DeviceDetails.getDeviceDetails().setTime(sysUpTime);
					}
					for(String key : ifAliasMap.keySet()) {
						l3DeviceDetails.getDeviceDetails().getInterfaces().add(ifAliasMap.get(key));
					}
				}
			} catch (Exception e) {
				log.error(e,e);
			}
			resp = new String[] {"SENDRESPONSE"};
		}
		return resp;
	}
}
