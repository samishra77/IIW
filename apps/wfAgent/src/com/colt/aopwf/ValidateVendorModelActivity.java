package com.colt.aopwf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentConfig;
import com.colt.util.AgentUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class ValidateVendorModelActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(ValidateVendorModelActivity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				if (DeviceDetailsRequest.TYPE_PE.equalsIgnoreCase(deviceDetails.getType())) {
					String pathFile = AgentConfig.getDefaultInstance().getProperty("pathFile").trim();
					String os = AgentUtil.validateVendorModel(pathFile+"agentValidators.xml", deviceDetails.getDeviceType().getVendor(), deviceDetails.getDeviceType().getModel());
					input.put("vendor", deviceDetails.getDeviceType().getVendor());
					input.put("os", os);
					resp = new String[] {"CLIFETCH"};
				} else {
					resp = new String[] {"SNMPFETCH"};
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return resp;
	}
}
