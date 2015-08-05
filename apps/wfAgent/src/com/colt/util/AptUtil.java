package com.colt.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.apt.business.User;
import com.colt.common.aptcache.IDeviceDAO;
import com.colt.apt.business.Device;

import electric.registry.Registry;

public class AptUtil {

	private Log log = LogFactory.getLog(AptUtil.class);

	public String retrieveAddressByDeviceNameFromAPT(String deviceName, String type, String networkObjectName, String xngSlotNumber) {
		try {
			if(deviceName != null && !"".equals(deviceName)) {
				String baseUrl = AgentConfig.getDefaultInstance().getProperty("apt.baseUrl");
				String userName = AgentConfig.getDefaultInstance().getProperty("apt.userName");
				String userPass = AgentConfig.getDefaultInstance().getProperty("apt.userPass");
				User user = new User();
				user.setUsername(userName);
				user.setPassword(userPass);

				IDeviceDAO deviceDAO = (IDeviceDAO) Registry.bind(baseUrl+"/aptCache/services/DeviceDAO.wsdl",IDeviceDAO.class);
				String address = null;
				if (!type.equalsIgnoreCase("LANLink")) {
					Device[] deviceArray = deviceDAO.retrieveDevicesByName(user, deviceName);
					if (deviceArray != null && deviceArray.length > 0) {
						address = deviceArray[0].getAddress();
					}
				} else {
					xngSlotNumber = processSlot(xngSlotNumber);
					address = deviceDAO.retrieveIpaddressByDeviceName(user, deviceName, type, networkObjectName, xngSlotNumber);
				}
				return address;
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return null;
	}

	private String processSlot(String slotNumber) {
		if(slotNumber != null && !"".equals(slotNumber)) {
			try {
				int slot = Integer.valueOf(slotNumber);
				if(slot == 0) {
					slotNumber = null;
				}
			} catch (Exception e) {
				// Empty
			}
		}
		return slotNumber;
	}
}
