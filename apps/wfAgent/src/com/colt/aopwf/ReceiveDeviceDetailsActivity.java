package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.ws.biz.DeviceDetailsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ReceiveDeviceDetailsActivity implements IWorkflowProcessActivity {

	private static Log log = LogFactory.getLog(ReceiveDeviceDetailsActivity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		if(input != null && input.containsKey("deviceDetails")) {
			DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
			if (log.isDebugEnabled()) {
				try {
					ObjectWriter ow = new ObjectMapper().writer();
					String json = ow.writeValueAsString(deviceDetails);
					log.debug("DeviceDetailsRequest: " + json);
				} catch (JsonProcessingException e) {
					// Empty
				}
			}
			if(deviceDetails.getIp() != null && !"".equals(deviceDetails.getIp())) {
				resp = new String[] {"FETCH_DEVICE_DONE"};
			}
		}
		return resp;
	}
}
