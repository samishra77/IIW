package com.colt.aopwf;

import java.util.Map;

import com.colt.adapters.l2.Adapter;
import com.colt.adapters.l2.FactoryAdapter;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.L2DeviceDetailsResponse;

public class SNMPFetchL2Activity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		return snmpFetchL2(input);
	}

	private String[] snmpFetchL2(Map<String,Object> input) {
		String[] resp = null;
		IDeviceDetailsResponse deviceDetailsResponse = null;
		if(input.containsKey("deviceDetailsResponse")) {
			if(input.get("deviceDetailsResponse") instanceof L2DeviceDetailsResponse) {
				deviceDetailsResponse = (L2DeviceDetailsResponse) input.get("deviceDetailsResponse");
			}
		}
		if(input != null && input.containsKey("vendor") && deviceDetailsResponse != null) {
			try {
				DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
				String vendor = (String) input.get("vendor");
				FactoryAdapter factoryAdapter = new FactoryAdapter();
				Adapter adapter = factoryAdapter.getAdapter(vendor);
				if(adapter != null) {
					Integer snmpVersion = (Integer) input.get("snmpVersion");
					String community = null;
					String serviceType = null;
					if(input.containsKey("community")) {
						community = (String) input.get("community");
					}
					if (deviceDetails.getServiceType() != null && !deviceDetails.getServiceType().equals("")) {
						serviceType = deviceDetails.getServiceType();
					}
					String portname = alterPort(deviceDetails.getPortName(), vendor);
					IDeviceDetailsResponse ddr = adapter.fetch(deviceDetailsResponse.getCircuitID(), deviceDetailsResponse.getDeviceIP(), snmpVersion, community, portname, deviceDetails.getType(), serviceType, deviceDetails.getOcn(), deviceDetails.getName());
					if(ddr != null && ddr.getDeviceDetails() != null && deviceDetailsResponse.getDeviceDetails() != null) {
						deviceDetailsResponse.getDeviceDetails().setTime(ddr.getDeviceDetails().getTime());
						deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(ddr.getDeviceDetails().getInterfaces());
						if(ddr.getErrorResponse() != null) {
							if (deviceDetailsResponse.getErrorResponse() != null) {
								ErrorResponse adapterEr = ddr.getErrorResponse();
								ErrorResponse er = deviceDetailsResponse.getErrorResponse();
								if (adapterEr.getFailedConn() != null && adapterEr.getFailedConn().size() > 0) {
									er.getFailedConn().addAll(adapterEr.getFailedConn());
								}
								if (adapterEr.getFailedSnmp() != null && adapterEr.getFailedSnmp().size() > 0) {
									er.getFailedSnmp().addAll(adapterEr.getFailedSnmp());
								}
							} else {
								deviceDetailsResponse.setErrorResponse(ddr.getErrorResponse());
							}
						}
					}
					resp = new String[] {"SENDRESPONSE"};
				}
			} catch (Exception e) {
				if(deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setMessage(e.toString());
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
			}
		}
		return resp;
	}

	private String alterPort(String port, String vendor) {
		String portName = null;
		if(vendor.equalsIgnoreCase("Accedian")) {
		 if(port.contains(" ") && port.contains("-")) {
			 String ar[] = port.split(" ");
			 if(ar[1].contains("-")) {
					portName = "SFP-"+ar[1].trim();
				}
			 return "SFP-"+ar[1].trim();
		 }
		 String var[] = port.split("-");
		 String name = var[0];		
		 portName = "PORT-" + name.charAt(name.length()-1);
		 System.out.println(portName);
		 
		 String ar[] = var[0].split(" ");
		 if(1 < ar.length ) {			
			if(ar[1].startsWith("0"))
				ar[1] = ar[1].replace("0", "") ;
			portName = "PORT-" + ar[1].trim();			
		 }
		 System.out.println(portName);
		 
		} else if(vendor.equalsIgnoreCase("Actelis")) {
			port = port.replace("ET ", "ETH-");
			return port;
		}  else if(vendor.equalsIgnoreCase("Overture")) {
			return "Client";
		}
		return portName;
	}

}
