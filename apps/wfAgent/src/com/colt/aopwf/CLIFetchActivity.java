package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.Adapter;
import com.colt.adapters.FactoryAdapter;
import com.colt.ws.biz.DeviceDetailsRequest;

public class CLIFetchActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(CLIFetchActivity.class);

	public String[] process(Map<String,Object> input)  {
		String[] resp = null;
		if(input != null && input.containsKey("vendor") && input.containsKey("os") && input.containsKey("deviceDetails")) {
			DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
			String vendor = (String) input.get("vendor");
			String os = (String) input.get("os");
			FactoryAdapter factoryAdapter = new FactoryAdapter();
			Adapter adapter = factoryAdapter.getAdapter(vendor, os);
			if(adapter != null) {
				try {
					adapter.fetch(deviceDetails.getIp());
					resp = new String[] {"SENDRESPONSE"};
				} catch (Exception e) {
					log.error(e,e);
				}
			}
		}
		return resp;
	}
}
