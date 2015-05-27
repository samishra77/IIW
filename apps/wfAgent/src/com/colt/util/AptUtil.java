package com.colt.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.apt.business.Device;
import com.colt.apt.business.User;
import com.colt.common.aptcache.IDeviceDAO;

import electric.registry.Registry;

public class AptUtil {

	private Log log = LogFactory.getLog(AptUtil.class);

	public String retrieveAddressByDeviceNameFromAPT(String deviceName) {
		String ipAdrress = null;
		try {
			if(deviceName != null && !"".equals(deviceName)) {
				String baseUrl = AgentConfig.getDefaultInstance().getProperty("apt.baseUrl");
				String userName = AgentConfig.getDefaultInstance().getProperty("apt.userName");
				String userPass = AgentConfig.getDefaultInstance().getProperty("apt.userPass");
				User user = new User();
				user.setUsername(userName);
				user.setPassword(userPass);

				IDeviceDAO deviceDAO = (IDeviceDAO) Registry.bind(baseUrl+"/aptCache/services/DeviceDAO.wsdl",IDeviceDAO.class);
				Device[] deviceArray = deviceDAO.retrieveDevicesByName(user, deviceName);
				if(deviceArray != null && deviceArray.length == 1 && deviceArray[0].getAddress() != null && !"".equals(deviceArray[0].getAddress())) {
					ipAdrress = deviceArray[0].getAddress();
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return ipAdrress;
	}
}
