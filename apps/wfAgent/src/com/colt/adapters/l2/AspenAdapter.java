package com.colt.adapters.l2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L2DeviceDetailsResponse;

public class AspenAdapter extends Adapter {

	private Log log = LogFactory.getLog(AspenAdapter.class);

	public IDeviceDetailsResponse fetch(String circuitId, String deviceIP, Integer snmpVersion, String community, String portName, String type, String serviceType, String ocn) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L2DeviceDetailsResponse();
		return deviceDetailsResponse;
	}
}
