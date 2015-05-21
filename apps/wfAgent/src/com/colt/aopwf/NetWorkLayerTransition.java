package com.colt.aopwf;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
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
					l3DeviceDetails.setWanIP(deviceDetails.getIp());
					l3DeviceDetails.setCircuitID(deviceDetails.getCircuitID());
					l3DeviceDetails.setResponseID(deviceDetails.getRequestID());
					if (((List<String>) input.get("WORKFLOW-STATE")).contains("PING_FAIL")) {
						l3DeviceDetails.getDeviceDetails().setStatus(AgentUtil.DOWN);
					} else {
						l3DeviceDetails.getDeviceDetails().setStatus(AgentUtil.UP);
					}
					input.put("l3DeviceDetails", l3DeviceDetails);
					resp = new String[] {"L3DEVICE"};
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			L3DeviceDetailsResponse l3DeviceDetails = new L3DeviceDetailsResponse();
			DeviceDetail deviceDetails = new DeviceDetail();
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage("Populate Network Layer failed.");
			errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
			if (input != null) {
				DeviceDetailsRequest ddr = (DeviceDetailsRequest) input.get("deviceDetails");
				if (ddr != null) {
					errorResponse.getFailedConn().add(ddr.getIp());
					l3DeviceDetails.setWanIP(ddr.getIp());
					l3DeviceDetails.setCircuitID(ddr.getCircuitID());
					l3DeviceDetails.setResponseID(ddr.getRequestID());
				}
			}
			l3DeviceDetails.setErrorResponse(errorResponse);
			l3DeviceDetails.setDeviceDetails(deviceDetails);
			input.put("l3DeviceDetails", l3DeviceDetails);
		}
		return resp;
	}
}
