package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.apt.business.Device;
import com.colt.util.AptUtil;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class FetchAPTDeviceIPActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(FetchAPTDeviceIPActivity.class);

	public String getIpAddress (Device[] deviceArray, String deviceName, String type) {
		String ipAddress = null;
		if (deviceArray != null && deviceArray.length > 0) {
			for (Device dev : deviceArray) {
				if ( dev.getAddress() != null && !"".equals(dev.getAddress())) {
					if (type.equals("LANLink")) {
						String devName = deviceName.contains("*") ? deviceName.replace("*", "") : deviceName;
						if (dev.getName().equalsIgnoreCase(devName)) {
							ipAddress = dev.getAddress();
							break;
						} else {
							if (dev.getName().startsWith(devName+"-.")) {
								ipAddress = dev.getAddress();
								break;
							} else if (dev.getName().startsWith(devName+"_.")) {
								ipAddress = dev.getAddress();
								break;
							} else if (dev.getName().startsWith(devName+"-10G.")) {
								ipAddress = dev.getAddress();
								break;
							} else if (dev.getName().startsWith(devName+"_10G.")) {
								ipAddress = dev.getAddress();
								break;
							}
						}
					} else {
						ipAddress = deviceArray[0].getAddress();
						break;
					}
				}
			}
		}
		return ipAddress;
	}

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				String associatedDeviceIp = null;
				AptUtil aptUtil = new AptUtil();
				if (deviceDetails.getType() != null && deviceDetails.getType().equals("LANLink")) {
					Device[] deviceArray = null;
					String ipAddress = null;
					if(deviceDetails != null && deviceDetails.getXngNetworkObjectName() != null && !deviceDetails.getXngNetworkObjectName().equals("")) {
						deviceArray = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getXngNetworkObjectName());
						ipAddress = getIpAddress(deviceArray, deviceDetails.getXngNetworkObjectName(), deviceDetails.getType());
					}

					if (ipAddress == null || ipAddress.equals("")) {
						if(deviceDetails != null && deviceDetails.getName() != null && !deviceDetails.getName().equals("")) {
							deviceArray = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getName());
							ipAddress = getIpAddress(deviceArray, deviceDetails.getName(), deviceDetails.getType());
						}
					}

					if (ipAddress == null || ipAddress.equals("")) {
						if(deviceDetails != null && deviceDetails.getName() != null && !deviceDetails.getName().equals("")) {
							deviceArray = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getName() + "*");
							ipAddress = getIpAddress(deviceArray, deviceDetails.getName() + "*", deviceDetails.getType());
						}
					}

					if(ipAddress != null && !"".equals(ipAddress)) {
						deviceDetails.setIp(ipAddress);
						resp = new String[] {"FETCH_DEVICE_DONE"};
					} else {
						IDeviceDetailsResponse deviceDetailsResponse = (IDeviceDetailsResponse) new L3DeviceDetailsResponse();
						DeviceDetail dd = new DeviceDetail();
						ErrorResponse errorResponse = new ErrorResponse();
						errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("apt.mgmtIPNotFound"));
						errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
						deviceDetailsResponse.setErrorResponse(errorResponse);
						deviceDetailsResponse.setDeviceDetails(dd);
						input.put("deviceDetailsResponse", deviceDetailsResponse);
					}
				} else {
					// Find CPE mgmt address in APT
					if(deviceDetails != null && deviceDetails.getAssociatedDevice() != null && (deviceDetails.getAssociatedDeviceIp() == null || "".equals(deviceDetails.getAssociatedDeviceIp()))) {
						Device[] deviceArray = null;
						if(deviceDetails != null && deviceDetails.getAssociatedDevice() != null && !deviceDetails.getAssociatedDevice().equals("")) {
							deviceArray = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getAssociatedDevice());
							associatedDeviceIp = getIpAddress(deviceArray, deviceDetails.getAssociatedDevice(), deviceDetails.getServiceType());
							deviceDetails.setAssociatedDeviceIp(associatedDeviceIp);
						}
					}

					if(deviceDetails != null && deviceDetails.getName() != null) {
						Device[] deviceArray = aptUtil.retrieveAddressByDeviceNameFromAPT(deviceDetails.getName());
						String ipAddress = null;
						ipAddress = getIpAddress(deviceArray, deviceDetails.getName(), deviceDetails.getType());
						if(ipAddress != null && !"".equals(ipAddress)) {
							deviceDetails.setIp(ipAddress);
							resp = new String[] {"FETCH_DEVICE_DONE"};
						} else {
							if (deviceDetails.getName() != null && deviceDetails.getName().startsWith("lo0-")) {
								String devName = "*" + deviceDetails.getName().substring(4,deviceDetails.getName().length());
								deviceArray = aptUtil.retrieveAddressByDeviceNameFromAPT(devName);
								ipAddress = getIpAddress(deviceArray, deviceDetails.getName(), deviceDetails.getType());
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
