package com.colt.adapters.l2;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.l2.Adapter;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L2DeviceDetailsResponse;

public class ActelisAdapter extends Adapter {

	private Log log = LogFactory.getLog(ActelisAdapter.class);

	@Override
	public IDeviceDetailsResponse fetch(String circuitId, String deviceIP, Integer snmpVersion, String community, String portName, String type, String serviceType, String ocn, String deviceName) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L2DeviceDetailsResponse();
		DeviceDetail deviceDetail = new DeviceDetail();
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
		SNMPUtil snmp = new SNMPUtil(snmpVersion, type, serviceType);
		snmp.setCommunity(community);
		Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(circuitId, deviceIP, portName, type, deviceDetailsResponse);
		snmp.retrieveInterfaceL2Status(ifAliasMap, deviceIP, deviceDetailsResponse, SNMPUtil.L2_ADMIN_STATUS);
		snmp.retrieveInterfaceL2Status(ifAliasMap, deviceIP, deviceDetailsResponse, SNMPUtil.L2_OPERATIONAL_STATUS);
		String sysUpTime = snmp.retrieveInterfaceSysUpTime(deviceIP, deviceDetailsResponse);
		if (sysUpTime != null && !"".equals(sysUpTime)) {
			String sysuptimeFormated = snmp.retrieveSysUpTime(sysUpTime);
			if (sysuptimeFormated != null && !"".equals(sysuptimeFormated)) {
				deviceDetailsResponse.getDeviceDetails().setTime(sysuptimeFormated);
				snmp.retrieveInterfaceLastStatusChange(ifAliasMap, deviceIP, type, deviceDetailsResponse, sysuptimeFormated);
			}
		}
		if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
			for(String key : ifAliasMap.keySet()) {
				deviceDetailsResponse.getDeviceDetails().getInterfaces().add(ifAliasMap.get(key));
			}
		}
		return deviceDetailsResponse;
	}
}
