package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.apt.business.Device;
import com.colt.apt.business.User;
import com.colt.common.aptcache.IDeviceDAO;
import com.colt.util.AgentConfig;
import com.colt.ws.biz.DeviceDetailsRequest;

import electric.registry.Registry;

public class FetchAPTDeviceIPActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(FetchAPTDeviceIPActivity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				if(deviceDetails != null && deviceDetails.getName() != null) {
					String baseUrl = AgentConfig.getDefaultInstance().getProperty("apt.baseUrl");
					String userName = AgentConfig.getDefaultInstance().getProperty("apt.userName");
					String userPass = AgentConfig.getDefaultInstance().getProperty("apt.userPass");
					User user = new User();
					user.setUsername(userName);
					user.setPassword(userPass);

					IDeviceDAO deviceDAO = (IDeviceDAO) Registry.bind(baseUrl+"/aptCache/services/DeviceDAO.wsdl",IDeviceDAO.class);
					Device[] deviceArray = deviceDAO.retrieveDevicesByName(user, deviceDetails.getName());
					if(deviceArray != null && deviceArray.length == 1 && deviceArray[0].getAddress() != null && !"".equals(deviceArray[0].getAddress())) {
						int index = deviceArray[0].getAddress().lastIndexOf(".");
						if(index > -1) {
							String lastOctet = deviceArray[0].getAddress().substring(index+1, deviceArray[0].getAddress().length());
							String partialAddress = deviceArray[0].getAddress().substring(0, deviceArray[0].getAddress().lastIndexOf(".")+1);
							int octet = 0;
							try {
								octet = Integer.valueOf(lastOctet);
								if(octet > 0) {
									octet = octet - 1;
								}
								partialAddress+= octet;
								deviceDetails.setIp(partialAddress);
							}catch (Exception e) {
								log.error(e,e);
							}
						}
						resp = new String[] {"FETCH_DEVICE_DONE"};
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return resp;
	}
}
