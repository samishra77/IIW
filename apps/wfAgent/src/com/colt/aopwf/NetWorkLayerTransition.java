package com.colt.aopwf;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentUtil;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L2DeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class NetWorkLayerTransition implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(NetWorkLayerTransition.class);

	@Override
	public String[] process(Map<String, Object> input) {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = null;
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				if( deviceDetails.getType() != null && 
						(DeviceDetailsRequest.TYPE_PE.equalsIgnoreCase(deviceDetails.getType()) || DeviceDetailsRequest.TYPE_CPE.equalsIgnoreCase(deviceDetails.getType())) ) {
					deviceDetailsResponse = new L3DeviceDetailsResponse();
					resp = new String[] {"L3DEVICE"};
				} else if(DeviceDetailsRequest.TYPE_LAN_LINK.equalsIgnoreCase(deviceDetails.getType())) {
					deviceDetailsResponse = new L2DeviceDetailsResponse();
					resp = new String[] {"L2DEVICE"};
				}
				if(deviceDetailsResponse != null) {
					deviceDetailsResponse.setAssociatedDeviceIp(deviceDetails.getAssociatedDeviceIp());
					deviceDetailsResponse.setDeviceIP(deviceDetails.getIp());
					deviceDetailsResponse.setCircuitID(deviceDetails.getCircuitID());
					deviceDetailsResponse.setResponseID(deviceDetails.getRequestID());
					deviceDetailsResponse.setDeviceDetails(new DeviceDetail());
					if (((List<String>) input.get("WORKFLOW-STATE")).contains("PING_FAIL")) {
						deviceDetailsResponse.getDeviceDetails().setStatus(AgentUtil.DOWN);
					} else {
						deviceDetailsResponse.getDeviceDetails().setStatus(AgentUtil.UP);
					}
					input.put("deviceDetailsResponse", deviceDetailsResponse);
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			if(deviceDetailsResponse == null) {
				deviceDetailsResponse = (IDeviceDetailsResponse) new L3DeviceDetailsResponse();
			}
			DeviceDetail deviceDetails = new DeviceDetail();
			ErrorResponse errorResponse = new ErrorResponse();
			String message = "";
			try {
				message = MessagesErrors.getDefaultInstance().getProperty("networkLayer.error");
			} catch (Exception ex) {
				log.error(ex,ex);
			}
			errorResponse.setMessage(message);
			errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
			if (input != null) {
				DeviceDetailsRequest ddr = (DeviceDetailsRequest) input.get("deviceDetails");
				if (ddr != null) {
					deviceDetailsResponse.setCircuitID(ddr.getCircuitID());
					deviceDetailsResponse.setResponseID(ddr.getRequestID());
				}
			}
			deviceDetailsResponse.setErrorResponse(errorResponse);
			deviceDetailsResponse.setDeviceDetails(deviceDetails);
			input.put("deviceDetailsResponse", deviceDetailsResponse);
		}
		return resp;
	}
}
