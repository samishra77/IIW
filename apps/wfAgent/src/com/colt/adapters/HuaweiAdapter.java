package com.colt.adapters;

import java.util.Map;

import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class HuaweiAdapter extends Adapter {

	@Override
	public IDeviceDetailsResponse fetch(String circuitID, String ipAddress, int snmpVersion) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		DeviceDetail deviceDetail = new DeviceDetail();
		if(ipAddress != null && !"".equals(ipAddress) && circuitID != null && !"".equals(circuitID)) {
			SNMPUtil snmp = new SNMPUtil(snmpVersion);
			Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(circuitID, ipAddress, deviceDetailsResponse);
			snmp.retrieveInterfaceName(ifAliasMap, ipAddress, deviceDetailsResponse);
			snmp.retrieveInterfaceLastStatusChange(ifAliasMap, ipAddress, deviceDetailsResponse);
			snmp.retrieveInterfaceIpAddress(ifAliasMap, ipAddress, deviceDetailsResponse);
			snmp.retrieveInterfaceOperStatus(ifAliasMap, ipAddress, deviceDetailsResponse);
			String sysUpTime = snmp.retrieveInterfaceSysUpTime(ipAddress, deviceDetailsResponse);
			if(sysUpTime != null && !"".equals(sysUpTime)) {
				deviceDetail.setTime(sysUpTime);
			}
			for(String key : ifAliasMap.keySet()) {
				deviceDetail.getInterfaces().add(ifAliasMap.get(key));
			}
		}
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
		return deviceDetailsResponse;
	}
}
