package com.colt.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.colt.util.MessagesErrors;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class HuaweiAdapter extends Adapter {

	@Override
	public IDeviceDetailsResponse fetch(String circuitID, String deviceIP, Integer snmpVersion, String wanIP) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		DeviceDetail deviceDetail = new DeviceDetail();
		if(deviceIP != null && !"".equals(deviceIP) && circuitID != null && !"".equals(circuitID) && snmpVersion != null) {
			SNMPUtil snmp = new SNMPUtil(snmpVersion);
			Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(circuitID, deviceIP, deviceDetailsResponse);
			snmp.retrieveInterfaceName(ifAliasMap, deviceIP, deviceDetailsResponse);
			snmp.retrieveInterfaceLastStatusChange(ifAliasMap, deviceIP, deviceDetailsResponse);
			snmp.retrieveInterfaceIpAddress(ifAliasMap, deviceIP, deviceDetailsResponse);
			snmp.retrieveInterfaceOperStatus(ifAliasMap, deviceIP, deviceDetailsResponse);
			String sysUpTime = snmp.retrieveInterfaceSysUpTime(deviceIP, deviceDetailsResponse);
			if(sysUpTime != null && !"".equals(sysUpTime)) {
				deviceDetail.setTime(sysUpTime);
			}
			deviceDetail.getInterfaces().addAll(sortInterfaces(ifAliasMap, wanIP));

			if(wanIP == null || "".equals(wanIP)) {
				if (deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("wanIP.calculetError"));
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
			}
		}
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
		return deviceDetailsResponse;
	}

	private List<Interface> sortInterfaces(Map<String, Interface> ifAliasMap, String wanIP) {
		List<Interface> respList = new ArrayList<Interface>();
		if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
			List<Interface> wanInterfaceList = new ArrayList<Interface>();
			List<Interface> deviceIpInterfaceList = new ArrayList<Interface>();
			for(String key : ifAliasMap.keySet()) {
				if(wanIP != null && wanIP.equals(ifAliasMap.get(key).getIpaddress())) {
					wanInterfaceList.add(ifAliasMap.get(key));
				} else {
					deviceIpInterfaceList.add(ifAliasMap.get(key));
				}
			}

			if(!wanInterfaceList.isEmpty()) {
				respList.addAll(wanInterfaceList);
			}

			if(!deviceIpInterfaceList.isEmpty()) {
				respList.addAll(deviceIpInterfaceList);
			}
		}
		return respList;
	}
}
