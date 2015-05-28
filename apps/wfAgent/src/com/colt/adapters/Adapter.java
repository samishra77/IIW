package com.colt.adapters;

import com.colt.ws.biz.IDeviceDetailsResponse;

public abstract class Adapter {

	public abstract IDeviceDetailsResponse fetch(String circuitID, String deviceIP, Integer snmpVersion, String wanIP, String community) throws Exception;
}
