package com.colt.aopwf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentConfig;
import com.colt.util.MessagesErrors;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class ValidateVendorModelActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(ValidateVendorModelActivity.class);

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = null;
		if(input.containsKey("deviceDetailsResponse")) {
			if(input.get("deviceDetailsResponse") instanceof L3DeviceDetailsResponse) {
				deviceDetailsResponse = (L3DeviceDetailsResponse) input.get("deviceDetailsResponse");
			} else {
				//L2 Cast
			}
		}
		try {
			if(input != null && input.containsKey("deviceDetails")) {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				//test Model, find version
				SNMPUtil snmp = null;
				if (null != deviceDetails.getType() && "PE".equalsIgnoreCase(deviceDetails.getType())) {
					snmp = new SNMPUtil();
				} else {
					snmp = new SNMPUtil(deviceDetails.getType(), deviceDetails.getServiceType());
				}
				snmp.discoverVendor(deviceDetails, deviceDetailsResponse);
				if(snmp.getVersion() == null && DeviceDetailsRequest.TYPE_CPE.equalsIgnoreCase(deviceDetails.getType())) {
					if(deviceDetailsResponse.getErrorResponse() == null) {
						deviceDetailsResponse.setErrorResponse(new ErrorResponse());
						deviceDetailsResponse.getErrorResponse().setCode(ErrorResponse.CODE_UNKNOWN);
						deviceDetailsResponse.getErrorResponse().setMessage(MessagesErrors.getDefaultInstance().getProperty("snmp.queryFailed"));
					}
					deviceDetailsResponse.getErrorResponse().getFailedSnmp().add(deviceDetails.getIp());
					log.debug("SNMP query to device failed: " + deviceDetails.getIp());
				}
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
				if (DeviceDetailsRequest.TYPE_PE.equalsIgnoreCase(deviceDetails.getType())) {
					String os = snmp.getOs();
					if(os != null && !"".equals(os)) {
						input.put("vendor", deviceDetails.getDeviceType().getVendor());
						input.put("os", os);
						deviceDetailsResponse.setOs(os);
						resp = new String[] {"CLIFETCH"};
					} else {
						if(deviceDetailsResponse.getErrorResponse() == null) {
							ErrorResponse errorResponse = new ErrorResponse();
							errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("validate.vendorModelFile"));
							errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
							deviceDetailsResponse.setErrorResponse(errorResponse);
						}
					}
				} else {
					resp = new String[] {"SNMPFETCH"};
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
