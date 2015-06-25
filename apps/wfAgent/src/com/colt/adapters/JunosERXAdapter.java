package com.colt.adapters;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.colt.connect.ConnectDevice;
import com.colt.util.AgentUtil;
import com.colt.util.DeviceCommand;
import com.colt.util.MessagesErrors;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;


public class JunosERXAdapter extends Adapter {

	private Log log = LogFactory.getLog(JunosERXAdapter.class);

	public ErrorResponse validate (String serviceType, String cpeMgmtIp, String serviceId) throws IOException {
		if (serviceType.equals("IPVPN")) {
			if (serviceId == null || serviceId.trim().equals("")) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.noServiceId"));
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				return errorResponse;
			}
		}
		if (cpeMgmtIp == null || cpeMgmtIp.trim().equals("")) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.noCpeMgmtIp"));
			errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
			return errorResponse;
		}
		return null;
	}

	@Override
	public IDeviceDetailsResponse fetch(String circuitID, String deviceIP, Integer snmpVersion, String wanIP, String community, String serviceId, String serviceType, String cpeMgmtIp, String deviceName) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		DeviceDetail deviceDetail = new DeviceDetail();
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
		if(deviceIP != null && !"".equals(deviceIP) ) {
			ConnectDevice connectDevice = null;
			try {
				connectDevice = new ConnectDevice();
				connectDevice.connect(deviceIP, 30, "telnet");
			} catch (Exception e) {
				try {
					connectDevice = new ConnectDevice();
					connectDevice.connect(deviceIP, 30, "ssh");
				} catch (Exception e2) {
					throw e2;
				}
			}
			try {
				String devName = null;
				String ipDevBkp = null;
				if (deviceName != null) {
					try {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document doc = db.parse(getClass().getResourceAsStream("/conf/erxBkp.xml"));
						Element r = doc.getDocumentElement();
						NodeList erxList = r.getElementsByTagName("erx");
						for (int i=0; i < erxList.getLength(); i++) {
							if (erxList.item(i).getAttributes().getLength() > 0) {
								Element e = (Element) erxList.item(i);
								devName = e.getAttribute("name").toLowerCase();
								if (deviceName.toLowerCase().contains(devName)) {
									for (int k=0; k < e.getChildNodes().getLength(); k++) {
										if ( e.getChildNodes().item(k).getNodeName().equals("backup") ) {
											ipDevBkp = ((Element) e.getChildNodes().item(k)).getAttribute("ip");
										}
									}
								}
							}
						}
					} catch (Exception e){
						log.error(e,e);
					}
				}
				connectDevice.prepareForCommands(FactoryAdapter.VENDOR_JUNIPER);
				executeCommands(connectDevice, deviceIP, deviceDetailsResponse, serviceId, serviceType, cpeMgmtIp, ipDevBkp);
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

	private void executeCommands(ConnectDevice connectDevice, String deviceIP, IDeviceDetailsResponse deviceDetailsResponse, String serviceId, String serviceType, String cpeMgmtIp, String ipDevBkp) throws IOException {
		retrieveDeviceUpTime(connectDevice, deviceDetailsResponse);
		ErrorResponse errorResponse = validate (serviceType, cpeMgmtIp, serviceId);
		if (errorResponse == null) {
			retrieveInterfaces(connectDevice, deviceDetailsResponse, serviceId, serviceType, cpeMgmtIp, ipDevBkp);
		} else {
			if (deviceDetailsResponse.getErrorResponse() == null) {
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	private void retrieveDeviceUpTime(ConnectDevice connectDevice, IDeviceDetailsResponse deviceDetailsResponse) {
		try {
			String command = DeviceCommand.getDefaultInstance().getProperty("junos.erx.showDeviceUptime").trim();
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
							if(lineLowerCase.contains("system running for")) {
								line = line.trim();
								String[] lineArray = line.split(":");
								if(lineArray != null && lineArray.length > 1) {
									values = AgentUtil.splitByDelimiters(lineArray[1], ",");
								}
								if(values != null && !values.isEmpty()) {
									String time = "";
									String valueUpper = null;
									int year = 0;
									int month = 0;
									int day = 0;
									int hour = 0;
									int minute = 0;
									for(String value : values) {
										valueUpper = value.toUpperCase();
										if(valueUpper.contains("YEAR")) {
											List<String> splitList = AgentUtil.splitByDelimiters(value, " ");
											if(splitList != null && splitList.size() > 1) {
												year = Integer.valueOf(splitList.get(0)) * 365;
											}
										}
										if(valueUpper.contains("MONTH")) {
											List<String> splitList = AgentUtil.splitByDelimiters(value, " ");
											if(splitList != null && splitList.size() > 1) {
												month = Integer.valueOf(splitList.get(0)) * 30;
											}
										}
										if(valueUpper.contains("DAY")) {
											List<String> splitList = AgentUtil.splitByDelimiters(value, " ");
											if(splitList != null && splitList.size() > 1) {
												day = Integer.valueOf(splitList.get(0));
											}
										}
										if(valueUpper.contains("HOUR")) {
											List<String> splitList = AgentUtil.splitByDelimiters(value, " ");
											if(splitList != null && splitList.size() > 1) {
												hour = Integer.valueOf(splitList.get(0));
											}
										}
										if(valueUpper.contains("MINUTE")) {
											List<String> splitList = AgentUtil.splitByDelimiters(value, " ");
											if(splitList != null && splitList.size() > 1) {
												minute = Integer.valueOf(splitList.get(0));
											}
										}
									}
									day = year + month + day;
									time += day + "d " + hour + "h " + minute + "m" ;
									deviceDetailsResponse.getDeviceDetails().setTime(time);
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

	private static String getVrf (ConnectDevice connectDevice, String serviceType, String serviceId) throws Exception {
		String ret= null;
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.erx.vrf").trim(), serviceId);
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
					String lineLowerCase = null;
					for(String line : array) {
						lineLowerCase = line.toLowerCase();
						if( lineLowerCase.contains("vrf") ) {
							String[] lineArray = lineLowerCase.split(":");
							ret = lineArray[1].trim();
							break;
						}
					}
				}
			}
		}
		return ret;
	}

	private static String getInterface (ConnectDevice connectDevice, String serviceType, String vrf, String ip) throws Exception {
		String ret= null;
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.erx.interface.vrf").trim(), vrf.toUpperCase(), ip);
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
					int count = 0;
					for(String line : array) {
						if (count != 0) {
							if(line.toLowerCase().contains(ip) ) {
								ret = line.substring(56,line.length());
								if (array[count+1].length() == 79) {
									if (ret != null && array.length != (count+1) && array[count+1] != null) {
										String nextHop = array[count+1].substring(29,44).trim();
										String prefix = array[count+1].substring(0,18).trim();
										if (nextHop.equals("") && prefix.equals("")) {
											ret = ret + array[count+1].substring(56,array[count+1].length());
										}
									}
								}
								break;
							}
						}
						count++;
					}
				}
			}
		}
		return ret != null ? ret.trim() : ret;
	}

	private static String getInterfaceIpaccess (ConnectDevice connectDevice,String ip, boolean inet) throws Exception {
		String ret= null;
		if (inet) {
			String c = DeviceCommand.getDefaultInstance().getProperty("junos.erx.inet").trim();
			if(c != null && !"".equals(c)) {
				connectDevice.applyCommands(c, "#|>");
			}
		}
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.erx.show.ip.route").trim(), ip);
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
					int count = 0;
					for(String line : array) {
						if (count != 0) {
							if( line.toLowerCase().contains(ip) ) {
								ret = line.substring(56,line.length());
								if (array[count+1].length() == 79) {
									if (ret != null && array.length != (count+1) && array[count+1] != null ) {
										String nextHop = array[count+1].substring(29,44).trim();
										String prefix = array[count+1].substring(0,18).trim();
										if (nextHop.equals("") && prefix.equals("")) {
											ret = ret + array[count+1].substring(56,array[count+1].length());
										}
									}
								}
								break;
							}
						}
						count++;
					}
				}
			}
		}
		return ret != null ? ret.trim() : ret;
	}

	private static String getInterfaceIp (ConnectDevice connectDevice, String interfaceName) throws Exception {
		String ret= null;
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.erx.ppp.interface").trim(), interfaceName);
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
					String lineLowerCase = null;
					int count = 0;
					for(String line : array) {
						if (count > 0) {
							lineLowerCase = line.toLowerCase();
							if( lineLowerCase.contains("ip-address") ) {
								List<String> lineList = AgentUtil.splitByDelimiters(lineLowerCase," ");
								if (lineList.get(1) != null) {
									ret = lineList.get(1).trim();
									break;
								}
							}
						}
						count++;
					}
				}
			}
		}
		return ret;
	}

	private void retrieveInterfaces(ConnectDevice connectDevice, IDeviceDetailsResponse deviceDetailsResponse, String serviceId, String serviceType, String cpeMgmtIp, String ipDevBkp) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		try {
			String interfName = null;
			String interfIp = null;
			if ("IPVPN".toString().equalsIgnoreCase(serviceType)) {
				if (serviceId.contains("/")) {
					serviceId = serviceId.substring(0,serviceId.indexOf("/"));
				}
				String vrf = getVrf(connectDevice,serviceType,serviceId);
				if (vrf != null) {
					interfName = getInterface(connectDevice, serviceType, vrf, cpeMgmtIp);
				}
				if ((interfName == null || interfName.trim().equals("")) && ipDevBkp != null && !ipDevBkp.trim().equals("")) {
					ConnectDevice connectDeviceBkp = new ConnectDevice();
					connectDeviceBkp.connect(ipDevBkp, 30, "telnet");
					connectDeviceBkp.prepareForCommands(FactoryAdapter.VENDOR_JUNIPER);
					if ( vrf != null ) {
						interfName = getInterface(connectDeviceBkp, serviceType, vrf, cpeMgmtIp);
					}
				}
			} else {
				if ("IP ACCESS".toString().equalsIgnoreCase(serviceType)) {
					interfName = getInterfaceIpaccess(connectDevice, cpeMgmtIp, false);
					if (interfName == null || interfName.equals("")) {
						interfName = getInterfaceIpaccess(connectDevice, cpeMgmtIp, true);
						if ((interfName == null || interfName.equals("")) && ipDevBkp != null && !ipDevBkp.trim().equals("")) {
							ConnectDevice connectDeviceBkp = new ConnectDevice();
							connectDeviceBkp.connect(ipDevBkp, 30, "telnet");
							connectDeviceBkp.prepareForCommands(FactoryAdapter.VENDOR_JUNIPER);
							interfName = getInterfaceIpaccess(connectDeviceBkp, cpeMgmtIp, false);
							if (interfName == null || interfName.equals("")) {
								interfName = getInterfaceIpaccess(connectDeviceBkp, cpeMgmtIp, true);
							}
						}
					}
				}
			}
			if (interfName != null && !interfName.equals("")) {
				interfIp = getInterfaceIp(connectDevice, interfName);
			}
			if (interfName != null && !interfName.trim().equals("")) {
				Interface interf = new Interface();
				interf.setStatus(AgentUtil.UP);
				interf.setName(interfName);
				if (interfIp != null && !interfIp.equals("")) {
					interf.setIpaddress(interfIp);
				}
				interfaceList.add(interf);
				if(!interfaceList.isEmpty()) {
					deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(interfaceList);
				}
			} else {
				Interface interf = new Interface();
				interf.setStatus(AgentUtil.DOWN);
				interfaceList.add(interf);
				if(!interfaceList.isEmpty()) {
					deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(interfaceList);
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

}
