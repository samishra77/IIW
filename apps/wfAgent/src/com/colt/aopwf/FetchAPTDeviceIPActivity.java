package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentUtil;
import com.colt.util.AptUtil;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;

public class FetchAPTDeviceIPActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(FetchAPTDeviceIPActivity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		String ipAddress = null;
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				String associatedDeviceIp = null;
				AptUtil aptUtil = new AptUtil();
				// Find CPE mgmt address in APT
				if (!deviceDetails.getType().equals("LANLink")) {
					if(deviceDetails != null && deviceDetails.getAssociatedDevice() != null && (deviceDetails.getAssociatedDeviceIp() == null || "".equals(deviceDetails.getAssociatedDeviceIp()))) {
						if(deviceDetails != null && deviceDetails.getAssociatedDevice() != null && !deviceDetails.getAssociatedDevice().equals("")) {
							associatedDeviceIp = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getAssociatedDevice(), deviceDetails.getType(), null, null);
							deviceDetails.setAssociatedDeviceIp(associatedDeviceIp);
						}
					}
				}

				if(deviceDetails != null && deviceDetails.getName() != null) {
					ipAddress = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getName(), deviceDetails.getType(), deviceDetails.getXngNetworkObjectName(), deviceDetails.getXngSlotNumber());
					if((ipAddress == null || "".equals(ipAddress)) && !deviceDetails.getType().equals("LANLink")) {
						if (deviceDetails.getName() != null && deviceDetails.getName().startsWith("lo0-")) {
							String devName = "*" + deviceDetails.getName().substring(4,deviceDetails.getName().length());
							ipAddress = aptUtil.retrieveAddressByDeviceNameFromAPT(devName, deviceDetails.getType(), null, null);
						}
					}
				}

				if ((ipAddress == null || ipAddress.equals("")) && deviceDetails.getType().equals("LANLink")) {
					ipAddress = deviceDetails.getIp();
				}

				if(ipAddress != null && !"".equals(ipAddress)) {
					deviceDetails.setIp(ipAddress);
					resp = new String[] {"FETCH_DEVICE_DONE"};
				} else {
					IDeviceDetailsResponse deviceDetailsResponse = AgentUtil.discoverDeviceDetailsResponse(input);
					DeviceDetail dd = new DeviceDetail();
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("apt.mgmtIPNotFound"));
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					deviceDetailsResponse.setErrorResponse(errorResponse);
					deviceDetailsResponse.setDeviceDetails(dd);
					input.put("deviceDetailsResponse", deviceDetailsResponse);
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			IDeviceDetailsResponse deviceDetailsResponse = AgentUtil.discoverDeviceDetailsResponse(input);
			DeviceDetail dd = new DeviceDetail();
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage(e.toString());
			errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
			deviceDetailsResponse.setErrorResponse(errorResponse);
			deviceDetailsResponse.setDeviceDetails(dd);
			input.put("deviceDetailsResponse", deviceDetailsResponse);
		}
		return resp;
	}
}
