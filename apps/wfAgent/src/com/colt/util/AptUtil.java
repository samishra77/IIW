package com.colt.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.apt.business.Device;
import com.colt.apt.business.User;
import com.colt.common.aptcache.IDeviceDAO;

import electric.registry.Registry;

public class AptUtil {

	private Log log = LogFactory.getLog(AptUtil.class);

	public Device[] retrieveAddressByDeviceNameFromAPT(String deviceName) {
		Device[] deviceArray = null;
		try {
			if(deviceName != null && !"".equals(deviceName)) {
				String baseUrl = AgentConfig.getDefaultInstance().getProperty("apt.baseUrl");
				String userName = AgentConfig.getDefaultInstance().getProperty("apt.userName");
				String userPass = AgentConfig.getDefaultInstance().getProperty("apt.userPass");
				User user = new User();
				user.setUsername(userName);
				user.setPassword(userPass);

				IDeviceDAO deviceDAO = (IDeviceDAO) Registry.bind(baseUrl+"/aptCache/services/DeviceDAO.wsdl",IDeviceDAO.class);
				deviceArray = deviceDAO.retrieveDevicesByName(user, deviceName);
				return deviceArray;
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return deviceArray;
	}
}
