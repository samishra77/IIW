package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.Adapter;
import com.colt.adapters.FactoryAdapter;
import com.colt.apt.business.Device;
import com.colt.apt.business.User;
import com.colt.common.aptcache.IDeviceDAO;
import com.colt.util.AgentConfig;
import com.colt.util.AgentUtil;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;

import electric.registry.Registry;

public class CLIFetchActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(CLIFetchActivity.class);

	public String[] process(Map<String,Object> input)  {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = null;
		if(input.containsKey("deviceDetailsResponse")) {
			if(input.get("deviceDetailsResponse") instanceof L3DeviceDetailsResponse) {
				deviceDetailsResponse = (L3DeviceDetailsResponse) input.get("deviceDetailsResponse");
			} else {
				//L2 Cast
			}
		}
		if(input != null && input.containsKey("vendor") && input.containsKey("os") && deviceDetailsResponse != null) {
			String cpeMgmtIp = null;
			try {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				// Find CPE mgmt address in APT
				if(deviceDetails != null && deviceDetails.getAssociatedDevice() != null) {
					String baseUrl = AgentConfig.getDefaultInstance().getProperty("apt.baseUrl");
					String userName = AgentConfig.getDefaultInstance().getProperty("apt.userName");
					String userPass = AgentConfig.getDefaultInstance().getProperty("apt.userPass");
					User user = new User();
					user.setUsername(userName);
					user.setPassword(userPass);

					IDeviceDAO deviceDAO = (IDeviceDAO) Registry.bind(baseUrl+"/aptCache/services/DeviceDAO.wsdl",IDeviceDAO.class);
					Device[] deviceArray = deviceDAO.retrieveDevicesByName(user, deviceDetails.getAssociatedDevice());
					if(deviceArray != null && deviceArray.length == 1 && deviceArray[0].getAddress() != null && !"".equals(deviceArray[0].getAddress())) {
						cpeMgmtIp = deviceArray[0].getAddress();
					}
				}
				if (cpeMgmtIp == null) {
					if(deviceDetailsResponse.getErrorResponse() == null) {
						ErrorResponse errorResponse = new ErrorResponse();
						errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("apt.mgmtIPNotFound.cpe"));
						errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
						deviceDetailsResponse.setErrorResponse(errorResponse);
					}
				} else {
					// Call adaptor implementation
					String vendor = (String) input.get("vendor");
					String os = (String) input.get("os");
					FactoryAdapter factoryAdapter = new FactoryAdapter();
					Adapter adapter = factoryAdapter.getAdapter(vendor, os);
					String wanIP = AgentUtil.calculateWanIp(cpeMgmtIp);
					if(adapter != null) {
							Integer snmpVersion = (Integer) input.get("snmpVersion");
							IDeviceDetailsResponse ddr = adapter.fetch(deviceDetailsResponse.getCircuitID(), deviceDetailsResponse.getDeviceIP(), snmpVersion, wanIP);
							if(ddr != null && ddr.getDeviceDetails() != null && deviceDetailsResponse.getDeviceDetails() != null) {
								deviceDetailsResponse.getDeviceDetails().setTime(ddr.getDeviceDetails().getTime());
								deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(ddr.getDeviceDetails().getInterfaces());
								if(ddr.getErrorResponse() != null) {
									deviceDetailsResponse.setErrorResponse(ddr.getErrorResponse());
								}
							}
							resp = new String[] {"SENDRESPONSE"};
					}
				}
			} catch (Exception e) {
				log.error(e,e);
				if(deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setMessage(e.toString());
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
			}
		}
		return resp;
	}
}
