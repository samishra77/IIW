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

public class JunosAdapter extends Adapter {

	private Log log = LogFactory.getLog(JunosAdapter.class);

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
				connectDevice.prepareForCommands(FactoryAdapter.VENDOR_JUNIPER);
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
			String command = DeviceCommand.getDefaultInstance().getProperty("junos.showDeviceUptime").trim();
			if(command != null && !"".equals(command)) {
				String output = connectDevice.applyCommands(command, ">");
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
							if(line.contains("up") && line.contains("day")) {
								line = line.trim();
								String[] lineArray = line.split(" ");
								values = new ArrayList<String>();
								for(String l : lineArray) {
									if(!" ".equals(l) && !"".equals(l)) {
										values.add(l.trim());
									}
								}
								if(!values.isEmpty()) {
									String day = "";
									String hour = "";
									String minute = "";
									if(values.get(4) != null && values.get(4).contains(":") && values.get(4).contains(",")) {
										String aux = values.get(4).replace(",", "");
										String[] hourMinute = aux.split(":");
										if(hourMinute != null && hourMinute.length > 1) {
											int hourNumber = 0;
											if(hourMinute[0] != null && !"".equals(hourMinute[0])) {
												hourNumber = Integer.valueOf(hourMinute[0]);
												if(hourNumber != 1) {
													hour = hourNumber + " hours ";
												} else {
													hour = hourNumber + " hour ";
												}
											}
											int minuteNumber = 0;
											if(hourMinute[1] != null && !"".equals(hourMinute[1])) {
												minuteNumber = Integer.valueOf(hourMinute[1]);
												if(minuteNumber != 1) {
													minute = minuteNumber + " minutes";
												} else {
													minute = minuteNumber + " minute";
												}
											}

										}
									}
									int dayNumber = 0;
									if(values.get(2) != null) {
										dayNumber = Integer.valueOf(values.get(2));
										if(dayNumber != 1) {
											day = dayNumber + " days ";
										} else {
											day = dayNumber + " day ";
										}
									}
									deviceDetailsResponse.getDeviceDetails().setTime(day + hour + minute);
									break;
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

	private void retrievePhysicalInterface(ConnectDevice connectDevice, String physicalInterfaceName, IDeviceDetailsResponse deviceDetailsResponse) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		try {
			if(physicalInterfaceName != null && !"".equals(physicalInterfaceName)) {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaces").trim(), "\"" + physicalInterfaceName + " \"");
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, ">");
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
								if(line.contains("down") || line.contains("up")) {
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

	private String retrieveInterfaceByWanIp(ConnectDevice connectDevice, String ipAddress, IDeviceDetailsResponse deviceDetailsResponse) {
		String logicalInterfaceName = null;
		try {
			if(ipAddress != null && !"".equals(ipAddress)) {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaces").trim(), "\" " + ipAddress + "/\"");
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, ">");
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
								if(line.contains("down") || line.contains("up")) {
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

	private void retrieveLogicalInterfaces(ConnectDevice connectDevice, String circuitID, IDeviceDetailsResponse deviceDetailsResponse, String logicalInterfaceName, String wanIP) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		String sidArg = null;
		String sidParam = null;
		if(logicalInterfaceName != null && !"".equals(logicalInterfaceName)) {
			try {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaceDescription").trim(), "\"" + logicalInterfaceName + " \"");
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, ">");
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
								if( line.contains("L1Circuit[" + circuitID + "]")
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
														sidArg = splitSID.get(0) + "\\["+ splitSID.get(1) + "\\]";
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
					String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaceDescription").trim(), "\"" + sidArg + " \"");
					if(command != null && !"".equals(command)) {
						String output = connectDevice.applyCommands(command, ">");
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
									if( line.contains(sidParam) 
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
