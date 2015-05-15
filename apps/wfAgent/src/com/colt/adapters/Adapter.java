package com.colt.adapters;

import com.colt.ws.biz.DeviceDetail;


public abstract class Adapter {

	public abstract DeviceDetail fetch(String circuitID, String ipAddress, int snmpVersion) throws Exception;
}
