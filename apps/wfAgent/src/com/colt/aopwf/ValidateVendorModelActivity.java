package com.colt.aopwf;

import java.util.Map;

import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class ValidateVendorModelActivity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		if(input != null && input.containsKey("deviceDetails")) {
			DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
			if (DeviceDetailsRequest.TYPE_PE.equalsIgnoreCase(deviceDetails.getType())) {
				input.put("vendor", "Cisco");
				input.put("os", "xr");
				resp = new String[] {"CLIFETCH"};
			} else {
				resp = new String[] {"SNMPFETCH"};
			}
			if(!input.containsKey("l3DeviceDetails")) {
				L3DeviceDetailsResponse l3DeviceDetails = new L3DeviceDetailsResponse();
				l3DeviceDetails.setDeviceDetails(new DeviceDetail());
				l3DeviceDetails.getDeviceDetails().setStatus(deviceDetails.getStatus());
				l3DeviceDetails.setWanIP(deviceDetails.getIp());
				l3DeviceDetails.setCircuitID(deviceDetails.getCircuitID());
				l3DeviceDetails.setResponseID(deviceDetails.getRequestID());
				input.put("l3DeviceDetails", l3DeviceDetails);
			}
		}
		return resp;
	}
}
