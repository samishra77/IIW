package com.colt.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.colt.agents.DeviceDetailsAgent;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.L3DeviceDetailsResponse;

@RestController
public class DeviceDetail {

	private Log log = LogFactory.getLog(DeviceDetail.class);

	@RequestMapping(value = "/getDeviceDetails", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getDeviceDetails(@RequestBody DeviceDetailsRequest deviceDetail) {
		log.info("[" + deviceDetail.getSeibelUserID() + "] Entering method getDeviceDetails()");
		L3DeviceDetailsResponse l3DeviceDetails = new L3DeviceDetailsResponse();
		try {
			DeviceDetailsAgent deviceDetailsAgent = new DeviceDetailsAgent();
			l3DeviceDetails = deviceDetailsAgent.execute(deviceDetail);
		} catch (Exception e) {
			log.error("[" + deviceDetail.getSeibelUserID() + "] " + e, e);
		}
		log.info("[" + deviceDetail.getSeibelUserID() + "] Exit method getDeviceDetails()");
		return l3DeviceDetails;
	}
}
