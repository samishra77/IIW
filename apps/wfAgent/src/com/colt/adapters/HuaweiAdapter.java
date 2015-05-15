package com.colt.adapters;

import java.util.Map;

import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.Interface;

public class HuaweiAdapter extends Adapter {

	@Override
	public DeviceDetail fetch(String circuitID, String ipAddress, int snmpVersion) throws Exception {
		DeviceDetail deviceDetail = new DeviceDetail();
		if(ipAddress != null && !"".equals(ipAddress) && circuitID != null && !"".equals(circuitID)) {
			SNMPUtil snmp = new SNMPUtil(snmpVersion);
			Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(circuitID, ipAddress);
			snmp.retrieveInterfaceName(ifAliasMap, ipAddress);
			snmp.retrieveInterfaceLastStatusChange(ifAliasMap, ipAddress);
			snmp.retrieveInterfaceIpAddress(ifAliasMap, ipAddress);
			snmp.retrieveInterfaceOperStatus(ifAliasMap, ipAddress);
			String sysUpTime = snmp.retrieveInterfaceSysUpTime(ipAddress);
			if(sysUpTime != null && !"".equals(sysUpTime)) {
				deviceDetail.setTime(sysUpTime);
			}
			for(String key : ifAliasMap.keySet()) {
				deviceDetail.getInterfaces().add(ifAliasMap.get(key));
			}
		}
		return deviceDetail;
	}
}
