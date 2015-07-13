package com.colt.aopwf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.l2.FactoryAdapter;
import com.colt.util.AgentConfig;
import com.colt.util.MessagesErrors;
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
				SNMPUtil snmp = new SNMPUtil(deviceDetails.getType(), deviceDetails.getServiceType());
				if (!FactoryAdapter.VENDOR_ATRICA.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor())) {
					snmp.discoverVendor(deviceDetails.getType(), deviceDetails.getIp(), deviceDetails.getDeviceType().getModel(), deviceDetails.getDeviceType().getVendor(), deviceDetailsResponse, deviceDetails.getName());
					if(snmp.getVersion() == null) {
						if(deviceDetailsResponse.getErrorResponse() == null) {
							deviceDetailsResponse.setErrorResponse(new ErrorResponse());
							deviceDetailsResponse.getErrorResponse().setCode(ErrorResponse.CODE_UNKNOWN);
							deviceDetailsResponse.getErrorResponse().setMessage(MessagesErrors.getDefaultInstance().getProperty("snmp.queryFailed"));
						}
						deviceDetailsResponse.getErrorResponse().getFailedSnmp().add(deviceDetails.getIp());
						log.debug("SNMP query to device failed: " + deviceDetails.getIp());
					} 
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
				if (deviceDetails != null && deviceDetails.getDeviceType() != null && deviceDetails.getDeviceType().getVendor() != null) {
					if (FactoryAdapter.VENDOR_ACCEDIAN.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor()) ||
							FactoryAdapter.VENDOR_ACTELIS.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor()) ||
							FactoryAdapter.VENDOR_OVERTURE.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor())) {
						input.put("vendor", deviceDetails.getDeviceType().getVendor());
						resp = new String[] {"SNMPFETCH_L2"};
					} else if (FactoryAdapter.VENDOR_ATRICA.equalsIgnoreCase(deviceDetails.getDeviceType().getVendor())) {
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
