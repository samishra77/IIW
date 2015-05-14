package com.colt.adapters;

import com.colt.connect.ConnectSSH;
import com.colt.connect.ConnectTelnet;
import com.colt.ws.biz.DeviceDetail;

public class HuaweiAdapter extends Adapter {

	@Override
	public DeviceDetail fetch(String circuitID, String ipAddress) throws Exception {
		DeviceDetail deviceDetail = new DeviceDetail();
		if(ipAddress != null && !"".equals(ipAddress) && circuitID != null && !"".equals(circuitID)) {
			try {
				ConnectTelnet telnetdev = new ConnectTelnet();
				telnetdev.connect(ipAddress, 15);
				telnetdev.prepareForCommands(FactoryAdapter.VENDOR_CISCO);
//				executeCommands(telnetdev, null, ipAddress, circuitID, deviceDetail);
			} catch (Exception e) {
				try {
					ConnectSSH sshdev = new ConnectSSH();
					sshdev.connect(ipAddress, 15);
					sshdev.prepareForCommands(FactoryAdapter.VENDOR_CISCO);
//					executeCommands(null, sshdev, ipAddress, circuitID, deviceDetail);
				} catch (Exception e2) {
					throw e2;
				}
			}
		}
		return deviceDetail;
	}
}
