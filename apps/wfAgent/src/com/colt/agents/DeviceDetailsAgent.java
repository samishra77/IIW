package com.colt.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.colt.aopwf.DeviceDetailsProcess;
import com.colt.aopwf.IWorkflowProcess;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class DeviceDetailsAgent {

	public static final int CODE_UNKKONWN = 0;

	private ApplicationContext ac;
	private static Object lock = new Object();

	public IDeviceDetailsResponse execute(DeviceDetailsRequest deviceDetail) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = null;
		IWorkflowProcess process = getProcess();

		Map<String,Object> input = new HashMap<String,Object>();

		List<String> wfState = new ArrayList<String>();
		input.put("WORKFLOW-STATE", wfState);

		input.put("deviceDetails", deviceDetail);

		process.execute(input);

		// Generate the response string
		if(input.containsKey("l3DeviceDetails")) {
			deviceDetailsResponse = (L3DeviceDetailsResponse) input.get("l3DeviceDetails");
		}

		if(input.containsKey("exception")) {
			if (deviceDetailsResponse != null) {
				if (deviceDetailsResponse.getErrorResponse() == null) {
					deviceDetailsResponse.setErrorResponse(new ErrorResponse());
					deviceDetailsResponse.getErrorResponse().setCode(CODE_UNKKONWN);
					if (input.get("exception") instanceof Exception) {
						deviceDetailsResponse.getErrorResponse().setMessage(((Exception) input.get("exception")).toString());
					}
					if (input.get("exception") instanceof String) {
						String exception  = ((String) input.get("exception")).toString();
						if (deviceDetailsResponse.getErrorResponse().getMessage() == null || "".equals(deviceDetailsResponse.getErrorResponse().getMessage())) {
							deviceDetailsResponse.getErrorResponse().setMessage(exception);
						}
						if (exception.equalsIgnoreCase("Error snmp connection")) {
							deviceDetailsResponse.getErrorResponse().getFailedSnmp().add(deviceDetailsResponse.getWanIP());
						} else if (exception.equalsIgnoreCase("Connect Exception")) {
							deviceDetailsResponse.getErrorResponse().getFailedConn().add(deviceDetailsResponse.getWanIP());
						}
					}
				} else {
					if (input.get("exception") instanceof String) {
						String exception  = ((String) input.get("exception")).toString();
						if (deviceDetailsResponse.getErrorResponse().getMessage() == null || "".equals(deviceDetailsResponse.getErrorResponse().getMessage())) {
							deviceDetailsResponse.getErrorResponse().setMessage(exception);
						}
						if (exception.equalsIgnoreCase("Error snmp connection")) {
							deviceDetailsResponse.getErrorResponse().getFailedSnmp().add(deviceDetailsResponse.getWanIP());
						} else if (exception.equalsIgnoreCase("Connect Exception")) {
							deviceDetailsResponse.getErrorResponse().getFailedConn().add(deviceDetailsResponse.getWanIP());
						}
					}
				}
			}
		}

		return deviceDetailsResponse;
	}

	private IWorkflowProcess getProcess() {
		synchronized (lock) {
			if (ac == null) {
				ac = new ClassPathXmlApplicationContext("application-config.xml", DeviceDetailsProcess.class);
			}
			return (IWorkflowProcess) ac.getBean("deviceDetailsProcess");
		}
	}

}
