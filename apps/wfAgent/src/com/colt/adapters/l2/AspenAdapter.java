package com.colt.adapters.l2;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.apt.business.Endpoint;
import com.colt.common.aptservices.router.aspen.IAspen;
import com.colt.common.aspenwrapper.business.DeviceInfo;
import com.colt.common.aspenwrapper.business.DeviceInfoResponse;
import com.colt.common.aspenwrapper.business.DevicePortsResponse;
import com.colt.common.aspenwrapper.business.Port;
import com.colt.util.AgentConfig;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L2DeviceDetailsResponse;

import electric.registry.Registry;

public class AspenAdapter extends Adapter {

	private Log log = LogFactory.getLog(AccedianAdapter.class);

	public IDeviceDetailsResponse fetch(String circuitId, String deviceIP, Integer snmpVersion, String community, String portName, String type, String serviceType, String ocn) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L2DeviceDetailsResponse();
		try {
			DeviceDetail deviceDetail = new DeviceDetail();
			deviceDetailsResponse.setDeviceDetails(deviceDetail);
			String baseUrl = AgentConfig.getDefaultInstance().getProperty("apt.baseUrl");
			IAspen aspen = (IAspen) Registry.bind(baseUrl + "/aptServices/services/Aspen.wsdl",IAspen.class);
			Endpoint[] endPoints = aspen.getServiceEndpoints(circuitId, ocn);
			List<Interface> interfaceList = new ArrayList<Interface>();
			if (endPoints != null && endPoints.length > 0) {
				for (Endpoint end : endPoints) {
					String ip = end.getDeviceIP();
					DeviceInfoResponse deviceInfRes = aspen.getDeviceInfo(ip);
					String deviceId = null;
					if(deviceInfRes != null) {
						DeviceInfo deviceInfo = deviceInfRes.getDeviceInfo();
						if(deviceInfo != null) {
							deviceId = deviceInfo.getId();
						}
					}
					DevicePortsResponse devicePortRes =  aspen.getAllDevicePorts(deviceId);
					if(deviceInfRes != null) {
						Port[] port = devicePortRes.getPorts();
						if(port != null && port.length > 0 ){
							for(int i = 0; i < port.length ; i++) {
								Interface inf = new Interface();
								inf.setOpStatus(port[i].getOperStatus());
								inf.setAdminStatus(port[i].getStatus());
								inf.setName(port[i].getName());
								interfaceList.add(inf);
							}
						}
					}
				}
			}
			if (!interfaceList.isEmpty()) {
				deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(interfaceList);
			}
		} catch (Exception e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.emsapi.atrica").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
		return deviceDetailsResponse;
	}
}
