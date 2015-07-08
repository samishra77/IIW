package com.colt.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.colt.agents.DeviceDetailsAgent;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L2DeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;

@RestController
public class DeviceDetail {

	private Log log = LogFactory.getLog(DeviceDetail.class);

	@RequestMapping(value = "/getDeviceDetails", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getDeviceDetails(@RequestBody DeviceDetailsRequest deviceDetail) {
		log.info("[" + deviceDetail.getSeibelUserID() + "] Entering method getDeviceDetails()");
		IDeviceDetailsResponse deviceDetailsResponse = null;
		try {
			DeviceDetailsAgent deviceDetailsAgent = new DeviceDetailsAgent();
			deviceDetailsResponse = deviceDetailsAgent.execute(deviceDetail);
			if(deviceDetailsResponse == null) {
				if(deviceDetail != null && deviceDetail.getType() != null && 
						(DeviceDetailsRequest.TYPE_PE.equalsIgnoreCase(deviceDetail.getType()) || DeviceDetailsRequest.TYPE_CPE.equalsIgnoreCase(deviceDetail.getType()))) {
					deviceDetailsResponse = new L3DeviceDetailsResponse();
				} else if(DeviceDetailsRequest.TYPE_LAN_LINK.equalsIgnoreCase(deviceDetail.getType())) {
					deviceDetailsResponse = new L2DeviceDetailsResponse();
				}
			}
			deviceDetailsResponse.setResponseID(deviceDetail.getRequestID());
			deviceDetailsResponse.setCircuitID(deviceDetail.getCircuitID());
		} catch (Exception e) {
			log.error("[" + deviceDetail.getSeibelUserID() + "] " + e, e);
		}
		log.info("[" + deviceDetail.getSeibelUserID() + "] Exit method getDeviceDetails()");
		return deviceDetailsResponse;
	}

}
