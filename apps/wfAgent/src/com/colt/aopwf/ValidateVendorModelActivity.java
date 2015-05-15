package com.colt.aopwf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentConfig;
import com.colt.util.AgentUtil;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetailsRequest;

public class ValidateVendorModelActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(ValidateVendorModelActivity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				//test Model, find version
				SNMPUtil snmp = new SNMPUtil();
				snmp.discoverModel(deviceDetails.getIp(), deviceDetails.getDeviceType().getModel());
				if(snmp.getVersion() != null) {
					input.put("snmpVersion", snmp.getVersion());
				}
				InputStream inputStreamFile = null;
				String pathFile = AgentConfig.getDefaultInstance().getProperty("agentValidators.pathFile").trim();
				String fileName = AgentConfig.getDefaultInstance().getProperty("agentValidators").trim();
				if(pathFile != null && !"".equals(pathFile) && !" ".equals(pathFile) && fileName != null && !"".equals(fileName) && !" ".equals(fileName)) {
					File file = new File(pathFile, fileName);
					if(file != null && file.isFile()) {
						inputStreamFile = new FileInputStream(file);
					}
				} else {
					inputStreamFile = this.getClass().getResourceAsStream("/conf/agentValidators.xml");
				}
				String os = AgentUtil.validateVendorModel(inputStreamFile, deviceDetails.getDeviceType().getVendor(), deviceDetails.getDeviceType().getModel());
				if(os != null && !"".equals(os)) {
					input.put("vendor", deviceDetails.getDeviceType().getVendor());
					input.put("os", os);
					if (DeviceDetailsRequest.TYPE_PE.equalsIgnoreCase(deviceDetails.getType())) {
						resp = new String[] {"CLIFETCH"};
					} else {
						resp = new String[] {"SNMPFETCH"};
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return resp;
	}
}
