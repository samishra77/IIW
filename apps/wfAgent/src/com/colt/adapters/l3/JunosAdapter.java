package com.colt.adapters.l3;

import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.connect.ConnectDevice;
import com.colt.util.AgentUtil;
import com.colt.util.ConnectionFactory;
import com.colt.util.DeviceCommand;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class JunosAdapter extends Adapter {

	private Log log = LogFactory.getLog(JunosAdapter.class);

	@Override
	public IDeviceDetailsResponse fetch(String circuitID, String deviceIP, Integer snmpVersion, String wanIP, String community, String serviceId, String serviceType, String cpeMgmtIp, String deviceName, String os) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		DeviceDetail deviceDetail = new DeviceDetail();
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
		if(deviceIP != null && !"".equals(deviceIP) && circuitID != null && !"".equals(circuitID)) {
			ConnectionFactory connFactory = new ConnectionFactory();
			ConnectDevice connectDevice = null;
			try {
				connectDevice = connFactory.getConnection(deviceIP, os, deviceDetailsResponse);
				if ((deviceDetailsResponse.getErrorResponse() == null) || (deviceDetailsResponse.getErrorResponse() != null && deviceDetailsResponse.getErrorResponse().getFailedConn().isEmpty()) ) {
					connectDevice.prepareForCommands(FactoryAdapter.VENDOR_JUNIPER);
					executeCommands(connectDevice, wanIP, deviceIP, circuitID, snmpVersion, deviceDetailsResponse, community);
				}
			} catch (Exception e) {
				log.error(e,e);
				if(deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("connection.failed"));
					errorResponse.setCode(ErrorResponse.CONNECTION_FAILED);
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
				deviceDetailsResponse.getErrorResponse().getFailedConn().add(deviceIP);
			} finally {
				connectDevice.disconnect();
			}
		}
		return deviceDetailsResponse;
	}

	private void executeCommands(ConnectDevice connectDevice, String wanIP, String deviceIP, String circuitID, Integer snmpVersion, IDeviceDetailsResponse deviceDetailsResponse, String community) {
		retrieveDeviceUpTime(connectDevice, deviceDetailsResponse);
		retrieveLogicalInterfaces(connectDevice, circuitID, deviceDetailsResponse);
		String physicalInterfaceName = null;
		if(deviceDetailsResponse.getDeviceDetails().getInterfaces() != null && !deviceDetailsResponse.getDeviceDetails().getInterfaces().isEmpty()) {
			for(Interface interf : deviceDetailsResponse.getDeviceDetails().getInterfaces()) {
				if(interf.getName().indexOf(".") > -1) {
					physicalInterfaceName = interf.getName().substring(0, interf.getName().indexOf("."));
					break;
				}
			}
		}
		if(physicalInterfaceName != null && !"".equals(physicalInterfaceName)) {
			retrievePhysicalInterface(connectDevice, physicalInterfaceName, deviceDetailsResponse);
		}
		if(deviceDetailsResponse.getDeviceDetails().getInterfaces() != null && !deviceDetailsResponse.getDeviceDetails().getInterfaces().isEmpty()) {
			for(Interface interf : deviceDetailsResponse.getDeviceDetails().getInterfaces()) {
				if (physicalInterfaceName == null || !physicalInterfaceName.equals(interf.getName())) {
					String ipAddress = retrieveInterfaceIp(connectDevice, interf.getName(), deviceDetailsResponse);
					interf.setIpaddress(ipAddress);
				}
			}
		}
		if(deviceDetailsResponse.getDeviceDetails().getInterfaces() != null && !deviceDetailsResponse.getDeviceDetails().getInterfaces().isEmpty()) {
			for(Interface interf : deviceDetailsResponse.getDeviceDetails().getInterfaces()) {
				interf.setLastChgTime("Not available yet");
			}
		}
	}

	private void retrieveDeviceUpTime(ConnectDevice connectDevice, IDeviceDetailsResponse deviceDetailsResponse) {
		try {
			String command = DeviceCommand.getDefaultInstance().getProperty("junos.showDeviceUptime").trim();
			if(command != null && !"".equals(command)) {
				String output = connectDevice.applyCommands(command, "#|>");
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
						List<String> values = null;
						String lineLowerCase = null;
						for(String line : array) {
							lineLowerCase = line.toLowerCase();
							if(lineLowerCase.contains("up") && lineLowerCase.contains("day")) {
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
												hour = hourNumber + "h ";
											}
											int minuteNumber = 0;
											if(hourMinute[1] != null && !"".equals(hourMinute[1])) {
												minuteNumber = Integer.valueOf(hourMinute[1]);
												minute = minuteNumber + "m";
											}

										}
									}
									int dayNumber = 0;
									if(values.get(2) != null) {
										dayNumber = Integer.valueOf(values.get(2));
										day = dayNumber + "d ";
									}
									deviceDetailsResponse.getDeviceDetails().setTime(day + hour + minute);
									break;
								}
							}
						}
					}
				}
			}
		} catch (SocketTimeoutException e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.socketTimeoutException").trim());
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
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

	private String retrieveInterfaceIp(ConnectDevice connectDevice, String interfName, IDeviceDetailsResponse deviceDetailsResponse) {
		String ipAddress = null;
		try {
			if(interfName != null && !"".equals(interfName)) {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaces").trim(), interfName);
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, "#|>");
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
								if(lineLowerCase.contains(interfName) && (lineLowerCase.contains("down") || lineLowerCase.contains("up"))) {
									line = line.trim();
									String[] lineArray = line.split(" ");
									values = new ArrayList<String>();
									for(String l : lineArray) {
										if(!" ".equals(l) && !"".equals(l)) {
											values.add(l);
										}
									}
									String[] interfaceData = values.toArray(new String[values.size()]);
									if(interfaceData != null && interfaceData.length > 0) {
										
										for (int i = 0; i < interfaceData.length; i++) {
											if(i == 4) {
												ipAddress = interfaceData[i].trim();
												if (ipAddress.contains("/") ) {
													ipAddress = ipAddress.substring(0,ipAddress.indexOf("/"));
												}
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
		} catch (SocketTimeoutException e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.socketTimeoutException").trim());
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
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
		return ipAddress;
	}

	private void retrieveLogicalInterfaces(ConnectDevice connectDevice, String circuitID, IDeviceDetailsResponse deviceDetailsResponse) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		try {
			String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaceDescription").trim(), "\"" + circuitID + "\"");
			if(command != null && !"".equals(command)) {
				String output = connectDevice.applyCommands(command, "#|>");
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
						String lineLowerCase = null;
						for(String line : array) {
							lineLowerCase = line.toLowerCase();
							if((line.contains(circuitID + "]") || line.contains(circuitID + " ")) 
								&& (lineLowerCase.contains("down") || lineLowerCase.contains("up")) ) {

								line = line.trim();
								interf = new Interface();
								String interfName = line.substring(0,16).trim();
								String status = line.substring(16,22).trim();
								interf.setName(interfName);
								if(AgentUtil.UP.equalsIgnoreCase(status.trim().toUpperCase())) {
									interf.setStatus(AgentUtil.UP);
								} else if(AgentUtil.DOWN.equalsIgnoreCase(status.trim().toUpperCase())) {
									interf.setStatus(AgentUtil.DOWN);
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
		} catch (SocketTimeoutException e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.socketTimeoutException").trim());
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
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
			if(physicalInterfaceName != null && !"".equals(physicalInterfaceName)) {
				String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaces").trim(), "\"" + physicalInterfaceName + " \"");
				if(command != null && !"".equals(command)) {
					String output = connectDevice.applyCommands(command, "#|>");
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
								if(lineLowerCase.contains("down") || lineLowerCase.contains("up")) {
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
						} else {
							if (deviceDetailsResponse.getErrorResponse() == null) {
								ErrorResponse errorResponse = new ErrorResponse();
								errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
								errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.noPhysicalInterface").trim());
								deviceDetailsResponse.setErrorResponse(errorResponse);
							}
						}
					}
				}
			}
		} catch (SocketTimeoutException e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.socketTimeoutException").trim());
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
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
