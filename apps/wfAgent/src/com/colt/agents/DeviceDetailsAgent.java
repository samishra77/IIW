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
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class DeviceDetailsAgent {

	public L3DeviceDetailsResponse execute(DeviceDetailsRequest deviceDetail) throws Exception {
		L3DeviceDetailsResponse l3DeviceDetails = null;
		ApplicationContext ac = new ClassPathXmlApplicationContext("application-config.xml", DeviceDetailsProcess.class);
		IWorkflowProcess process = (IWorkflowProcess) ac.getBean("deviceDetailsProcess");

		Map<String,Object> input = new HashMap<String,Object>();

		List<String> wfState = new ArrayList<String>();
		input.put("WORKFLOW-STATE", wfState);

		input.put("deviceDetails", deviceDetail);

		process.execute(input);
		
		if(input.containsKey("exception")) {
			throw (Exception) input.get("exception");
		}
		
		if(wfState.contains("PING_FAIL")) {
			throw new Exception("Ping Failed");
		}

		// Generate the response string
		if(input.containsKey("l3DeviceDetails")) {
			l3DeviceDetails = (L3DeviceDetailsResponse) input.get("l3DeviceDetails");
		}
		return l3DeviceDetails;
	}

}
