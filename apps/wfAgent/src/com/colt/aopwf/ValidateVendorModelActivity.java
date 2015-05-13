package com.colt.aopwf;

import java.util.Map;

import com.colt.ws.biz.DeviceDetailsRequest;

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
		}
		return resp;
	}
}
