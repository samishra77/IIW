package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.Adapter;
import com.colt.adapters.FactoryAdapter;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;
import com.jcraft.jsch.JSchException;

public class CLIFetchActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(CLIFetchActivity.class);

	public String[] process(Map<String,Object> input)  {
		String[] resp = null;
		if(input != null && input.containsKey("vendor") && input.containsKey("os") && input.containsKey("l3DeviceDetails") && input.containsKey("deviceDetails")) {
			L3DeviceDetailsResponse l3DeviceDetails = (L3DeviceDetailsResponse) input.get("l3DeviceDetails");
			DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
			String vendor = (String) input.get("vendor");
			String os = (String) input.get("os");
			FactoryAdapter factoryAdapter = new FactoryAdapter();
			Adapter adapter = factoryAdapter.getAdapter(vendor, os);
			if(adapter != null && l3DeviceDetails != null && deviceDetails.getIp() != null && deviceDetails != null) {
				try {
					Integer snmpVersion = (Integer) input.get("snmpVersion");
					int snmpv = 2;
					if (snmpVersion != null) {
						snmpv = snmpVersion;
					}
					IDeviceDetailsResponse deviceDetailsResponse = adapter.fetch(deviceDetails.getCircuitID(), deviceDetails.getIp(), snmpv);
					if(deviceDetailsResponse != null && deviceDetailsResponse.getDeviceDetails() != null && l3DeviceDetails.getDeviceDetails() != null) {
						l3DeviceDetails.getDeviceDetails().setTime(deviceDetailsResponse.getDeviceDetails().getTime());
						l3DeviceDetails.getDeviceDetails().getInterfaces().addAll(deviceDetailsResponse.getDeviceDetails().getInterfaces());
					}
					resp = new String[] {"SENDRESPONSE"};
				} catch (JSchException je) {
					log.error(je,je);
					input.put("exception", "Connect Exception");
				} catch (Exception e) {
					log.error(e,e);
					input.put("exception", e);
				}
			}
		}
		return resp;
	}
}
