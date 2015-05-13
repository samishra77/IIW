package com.colt.aopwf;

import java.util.Map;

import com.colt.ws.biz.DeviceDetailsRequest;

public class ReceiveDeviceDetailsActivity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		if(input != null && input.containsKey("deviceDetails")) {
			DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
			if(deviceDetails.getIp() != null && !"".equals(deviceDetails.getIp())) {
				resp = new String[] {"FETCH_DEVICE_DONE"};
			}
		}
		return resp;
	}
}
