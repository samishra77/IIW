package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class NetWorkLayerTransition implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(NetWorkLayerTransition.class);

	@Override
	public String[] process(Map<String, Object> input) {
		String[] resp = null;
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				if( deviceDetails.getType() != null && 
						(DeviceDetailsRequest.TYPE_PE.equalsIgnoreCase(deviceDetails.getType()) || DeviceDetailsRequest.TYPE_CPE.equalsIgnoreCase(deviceDetails.getType())) ) {
					L3DeviceDetailsResponse l3DeviceDetails = new L3DeviceDetailsResponse();
					l3DeviceDetails.setDeviceDetails(new DeviceDetail());
					l3DeviceDetails.getDeviceDetails().setStatus(deviceDetails.getStatus());
					l3DeviceDetails.setWanIP(deviceDetails.getIp());
					l3DeviceDetails.setCircuitID(deviceDetails.getCircuitID());
					l3DeviceDetails.setResponseID(deviceDetails.getRequestID());
					input.put("l3DeviceDetails", l3DeviceDetails);
					resp = new String[] {"L3DEVICE"};
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return resp;
	}
}
