package com.colt.aopwf;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.l2.FactoryAdapter;
import com.colt.util.AgentUtil;
import com.colt.util.AptUtil;
import com.colt.util.MessagesErrors;
import com.colt.ws.EIPCall;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;

public class FetchAPTDeviceIPActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(FetchAPTDeviceIPActivity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		String ipAddress = null;
		IDeviceDetailsResponse deviceDetailsResponse = AgentUtil.discoverDeviceDetailsResponse(input);
		try {
			List<String> list = null;
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				String associatedDeviceIp = null;
				AptUtil aptUtil = new AptUtil();
				// Find CPE mgmt address in APT
				if (!deviceDetails.getType().equals("LANLink")) {
					if(deviceDetails != null && deviceDetails.getAssociatedDevice() != null && (deviceDetails.getAssociatedDeviceIp() == null || "".equals(deviceDetails.getAssociatedDeviceIp()))) {
						if(deviceDetails != null && deviceDetails.getAssociatedDevice() != null && !deviceDetails.getAssociatedDevice().equals("")) {
							associatedDeviceIp = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getAssociatedDevice(), deviceDetails.getType(), null, null, deviceDetailsResponse);
							deviceDetails.setAssociatedDeviceIp(associatedDeviceIp);
						}
					}
				}

				if(deviceDetails != null && deviceDetails.getName() != null) {
					if (FactoryAdapter.VENDOR_ACTELIS.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor())) {
						list = new EIPCall().process(deviceDetails.getName());
						if (list != null && list.size() == 1) {
							ipAddress = list.get(0);
						}
					} else {
						ipAddress = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getName(), deviceDetails.getType(), deviceDetails.getXngNetworkObjectName(), deviceDetails.getXngSlotNumber(), deviceDetailsResponse);
						if((ipAddress == null || "".equals(ipAddress)) && !deviceDetails.getType().equals("LANLink")) {
							if (deviceDetails.getName() != null && deviceDetails.getName().startsWith("lo0-")) {
								String devName = "*" + deviceDetails.getName().substring(4,deviceDetails.getName().length());
								ipAddress = aptUtil.retrieveAddressByDeviceNameFromAPT(devName, deviceDetails.getType(), null, null, deviceDetailsResponse);
							}
						}
					}
				}

				if (!FactoryAdapter.VENDOR_ACTELIS.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor())) {
					if ((ipAddress == null || ipAddress.equals("")) && deviceDetails.getType().equals("LANLink")) {
						ipAddress = deviceDetails.getIp();
					}
				}

				if(ipAddress != null && !"".equals(ipAddress)) {
					deviceDetails.setIp(ipAddress);
					String vendor = deviceDetails.getDeviceType() != null ? deviceDetails.getDeviceType().getVendor() : "";
					if (FactoryAdapter.VENDOR_ACCEDIAN.toUpperCase().equals(vendor.toUpperCase()) || 
						FactoryAdapter.VENDOR_OVERTURE.toUpperCase().equals(vendor.toUpperCase())){
						input.put("deviceDetailsResponse", deviceDetailsResponse);
					}
					resp = new String[] {"FETCH_DEVICE_DONE"};
				} else {
					String errorMsg = null;
					if (FactoryAdapter.VENDOR_ACTELIS.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor())) {
						if (list == null || list.isEmpty()) {
							errorMsg = MessagesErrors.getDefaultInstance().getProperty("error.eip.noip");
						} else if (list.size() > 1){
							errorMsg = MessagesErrors.getDefaultInstance().getProperty("error.eip.tomanyip");
						}
					} else {
						errorMsg = MessagesErrors.getDefaultInstance().getProperty("apt.mgmtIPNotFound");
					}
					DeviceDetail dd = new DeviceDetail();
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setMessage(errorMsg);
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					deviceDetailsResponse.setErrorResponse(errorResponse);
					deviceDetailsResponse.setDeviceDetails(dd);
					input.put("deviceDetailsResponse", deviceDetailsResponse);
				}
			}
		} catch (Exception e) {
			log.error(e,e);
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
