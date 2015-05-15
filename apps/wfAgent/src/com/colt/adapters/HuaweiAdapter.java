package com.colt.adapters;

import java.util.Map;

import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.Interface;

public class HuaweiAdapter extends Adapter {

	@Override
	public DeviceDetail fetch(String circuitID, String ipAddress) throws Exception {
		DeviceDetail deviceDetail = new DeviceDetail();
		if(ipAddress != null && !"".equals(ipAddress) && circuitID != null && !"".equals(circuitID)) {
			Map<String, Interface> ifAliasMap = retrieveIfAlias(circuitID, ipAddress);

			retrieveSNMPInterfaceName(ifAliasMap, ipAddress);
			retrieveSNMPInterfaceLastStatusChange(ifAliasMap, ipAddress);
			retrieveInterfaceIpAddress(ifAliasMap, ipAddress);
			retrieveInterfaceOperStatus(ifAliasMap, ipAddress);
			String sysUpTime = retrieveInterfaceSysUpTime(ipAddress);
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
