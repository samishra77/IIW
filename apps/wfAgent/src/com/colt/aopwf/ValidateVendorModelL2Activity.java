package com.colt.aopwf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.l2.FactoryAdapter;
import com.colt.util.AgentConfig;
import com.colt.util.AgentUtil;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L2DeviceDetailsResponse;

public class ValidateVendorModelL2Activity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(ValidateVendorModelL2Activity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = null;
		if(input.containsKey("deviceDetailsResponse")) {
			if(input.get("deviceDetailsResponse") instanceof L2DeviceDetailsResponse) {
				deviceDetailsResponse = (L2DeviceDetailsResponse) input.get("deviceDetailsResponse");
			}
		}
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				SNMPUtil snmp = null;
				if (null != deviceDetails.getType() && "PE".equalsIgnoreCase(deviceDetails.getType())) {
					snmp = new SNMPUtil();
				} else {
					snmp = new SNMPUtil(deviceDetails.getType(), deviceDetails.getServiceType());
				}
				snmp.discoverVendor(deviceDetails.getType(), deviceDetails.getIp(), deviceDetails.getDeviceType().getModel(), deviceDetails.getDeviceType().getVendor(), deviceDetailsResponse, deviceDetails.getName());
				input.put("snmpVersion", snmp.getVersion());
				input.put("community", snmp.getCommunity());
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
				AgentUtil.validateVendorModel(inputStreamFile, deviceDetails.getDeviceType().getVendor(), deviceDetails.getDeviceType().getModel());
				if (deviceDetails != null && deviceDetails.getDeviceType() != null && deviceDetails.getDeviceType().getVendor() != null) {
					if (FactoryAdapter.VENDOR_ACCEDIAN.equals(deviceDetails.getDeviceType().getVendor()) ||
							FactoryAdapter.VENDOR_ACTELIS.equals(deviceDetails.getDeviceType().getVendor()) ||
							FactoryAdapter.VENDOR_ASPEN.equals(deviceDetails.getDeviceType().getVendor()) ||
							FactoryAdapter.VENDOR_OVERTURE.equals(deviceDetails.getDeviceType().getVendor()) ||
							FactoryAdapter.VENDOR_ATRICA.equals(deviceDetails.getDeviceType().getVendor())) {
						resp = new String[] {"SNMPFETCH_L2"};
					} else {
						resp = new String[] {"EMSAPIFETCH"};
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			if(deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setMessage(e.toString());
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
		return resp;
	}
}
