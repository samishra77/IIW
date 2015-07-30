package com.colt.adapters.l2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentUtil;
import com.colt.util.MessagesErrors;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L2DeviceDetailsResponse;

public class OvertureAdapter extends Adapter {

	private Log log = LogFactory.getLog(OvertureAdapter.class);

	public IDeviceDetailsResponse fetch(String circuitId, String deviceIP, Integer snmpVersion, String community, String portName, String slotNumber, String type, String serviceType, String ocn, String deviceName) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L2DeviceDetailsResponse();
		DeviceDetail deviceDetail = new DeviceDetail();
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
		SNMPUtil snmp = new SNMPUtil(snmpVersion, type, serviceType);
		snmp.setCommunity(community);
		if(portName != null) {
			log.debug("portName: " + portName);
			Map<String, Interface> ifAliasMapPortName = new HashMap<String, Interface>();
			Map<String, Interface> ifAliasMap = snmp.retrieveIfAlias(circuitId, deviceIP, type, deviceDetailsResponse);
			// Filter output by portname. If no interfaces found, then we show all interfaces with admin status UP.
			Interface interfPortName = null;
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				for(String key : ifAliasMap.keySet()) {
					if(ifAliasMap.get(key).getName().contains(portName)) {
						interfPortName = ifAliasMap.get(key);
						ifAliasMapPortName.put(key, interfPortName);
					}
				}
				if(!ifAliasMapPortName.isEmpty()) {
					ifAliasMap = ifAliasMapPortName;
				}
			}
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				for(String ifAlias : ifAliasMap.keySet()) {
					snmp.retrieveInterfaceL2Status(ifAliasMap, deviceIP, deviceDetailsResponse, SNMPUtil.L2_ADMIN_STATUS, SNMPUtil.L2_ADMIN_STATUS + ifAlias);
				}
			}
			if(ifAliasMap != null && !ifAliasMap.isEmpty() && interfPortName == null) {
				Iterator<Map.Entry<String,Interface>> iter = ifAliasMap.entrySet().iterator();
				//show just interfaces with admin status UP
				while(iter.hasNext()) {
					Map.Entry<String,Interface> entry = iter.next();
					if(!AgentUtil.UP.equalsIgnoreCase(entry.getValue().getAdminStatus())){
						iter.remove();
					}
				}
			}
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				for(String ifAlias : ifAliasMap.keySet()) {
					snmp.retrieveInterfaceL2Status(ifAliasMap, deviceIP, deviceDetailsResponse, SNMPUtil.L2_OPERATIONAL_STATUS, SNMPUtil.L2_OPERATIONAL_STATUS + ifAlias);
				}
			}
			//snmp.retrieveInterfaceL2Status(ifAliasMap, deviceIP, deviceDetailsResponse, SNMPUtil.L2_INTERFACE_STATUS);
			String sysUpTime = snmp.retrieveInterfaceSysUpTime(deviceIP, deviceDetailsResponse);
			if (sysUpTime != null && !"".equals(sysUpTime)) {
				String sysuptimeFormated = snmp.retrieveSysUpTime(sysUpTime);
				if (sysuptimeFormated != null && !"".equals(sysuptimeFormated) && ifAliasMap != null && !ifAliasMap.isEmpty()) {
					deviceDetailsResponse.getDeviceDetails().setTime(sysuptimeFormated);
					for(String ifAlias : ifAliasMap.keySet()) {
						snmp.retrieveInterfaceLastStatusChangeL2(ifAliasMap, deviceIP, deviceDetailsResponse, sysuptimeFormated, "ifLastChange." + ifAlias);
					}
				}
			}
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				for(String key : ifAliasMap.keySet()) {
					deviceDetailsResponse.getDeviceDetails().getInterfaces().add(ifAliasMap.get(key));
				}
			}
		} else {
			ErrorResponse errorResponse = null;
			if (deviceDetailsResponse.getErrorResponse() == null) {
				errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.snmp.portName").trim());
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
		return deviceDetailsResponse;
	}
}
