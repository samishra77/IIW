package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.Adapter;
import com.colt.adapters.FactoryAdapter;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class CLIFetchActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(CLIFetchActivity.class);

	public String[] process(Map<String,Object> input)  {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = null;
		if(input.containsKey("deviceDetailsResponse")) {
			if(input.get("deviceDetailsResponse") instanceof L3DeviceDetailsResponse) {
				deviceDetailsResponse = (L3DeviceDetailsResponse) input.get("deviceDetailsResponse");
			} else {
				//L2 Cast
			}
		}
		if(input != null && input.containsKey("vendor") && input.containsKey("os") && deviceDetailsResponse != null) {
			String vendor = (String) input.get("vendor");
			String os = (String) input.get("os");
			FactoryAdapter factoryAdapter = new FactoryAdapter();
			Adapter adapter = factoryAdapter.getAdapter(vendor, os);
			if(adapter != null && deviceDetailsResponse.getWanIP() != null) {
				try {
					Integer snmpVersion = (Integer) input.get("snmpVersion");
					int snmpv = 2;
					if (snmpVersion != null) {
						snmpv = snmpVersion;
					}
					IDeviceDetailsResponse ddr = adapter.fetch(deviceDetailsResponse.getCircuitID(), deviceDetailsResponse.getWanIP(), snmpv);
					if(ddr != null && ddr.getDeviceDetails() != null && deviceDetailsResponse.getDeviceDetails() != null) {
						deviceDetailsResponse.getDeviceDetails().setTime(ddr.getDeviceDetails().getTime());
						deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(ddr.getDeviceDetails().getInterfaces());
						if(ddr.getErrorResponse() != null) {
							deviceDetailsResponse.setErrorResponse(ddr.getErrorResponse());
						}
					}
					resp = new String[] {"SENDRESPONSE"};
				} catch (Exception e) {
					log.error(e,e);
					if(deviceDetailsResponse.getErrorResponse() == null) {
						ErrorResponse errorResponse = new ErrorResponse();
						errorResponse.setMessage(e.toString());
						errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
						deviceDetailsResponse.setErrorResponse(errorResponse);
					}
				}
			}
		}
		return resp;
	}
}
