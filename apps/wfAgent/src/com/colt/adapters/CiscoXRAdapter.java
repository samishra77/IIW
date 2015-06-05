package com.colt.adapters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.connect.ConnectDevice;
import com.colt.util.AgentUtil;
import com.colt.util.DeviceCommand;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class CiscoXRAdapter extends Adapter {

	private Log log = LogFactory.getLog(CiscoXRAdapter.class);

	@Override
	public IDeviceDetailsResponse fetch(String circuitID, String deviceIP, Integer snmpVersion, String wanIP, String community) throws Exception {
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
				executeCommands(connectDevice, wanIP, deviceIP, circuitID, snmpVersion, deviceDetailsResponse, community);
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

	private void executeCommands(ConnectDevice connectDevice, String wanIP, String deviceIP, String circuitID, Integer snmpVersion, IDeviceDetailsResponse deviceDetailsResponse, String community) {
		retrieveDeviceUpTime(connectDevice, deviceDetailsResponse);
		String wanIPInterfaceName = retrieveInterfaceByWanIp(connectDevice, wanIP, deviceDetailsResponse);
		retrieveLogicalInterfaces(connectDevice, circuitID, deviceDetailsResponse, wanIPInterfaceName, wanIP);
		String physicalInterfaceName = null;
		if(wanIPInterfaceName != null && !"".equals(wanIPInterfaceName)) {
			if(wanIPInterfaceName.indexOf(".") > -1) {
				physicalInterfaceName = wanIPInterfaceName.substring(0, wanIPInterfaceName.indexOf("."));
			}
		}
		if(physicalInterfaceName != null && !"".equals(physicalInterfaceName)) {
			retrievePhysicalInterface(connectDevice, physicalInterfaceName, deviceDetailsResponse);
		}
		if(deviceDetailsResponse.getDeviceDetails().getInterfaces() != null && !deviceDetailsResponse.getDeviceDetails().getInterfaces().isEmpty()) {
			for(Interface interf : deviceDetailsResponse.getDeviceDetails().getInterfaces()) {
				interf.setLastChgTime("Not available yet");
			}
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
					} else if(output.indexOf("\n") > -1) {
						List<String> outputList = AgentUtil.splitByDelimiters(output, "\n");
						if(outputList != null && !outputList.isEmpty()) {
							array = outputList.toArray(new String[outputList.size()]);
						}
					} else {
						array = new String[] {output};
					}
					if(array != null && array.length > 0) {
						for(String a : array) {
							String aLowerCase = a.toLowerCase();
							if(aLowerCase.contains("uptime is")) {
								String[] uptime = a.split("uptime is");
								if(uptime != null && uptime.length > 1) {
									uptime[1] = uptime[1].trim();
									uptime = uptime[1].split(",");
									if(uptime != null && uptime.length > 0) {
										int parseWeek = 0;
										int parseDay = 0;
										String year = "";
										String hour = "";
										String min = "";
										String lineLowerCase = null;
										for (String line : uptime) {
											lineLowerCase = line.toLowerCase();
											if(lineLowerCase.contains("year")) {
												List<String> lineSplited = AgentUtil.splitByDelimiters(line, " ");
												if(lineSplited != null && lineSplited.size() > 1) {
													year = lineSplited.get(0) + "y ";
												}
											}
											if(lineLowerCase.contains("week")) {
												List<String> weekArray = AgentUtil.splitByDelimiters(line, " ");
												if(weekArray != null && weekArray.size() > 1) {
													parseWeek = Integer.valueOf(weekArray.get(0)) * 7;
												}
											}
											if(lineLowerCase.contains("day")) {
												List<String> dayArray = AgentUtil.splitByDelimiters(line, " ");
												if(dayArray != null && dayArray.size() > 1) {
													parseDay = Integer.valueOf(dayArray.get(0));
												}
											}

											if(lineLowerCase.contains("hour")) {
												List<String> lineSplited = AgentUtil.splitByDelimiters(line, " ");
												if(lineSplited != null && lineSplited.size() > 1) {
													hour = lineSplited.get(0) + "h ";
												}
											}

											if(lineLowerCase.contains("minute")) {
												List<String> lineSplited = AgentUtil.splitByDelimiters(line, " ");
												if(lineSplited != null && lineSplited.size() > 1) {
													min = lineSplited.get(0) + "m";
												}
											}
										}
										int totalDay = parseWeek + parseDay;
										String day = "";
										if(totalDay > 0) {
											day = "" + totalDay + "d ";
										}
										String sysUpTime = year + day + hour + min;
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
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.cli.deviceuptime").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	private String retrieveInterfaceByWanIp(ConnectDevice connectDevice, String ipAddress, IDeviceDetailsResponse deviceDetailsResponse) {
		String logicalInterfaceName = null;
		try {
			if(ipAddress != null && !"".equals(ipAddress)) {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.xr.showIpInterfaces").trim(), ipAddress);
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, "#");
					if(output != null && !"".equals(output)) {
						//split each line
						String[] outputArray = null;
						if(output.indexOf("\r\n") > -1) {
							outputArray = output.split("\r\n");
						} else if(output.indexOf("\n") > -1) {
							List<String> outputList = AgentUtil.splitByDelimiters(output, "\n");
							if(outputList != null && !outputList.isEmpty()) {
								outputArray = outputList.toArray(new String[outputList.size()]);
							}
						} else {
							outputArray = new String[] {output};
						}

						//process data
						if(outputArray != null && outputArray.length > 1) {
							List<String> values = null;
							String lineLowerCase = null;
							for(String line : outputArray) {
								lineLowerCase = line.toLowerCase();
								if((lineLowerCase.contains("down") || lineLowerCase.contains("up")) && line.contains(" " + ipAddress + " ")) {
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
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.cli.showIpInterfaces").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
		return logicalInterfaceName;
	}

	private void retrieveLogicalInterfaces(ConnectDevice connectDevice, String circuitID, IDeviceDetailsResponse deviceDetailsResponse, String wanIPInterfaceName, String wanIP) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		try {
			String logicalInterfaceNameAux = AgentUtil.processCliInterfaceNameDescription(wanIPInterfaceName);
			String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.showInterfaceDescription").trim(), circuitID);
			if(command != null && !"".equals(command)) {
				String output = connectDevice.applyCommands(command, "#");
				if(output != null && !"".equals(output)) {
					Interface interf = null;
					String[] array = null;
					if(output.indexOf("\r\n") > -1) {
						array = output.split("\r\n");
					} else if(output.indexOf("\n") > -1) {
						List<String> outputList = AgentUtil.splitByDelimiters(output, "\n");
						if(outputList != null && !outputList.isEmpty()) {
							array = outputList.toArray(new String[outputList.size()]);
						}
					} else {
						array = new String[] {output};
					}
					if(array != null && array.length > 0) {
						List<String> values = null;
						String lineLowerCase = null;
						for(String line : array) {
							lineLowerCase = line.toLowerCase();
							if(line.contains("[" + circuitID + "]") && (lineLowerCase.contains("down") || lineLowerCase.contains("up")) ) {
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
										String interfaceName= null;
										for (int i = 0; i < interfaceData.length; i++) {
											if(i == 0) {
												interfaceName = AgentUtil.processCliInterfaceNameDescription(interfaceData[i]);
												if(logicalInterfaceNameAux != null && logicalInterfaceNameAux.equals(interfaceName)) {
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
		} catch (Exception e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.cli.showInterfaceDescription").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	private void retrievePhysicalInterface(ConnectDevice connectDevice, String physicalInterfaceName, IDeviceDetailsResponse deviceDetailsResponse) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		try {
			String physicalInterfaceNameAux = AgentUtil.processCliInterfaceNameDescription(physicalInterfaceName);
			if(physicalInterfaceNameAux != null && !"".equals(physicalInterfaceNameAux)) {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.showPhysicalInterface").trim(), physicalInterfaceNameAux);
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, "#");
					if(output != null && !"".equals(output)) {
						Interface interf = null;
						//split each line
						String[] outputArray = null;
						if(output.indexOf("\r\n") > -1) {
							outputArray = output.split("\r\n");
						} else if(output.indexOf("\n") > -1) {
							List<String> outputList = AgentUtil.splitByDelimiters(output, "\n");
							if(outputList != null && !outputList.isEmpty()) {
								outputArray = outputList.toArray(new String[outputList.size()]);
							}
						} else {
							outputArray = new String[] {output};
						}

						//process data
						if(outputArray != null && outputArray.length > 1) {
							List<String> values = null;
							String lineLowerCase = null;
							for(String line : outputArray) {
								lineLowerCase = line.toLowerCase();
								if((lineLowerCase.contains("down") || lineLowerCase.contains("up")) && line.contains(physicalInterfaceNameAux)) {
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
											boolean isPhysicalInterface = false;
											String interfaceNameDescription = null;
											for (int i = 0; i < interfaceData.length; i++) {
												if(i == 0) {
													interfaceNameDescription = AgentUtil.processCliInterfaceNameDescription(interfaceData[i]);
													if(interfaceNameDescription != null && physicalInterfaceNameAux.endsWith(interfaceNameDescription)) {
														interf.setName(physicalInterfaceName);
														isPhysicalInterface = true;
													}
												}
												if(i == 2 && isPhysicalInterface) {
													if(AgentUtil.UP.equalsIgnoreCase(interfaceData[i])) {
														interf.setStatus(AgentUtil.UP);
													} else if(AgentUtil.DOWN.equalsIgnoreCase(interfaceData[i])) {
														interf.setStatus(AgentUtil.DOWN);
													}
													break;
												}
											}
											if(isPhysicalInterface) {
												interfaceList.add(interf);
												break;
											}
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
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.cli.physicalInterface").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}
}
