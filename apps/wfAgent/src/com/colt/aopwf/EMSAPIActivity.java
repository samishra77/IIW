package com.colt.aopwf;

import java.util.Map;

import com.colt.adapters.l2.Adapter;
import com.colt.adapters.l2.AspenAdapter;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L2DeviceDetailsResponse;

public class EMSAPIActivity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		return emsapiFetch(input);
	}

	private String[] emsapiFetch(Map<String,Object> input) {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = null;
		if(input.containsKey("deviceDetailsResponse")) {
			if(input.get("deviceDetailsResponse") instanceof L2DeviceDetailsResponse) {
				deviceDetailsResponse = (L2DeviceDetailsResponse) input.get("deviceDetailsResponse");
			}
		}
		if(input != null && deviceDetailsResponse != null && input.containsKey("deviceDetails")) {
			try {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				Adapter adapter = new AspenAdapter();
				IDeviceDetailsResponse ddr = adapter.fetch(deviceDetailsResponse.getCircuitID(), deviceDetailsResponse.getDeviceIP(), null, null, deviceDetails.getPortName(), deviceDetails.getType(), null, deviceDetails.getOcn(), deviceDetails.getName());
				if(ddr != null && ddr.getDeviceDetails() != null && deviceDetailsResponse.getDeviceDetails() != null) {
					deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(ddr.getDeviceDetails().getInterfaces());
					if (ddr.getDeviceIP() != null) {
						deviceDetailsResponse.setDeviceIP(ddr.getDeviceIP());
					}
					if(ddr.getErrorResponse() != null) {
						if (deviceDetailsResponse.getErrorResponse() != null) {
							ErrorResponse adapterEr = ddr.getErrorResponse();
							ErrorResponse er = deviceDetailsResponse.getErrorResponse();
							if (adapterEr.getFailedConn() != null && adapterEr.getFailedConn().size() > 0) {
								er.getFailedConn().addAll(adapterEr.getFailedConn());
							}
							if (adapterEr.getFailedSnmp() != null && adapterEr.getFailedSnmp().size() > 0) {
								er.getFailedSnmp().addAll(adapterEr.getFailedSnmp());
							}
						} else {
							deviceDetailsResponse.setErrorResponse(ddr.getErrorResponse());
						}
					}
				}
				resp = new String[] {"SENDRESPONSE"};
			} catch (Exception e) {
				if(deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setMessage(e.toString());
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
			}
		}
		resp = new String[] {"SENDRESPONSE"};
		return resp;
	}
}
