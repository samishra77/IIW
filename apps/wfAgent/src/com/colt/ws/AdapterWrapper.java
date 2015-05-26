package com.colt.ws;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.Adapter;
import com.colt.adapters.FactoryAdapter;
import com.colt.aopwf.ValidateVendorModelActivity;
import com.colt.util.AgentUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailWS;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.DeviceDetailsWSResponse;
import com.colt.ws.biz.DeviceType;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.ErrorWSResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsResponse;
import com.colt.ws.biz.L3DeviceDetailsWSResponse;

@WebService(endpointInterface = "com.colt.ws.IAdapterWrapper", serviceName = "AdapterWrapper")
@WebServlet(name="AdapterWrapper", urlPatterns={"/services/AdapterWrapper"})
public class AdapterWrapper implements IAdapterWrapper {

	private Log log = LogFactory.getLog(AdapterWrapper.class);

	@Override
	public DeviceDetailsWSResponse fetch(String vendor, String model, String circuitID, String deviceIP) {
		DeviceDetailsWSResponse deviceDetailsResponse = new L3DeviceDetailsWSResponse();
		deviceDetailsResponse.setCircuitID(circuitID);
		deviceDetailsResponse.setDeviceIP(deviceIP);
		deviceDetailsResponse.setDeviceDetails(new DeviceDetailWS());

		Map<String, Object> input = new HashMap<String, Object>();

		IDeviceDetailsResponse ddr = new L3DeviceDetailsResponse();
		ddr.setDeviceDetails(new DeviceDetail());
		input.put("deviceDetailsResponse", ddr);

		DeviceDetailsRequest deviceDetails = new DeviceDetailsRequest();
		deviceDetails.setIp(deviceIP);
		deviceDetails.setCircuitID(circuitID);
		deviceDetails.setType(DeviceDetailsRequest.TYPE_PE);
		deviceDetails.setDeviceType(new DeviceType());
		deviceDetails.getDeviceType().setModel(model);
		deviceDetails.getDeviceType().setVendor(vendor);
		input.put("deviceDetails", deviceDetails);

		ValidateVendorModelActivity validateVendorModelActivity = new ValidateVendorModelActivity();
		String[] resp = validateVendorModelActivity.process(input);
		if(ddr.getErrorResponse() != null) {
			deviceDetailsResponse.setErrorResponse(ddr.getErrorResponse());
		}
		boolean isCliFetch = false;
		if(resp != null && resp.length > 0) {
			StringBuffer itemResp = new StringBuffer();
			isCliFetch = AgentUtil.verifyItemInList(resp, "CLIFETCH", itemResp);
		}
		if(isCliFetch && input.containsKey("snmpVersion") && input.containsKey("vendor") && input.containsKey("os")) {
			cliFetch((Integer) input.get("snmpVersion"), (String) input.get("os"), (String) input.get("vendor"), deviceDetailsResponse);
		}
		
		return deviceDetailsResponse;
	}

	private void cliFetch(Integer snmpVersion, String os, String vendor, DeviceDetailsWSResponse deviceDetailsResponse) {
		FactoryAdapter factoryAdapter = new FactoryAdapter();
		Adapter adapter = factoryAdapter.getAdapter(vendor, os);
		String wanIP = AgentUtil.calculateWanIp(deviceDetailsResponse.getDeviceIP());
		if(adapter != null) {
			try {
				IDeviceDetailsResponse ddr = adapter.fetch(deviceDetailsResponse.getCircuitID(), deviceDetailsResponse.getDeviceIP(), snmpVersion, wanIP);
				if(ddr != null && ddr.getDeviceDetails() != null && deviceDetailsResponse.getDeviceDetails() != null) {
					deviceDetailsResponse.getDeviceDetails().setTime(ddr.getDeviceDetails().getTime());
					deviceDetailsResponse.getDeviceDetails().setInterfaces(ddr.getDeviceDetails().getInterfaces());
					if(ddr.getErrorResponse() != null && deviceDetailsResponse.getErrorResponse() == null) {
						deviceDetailsResponse.setErrorResponse(ddr.getErrorResponse());
					}
				}
			} catch (Exception e) {
				log.error(e,e);
				if(deviceDetailsResponse.getErrorResponse() == null) {
					ErrorWSResponse errorResponse = new ErrorWSResponse();
					errorResponse.setMessage(e.toString());
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
			}
		}
	}
}
