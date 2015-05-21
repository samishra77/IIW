package com.colt.adapters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.connect.ConnectDevice;
import com.colt.util.AgentUtil;
import com.colt.util.DeviceCommand;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class CiscoXRAdapter extends Adapter {

	private Log log = LogFactory.getLog(CiscoIOSAdapter.class);

	@Override
	public IDeviceDetailsResponse fetch(String circuitID, String ipAddress, int snmpVersion) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		if(ipAddress != null && !"".equals(ipAddress) && circuitID != null && !"".equals(circuitID)) {
			ConnectDevice connectDevice = null;
			try {
				connectDevice = new ConnectDevice();
				connectDevice.connect(ipAddress, 15, "telnet");
			} catch (Exception e) {
				try {
					connectDevice = new ConnectDevice();
					connectDevice.connect(ipAddress, 15, "ssh");
				} catch (Exception e2) {
					throw e2;
				}
			}
			connectDevice.prepareForCommands(FactoryAdapter.VENDOR_CISCO);
			executeCommands(connectDevice, ipAddress, circuitID, snmpVersion, deviceDetailsResponse);
		}
		return deviceDetailsResponse;
	}
	private void executeCommands(ConnectDevice connectDevice, String ipAddress, String circuitID, int snmpVersion, IDeviceDetailsResponse deviceDetailsResponse) {
		retrieveDeviceUpTime(connectDevice, deviceDetailsResponse);
		if(ipAddress != null && !"".equals(ipAddress)) {
			retrieveWanInterface(connectDevice, ipAddress, deviceDetailsResponse);
		}
		if(circuitID != null && !"".equals(circuitID)) {
			retrieveCircuitInterface(connectDevice, circuitID, deviceDetailsResponse);
		}

		SNMPUtil snmp = new SNMPUtil(snmpVersion);
		snmp.retrieveLastStatusChange(ipAddress, deviceDetailsResponse);
	}

	private void retrieveDeviceUpTime(ConnectDevice connectDevice, IDeviceDetailsResponse deviceDetailsResponse) {
		DeviceDetail deviceDetail = new DeviceDetail();
		try {
			String command = DeviceCommand.getDefaultInstance().getProperty("cisco.showDeviceUptime").trim();
			if(command != null && !"".equals(command)) {
				String output = connectDevice.applyCommands(command, "#");
				if(output != null && !"".equals(output)) {
					String[] array = null;
					if(output.indexOf("\r\n") > -1) {
						array = output.split("\r\n");
					} else {
						array = new String[] {output};
					}
					if(array != null && array.length > 0) {
						for(String a : array) {
							if(a.contains("uptime is")) {
								String[] uptime = a.split("uptime is");
								if(uptime != null && uptime.length > 1) {
									uptime[1] = uptime[1].trim();
									uptime = uptime[1].split(",");
									if(uptime != null && uptime.length > 2) {
										if(uptime[0].contains("week") && uptime[1].contains("day")) {
											String[] weekArray = uptime[0].split(" ");
											String[] dayArray = uptime[1].split(" ");
											if(weekArray != null && weekArray.length > 0 && dayArray != null && dayArray.length > 1) {
												int parseWeek = Integer.valueOf(uptime[0].split(" ")[0]) * 7;
												int parseDay = Integer.valueOf(uptime[1].split(" ")[1]);
												int totalDay = parseWeek + parseDay;
												String sysUpTime = "";
												if(totalDay != 1) {
													sysUpTime = totalDay + " days" + uptime[2] + uptime[3];
												} else {
													sysUpTime = totalDay + " day" + uptime[2] + uptime[3];
												}
												deviceDetail.setTime(sysUpTime);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				errorResponse.setMessage(e.toString());
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
	}

	private void retrieveWanInterface(ConnectDevice connectDevice, String ipAddress, IDeviceDetailsResponse deviceDetailsResponse) {
		try {
			String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.xr.showIpInterfaces").trim(), ipAddress);
			if(command != null && !"".equals(command)) {
				String output = connectDevice.applyCommands(command, "#");
				if(output != null && !"".equals(output)) {
					List<Interface> interfaceList = new ArrayList<Interface>();
					Interface interf = null;
					//split each line
					String[] outputArray = null;
					if(output.indexOf("\r\n") > -1) {
						outputArray = output.split("\r\n");
					} else {
						outputArray = new String[] {output};
					}
					//process data
					if(outputArray != null && outputArray.length > 0) {
						List<String> values = null;
						for(String line : outputArray) {
							if(line.contains("down") || line.contains("up")) {
								line = line.trim();
								String[] lineArray = line.split(" ");
								values = new ArrayList<String>();
								for(String l : lineArray) {
									if(!" ".equals(l) && !"".equals(l)) {
										values.add(l);
									}
								}
								interf = new Interface();
								interf.setIpaddress(ipAddress);
								String[] interfaceData = values.toArray(new String[values.size()]);
								if(interfaceData.length > 0) {
									for (int i = 0; i < interfaceData.length; i++) {
										if(i == 0) {
											interf.setName(interfaceData[i]);
										}
										if(i == 1) {
											if(AgentUtil.UP.equalsIgnoreCase(interfaceData[i])) {
												interf.setStatus(AgentUtil.UP);
											} else if(AgentUtil.DOWN.equalsIgnoreCase(interfaceData[i])) {
												interf.setStatus(AgentUtil.DOWN);
											}
										}
									}
								}
								interfaceList.add(interf);
							}
						}
					}
					if(!interfaceList.isEmpty()) {
						deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(interfaceList);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				errorResponse.setMessage(e.toString());
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	private void retrieveCircuitInterface(ConnectDevice connectDevice, String circuitID, IDeviceDetailsResponse deviceDetailsResponse) {
		try {
			String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.showInterfaceDescription").trim(), circuitID);
			if(command != null && !"".equals(command)) {
				String output = connectDevice.applyCommands(command, "#");
				if(output != null && !"".equals(output)) {
					List<Interface> interfaceList = new ArrayList<Interface>();
					Interface interf = null;
					String[] array = null;
					if(output.indexOf("\r\n") > -1) {
						array = output.split("\r\n");
					} else {
						array = new String[] {output};
					}
					if(array != null && array.length > 0) {
						List<String> values = null;
						for(String line : array) {
							if(line.contains(circuitID) && (line.contains("down") || line.contains("up"))) {
								line = line.trim();
								String[] lineArray = line.split(" ");
								values = new ArrayList<String>();
								for(String l : lineArray) {
									if(!" ".equals(l) && !"".equals(l)) {
										values.add(l.trim());
									}
								}
								if(!values.isEmpty()) {
									interf = new Interface();
									String[] interfaceData = values.toArray(new String[values.size()]);
									if(interfaceData.length > 0) {
										for (int i = 0; i < interfaceData.length; i++) {
											if(i == 0) {
												interf.setName(interfaceData[i].substring(0, interfaceData[i].length()));
											}
											if(i == 1) {
												if(AgentUtil.UP.equalsIgnoreCase(interfaceData[i])) {
													interf.setStatus(AgentUtil.UP);
												} else if(AgentUtil.DOWN.equalsIgnoreCase(interfaceData[i])) {
													interf.setStatus(AgentUtil.DOWN);
												}
											}
										}
									}
									interfaceList.add(interf);
								}
							}
						}
					}
					if(!interfaceList.isEmpty()) {
						deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(interfaceList);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				errorResponse.setMessage(e.toString());
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}
}
