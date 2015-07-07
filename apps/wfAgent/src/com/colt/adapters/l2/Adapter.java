package com.colt.adapters.l2;

import com.colt.ws.biz.IDeviceDetailsResponse;

public abstract class Adapter {

	public abstract IDeviceDetailsResponse fetch(String circuitId, String deviceIP, Integer snmpVersion, String community, String portName, String type, String serviceType, String ocn) throws Exception;
}
