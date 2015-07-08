package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.l2.FactoryAdapter;
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
			if(DeviceDetailsRequest.TYPE_LAN_LINK.equalsIgnoreCase(deviceDetails.getType()) && 
					deviceDetails.getDeviceType() != null && FactoryAdapter.VENDOR_ATRICA.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor())) {
				resp = new String[] {"FETCH_DEVICE_DONE", "PING_SUCCESS"};
			} else if(deviceDetails.getIp() != null && !"".equals(deviceDetails.getIp())) {
				resp = new String[] {"FETCH_DEVICE_DONE"};
			}
		}
		return resp;
	}
}
