package com.colt.adapters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.connect.ConnectDevice;
import com.colt.util.AgentUtil;
import com.colt.util.DeviceCommand;
import com.colt.util.MessagesErrors;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class CiscoIOSAdapter extends Adapter {

	private Log log = LogFactory.getLog(CiscoIOSAdapter.class);

	@Override
	public IDeviceDetailsResponse fetch(String circuitID, String deviceIP, Integer snmpVersion, String wanIP) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		DeviceDetail deviceDetail = new DeviceDetail();
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
		if(deviceIP != null && !"".equals(deviceIP) && circuitID != null && !"".equals(circuitID)) {
			ConnectDevice connectDevice = null;
			try {
				connectDevice = new ConnectDevice();
				connectDevice.connect(deviceIP, 15, "telnet");
			} catch (Exception e) {
				try {
					connectDevice = new ConnectDevice();
					connectDevice.connect(deviceIP, 15, "ssh");
				} catch (Exception e2) {
					throw e2;
				}
			}
			try {
				connectDevice.prepareForCommands(FactoryAdapter.VENDOR_CISCO);
				executeCommands(connectDevice, wanIP, deviceIP, circuitID, snmpVersion, deviceDetailsResponse);
			} catch (Exception e) {
				log.error(e,e);
				if(deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("connection.failed"));
					errorResponse.setCode(ErrorResponse.CONNECTION_FAILED);
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
				deviceDetailsResponse.getErrorResponse().getFailedConn().add(deviceIP);
			}
		}
		return deviceDetailsResponse;
	}

	private void executeCommands(ConnectDevice connectDevice, String wanIP, String deviceIP, String circuitID, Integer snmpVersion, IDeviceDetailsResponse deviceDetailsResponse) {
		retrieveDeviceUpTime(connectDevice, deviceDetailsResponse);
		String logicalInterfaceName = retrieveInterfaceByWanIp(connectDevice, wanIP, deviceDetailsResponse);
		String physicalInterfaceName = null;
		if(logicalInterfaceName != null && logicalInterfaceName.indexOf(".") > -1) {
			physicalInterfaceName = logicalInterfaceName.substring(0, logicalInterfaceName.indexOf("."));
			retrievePhysicalInterface(connectDevice, physicalInterfaceName, deviceDetailsResponse);
		}
		retrieveLogicalInterfaces(connectDevice, circuitID, deviceDetailsResponse, logicalInterfaceName, wanIP);

		if(snmpVersion != null) {
			SNMPUtil snmp = new SNMPUtil(snmpVersion);
			snmp.retrieveLastStatusChange(deviceIP, deviceDetailsResponse);
		}
	}

	private void retrieveDeviceUpTime(ConnectDevice connectDevice, IDeviceDetailsResponse deviceDetailsResponse) {
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
									if(uptime != null && uptime.length > 0) {
										int parseWeek = 0;
										int parseDay = 0;
										String hour = "";
										String min = "";
										for (String line : uptime) {
											if(line.contains("week")) {
												String[] weekArray = line.split(" ");
												if(weekArray != null && weekArray.length > 0) {
													parseWeek = Integer.valueOf(line.split(" ")[0]) * 7;
												}
											}
											if(line.contains("day")) {
												String[] dayArray = line.split(" ");
												if(dayArray != null && dayArray.length > 0) {
													parseDay = Integer.valueOf(line.split(" ")[1]);
												}
											}
											if(line.contains("hour")) {
												List<String> lineSplited = AgentUtil.splitByDelimiters(line, " ");
												if(lineSplited != null && lineSplited.size() > 1) {
													hour = lineSplited.get(0) + "h ";
												}
											}

											if(line.contains("minute")) {
												List<String> lineSplited = AgentUtil.splitByDelimiters(line, " ");
												if(lineSplited != null && lineSplited.size() > 1) {
													min = lineSplited.get(0) + "m";
												}
											}
										}
										int totalDay = parseWeek + parseDay;
										String sysUpTime = totalDay + "d " + hour + min;
										deviceDetailsResponse.getDeviceDetails().setTime(sysUpTime);
										break;
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
	}

	private String retrieveInterfaceByWanIp(ConnectDevice connectDevice, String ipAddress, IDeviceDetailsResponse deviceDetailsResponse) {
		String logicalInterfaceName = null;
		try {
			if(ipAddress != null && !"".equals(ipAddress)) {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.ios.showIpInterfaces").trim(), ipAddress);
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, "#");
					if(output != null && !"".equals(output)) {
						//split each line
						String[] outputArray = null;
						if(output.indexOf("\r\n") > -1) {
							outputArray = output.split("\r\n");
						} else {
							outputArray = new String[] {output};
						}

						//process data
						if(outputArray != null && outputArray.length > 1) {
							List<String> values = null;
							for(String line : outputArray) {
								if((line.contains("down") || line.contains("up")) && line.contains(" " + ipAddress + " ")) {
									line = line.trim();
									String[] lineArray = line.split(" ");
									values = new ArrayList<String>();
									for(String l : lineArray) {
										if(!" ".equals(l) && !"".equals(l)) {
											values.add(l);
										}
									}
									String[] interfaceData = values.toArray(new String[values.size()]);
									if(interfaceData.length > 0) {
										logicalInterfaceName = interfaceData[0];
										break;
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
		return logicalInterfaceName;
	}

	private void retrievePhysicalInterface(ConnectDevice connectDevice, String physicalInterfaceName, IDeviceDetailsResponse deviceDetailsResponse) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		try {
			if(physicalInterfaceName != null && !"".equals(physicalInterfaceName)) {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.ios.showIpInterfaces").trim(), physicalInterfaceName);
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, "#");
					if(output != null && !"".equals(output)) {
						Interface interf = null;
						//split each line
						String[] outputArray = null;
						if(output.indexOf("\r\n") > -1) {
							outputArray = output.split("\r\n");
						} else {
							outputArray = new String[] {output};
						}

						//process data
						if(outputArray != null && outputArray.length > 1) {
							List<String> values = null;
							for(String line : outputArray) {
								if((line.contains("Down") || line.contains("Up")) && line.contains(" " + physicalInterfaceName + " ")) {
									line = line.trim();
									String[] lineArray = line.split(" ");
									values = new ArrayList<String>();
									for(String l : lineArray) {
										if(!" ".equals(l) && !"".equals(l)) {
											values.add(l);
										}
									}
									if(!values.isEmpty()) {
										interf = new Interface();
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
											interfaceList.add(interf);
											break;
										}
									}
								}
							}
						}
						if(!interfaceList.isEmpty()) {
							deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(interfaceList);
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
	}

	private void retrieveLogicalInterfaces(ConnectDevice connectDevice, String circuitID, IDeviceDetailsResponse deviceDetailsResponse, String logicalInterfaceName, String wanIP) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		String sidArg = null;
		String sidParam = null;
		if(logicalInterfaceName != null && !"".equals(logicalInterfaceName)) {
			try {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.showInterfaceDescription").trim(), circuitID);
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
							List<String> values = null;
							for(String line : array) {
								if( line.contains(circuitID) 
										&& (line.contains("down") || line.contains("up")) ) {
									line = line.trim();
									String[] lineArray = line.split(" ");
									values = new ArrayList<String>();
									for(String l : lineArray) {
										if(!" ".equals(l) && !"".equals(l)) {
											values.add(l.trim());
										}
									}
									if(!values.isEmpty()) {
										String[] interfaceData = values.toArray(new String[values.size()]);
										if(interfaceData.length > 0) {
											for(String data : interfaceData) {
												if(data.contains("SID") || data.contains("sid")) {
													List<String> splitSID = new ArrayList<String>();
													StringTokenizer st = new StringTokenizer(data.trim(), "[]");
													while(st.hasMoreTokens()) {
														splitSID.add(st.nextToken());
													}
													if(!splitSID.isEmpty() && splitSID.size() == 2) {
														sidArg = splitSID.get(1);
														sidParam = data.trim();
													}
													break;
												}
											}
											break;
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

			try {
				if(sidArg != null && !"".equals(sidArg) && sidParam != null && !"".equals(sidParam)) {
					String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.showInterfaceDescription").trim(), sidArg);
					if(command != null && !"".equals(command)) {
						String output = connectDevice.applyCommands(command, "#");
						if(output != null && !"".equals(output)) {
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
									if(line.contains(sidParam) && (line.contains("down") || line.contains("up")) ) {
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
														if(logicalInterfaceName.equalsIgnoreCase(interfaceData[i])) {
															interf.setIpaddress(wanIP);
														}
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
							}
							if(!interfaceList.isEmpty()) {
								deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(interfaceList);
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
		}
	}
}
