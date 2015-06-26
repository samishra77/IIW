package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AptUtil;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class FetchAPTDeviceIPActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(FetchAPTDeviceIPActivity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				String associatedDeviceIp = null;
				AptUtil aptUtil = new AptUtil();
				// Find CPE mgmt address in APT
				if(deviceDetails != null && deviceDetails.getAssociatedDevice() != null && (deviceDetails.getAssociatedDeviceIp() == null || "".equals(deviceDetails.getAssociatedDeviceIp()))) {
					associatedDeviceIp = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getAssociatedDevice());
					deviceDetails.setAssociatedDeviceIp(associatedDeviceIp);
				}
				if(deviceDetails != null && deviceDetails.getName() != null) {
					String ipAddress = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getName());
					if(ipAddress != null && !"".equals(ipAddress)) {
						deviceDetails.setIp(ipAddress);
						resp = new String[] {"FETCH_DEVICE_DONE"};
					} else {
						if (deviceDetails.getName() != null && deviceDetails.getName().startsWith("lo0-")) {
							String devName = "*" + deviceDetails.getName().substring(4,deviceDetails.getName().length());
							ipAddress = aptUtil.retrieveAddressByDeviceNameFromAPT(devName);
							if(ipAddress != null && !"".equals(ipAddress)) {
								deviceDetails.setIp(ipAddress);
								resp = new String[] {"FETCH_DEVICE_DONE"};
								return resp;
							}
						}

						IDeviceDetailsResponse deviceDetailsResponse = (IDeviceDetailsResponse) new L3DeviceDetailsResponse();
						DeviceDetail dd = new DeviceDetail();
						ErrorResponse errorResponse = new ErrorResponse();
						errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("apt.mgmtIPNotFound"));
						errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
						deviceDetailsResponse.setErrorResponse(errorResponse);
						deviceDetailsResponse.setDeviceDetails(dd);
						input.put("deviceDetailsResponse", deviceDetailsResponse);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			IDeviceDetailsResponse deviceDetailsResponse = (IDeviceDetailsResponse) new L3DeviceDetailsResponse();
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
