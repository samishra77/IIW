package com.colt.adapters.l3;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.colt.connect.ConnectDevice;
import com.colt.util.AgentConfig;
import com.colt.util.AgentUtil;
import com.colt.util.ConnectionFactory;
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
	public IDeviceDetailsResponse fetch(String circuitID, String deviceIP, Integer snmpVersion, String wanIP, String community, String serviceId, String serviceType, String cpeMgmtIp, String deviceName, String os) throws Exception {
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		DeviceDetail deviceDetail = new DeviceDetail();
		deviceDetailsResponse.setDeviceDetails(deviceDetail);
		if(deviceIP != null && !"".equals(deviceIP) ) {
			ConnectionFactory connFactory = new ConnectionFactory();
			ConnectDevice connectDevice = null;
			try {
				connectDevice = connFactory.getConnection(deviceIP, FactoryAdapter.VENDOR_JUNIPER, os, deviceDetailsResponse);
				String devName = null;
				String devNameBkp = null;
				String ipDevBkp = null;
				if (deviceName != null) {
					try {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
						String erxBkpFile = AgentConfig.getDefaultInstance().getProperty("erxBkpFile");
						Document doc = null;
						if (erxBkpFile!= null && !"".equals(erxBkpFile)) {
							try {
								doc = db.parse(erxBkpFile.trim());
								log.info("erxBkp.xml read from: "+ erxBkpFile);
							} catch (Exception e) {
								e.printStackTrace();
								doc = db.parse(getClass().getResourceAsStream("/conf/erxBkp.xml"));
								log.info("erxBkp.xml read from: /conf/erxBkp.xml");
							}
						} else { 
							doc = db.parse(getClass().getResourceAsStream("/conf/erxBkp.xml"));
							log.info("erxBkp.xml read from: /conf/erxBkp.xml");
						}
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
											devNameBkp =  ((Element) e.getChildNodes().item(k)).getAttribute("name");
										}
									}
								}
							}
						}
					} catch (Exception e){
						log.error(e,e);
					}
				}
				if(connectDevice != null) {
					connectDevice.prepareForCommands();
					executeCommands(connectDevice, deviceIP, deviceDetailsResponse, serviceId, serviceType, cpeMgmtIp, ipDevBkp, os, devNameBkp);
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
				if(connectDevice != null) {
					connectDevice.disconnect();
				}
			}
		}
		return deviceDetailsResponse;
	}

	private void executeCommands(ConnectDevice connectDevice, String deviceIP, IDeviceDetailsResponse deviceDetailsResponse, String serviceId, String serviceType, String cpeMgmtIp, String ipDevBkp, String os, String devNameBkp) throws IOException {
		retrieveDeviceUpTime(connectDevice, deviceDetailsResponse);
		ErrorResponse errorResponse = validate (serviceType, cpeMgmtIp, serviceId);
		if (errorResponse == null) {
			retrieveInterfaces(connectDevice, deviceDetailsResponse, serviceId, serviceType, cpeMgmtIp, ipDevBkp, os, devNameBkp);
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
								String type = line.substring(19,29).trim().toLowerCase();
								if (type != null && type.equals("bgp") && ret.contains("mpls.ip")) {
									ret = null;
									break;
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
		interfaceName = processInterfaNameForCommand(interfaceName);
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

	private String getStatusUpOrDown (ConnectDevice connectDevice, String interfName) throws Exception {
		String ret= null;
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.erx.interface.status").trim(), interfName);
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
						if( lineLowerCase.contains("is up") ) {
							ret = AgentUtil.UP;
							break;
						}
					}
				}
			}
		}
		return ret;
	}

	private String getLastChg (ConnectDevice connectDevice, String interfName) throws Exception {
		String ret= null;
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.erx.interface.status.last.chg").trim(), interfName);
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
						if( lineLowerCase.contains("seconds") ) {
							List<String> outputList = AgentUtil.splitByDelimiters(line.trim(), " ");
							ret = outputList.get(1) != null ? outputList.get(1) : null;
							break;
						}
					}
				}
			}
		}
		return ret;
	}

	private String getLastStatus(ConnectDevice connectDevice, String interfName) throws Exception {
		interfName = processInterfaNameForCommand(interfName);
		String status = getStatusUpOrDown(connectDevice, interfName);
		if (status != null && status.equals(AgentUtil.UP)) {
			String lastChangeSeconds = getLastChg(connectDevice, interfName);
			int day = (int)TimeUnit.SECONDS.toDays(Long.parseLong(lastChangeSeconds));
			long hours = TimeUnit.SECONDS.toHours(Long.parseLong(lastChangeSeconds)) - (day *24);
			long minute = TimeUnit.SECONDS.toMinutes(Long.parseLong(lastChangeSeconds)) - (TimeUnit.SECONDS.toHours(Long.parseLong(lastChangeSeconds))* 60);
			return (day+"d "+hours+"h "+minute+"m");
		}
		return "";
	}

	private void retrieveInterfaces(ConnectDevice connectDevice, IDeviceDetailsResponse deviceDetailsResponse, String serviceId, String serviceType, String cpeMgmtIp, String ipDevBkp, String os, String devNameBkp) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		ConnectDevice connectDeviceBkp = null;
		try {
			String interfName = null;
			String interfIp = null;
			String lastStatus = null;
			
			if ("IPVPN".toString().equalsIgnoreCase(serviceType)) {
				if (serviceId.contains("/")) {
					serviceId = serviceId.substring(0,serviceId.indexOf("/"));
				}
				String vrf = getVrf(connectDevice,serviceType,serviceId);
				if (vrf != null) {
					interfName = getInterface(connectDevice, serviceType, vrf, cpeMgmtIp);
					if (interfName != null && !"".equals(interfName)) {
						lastStatus = getLastStatus(connectDevice, interfName);
						interfIp = getInterfaceIp(connectDevice, interfName);
					}
				}
				if ((interfName == null || interfName.trim().equals("")) && ipDevBkp != null && !ipDevBkp.trim().equals("")) {
					ConnectionFactory connFactory = new ConnectionFactory();
					connectDeviceBkp = connFactory.getConnection(ipDevBkp, FactoryAdapter.VENDOR_JUNIPER, os, deviceDetailsResponse);
					connectDeviceBkp.prepareForCommands();
					if ( vrf != null ) {
						interfName = getInterface(connectDeviceBkp, serviceType, vrf, cpeMgmtIp);
						if (interfName != null && !"".equals(interfName)) {
							lastStatus = getLastStatus(connectDeviceBkp, interfName);
							interfIp = getInterfaceIp(connectDeviceBkp, interfName);
							retrieveDeviceUpTime(connectDeviceBkp, deviceDetailsResponse);
							deviceDetailsResponse.setDeviceIP(ipDevBkp);
							deviceDetailsResponse.setDeviceName(devNameBkp);
						}
					}
				}
			} else {
				if ("IP ACCESS".toString().equalsIgnoreCase(serviceType)) {
					interfName = getInterfaceIpaccess(connectDevice, cpeMgmtIp, false);
					if (interfName != null && !"".equals(interfName)) {
						lastStatus = getLastStatus(connectDevice, interfName);
						interfIp = getInterfaceIp(connectDevice, interfName);
					}
					if (interfName == null || interfName.equals("")) {
						interfName = getInterfaceIpaccess(connectDevice, cpeMgmtIp, true);
						if (interfName != null && !"".equals(interfName)) {
							lastStatus = getLastStatus(connectDevice, interfName);
							interfIp = getInterfaceIp(connectDevice, interfName);
						}
						if ((interfName == null || interfName.equals("")) && ipDevBkp != null && !ipDevBkp.trim().equals("")) {
							ConnectionFactory connFactory = new ConnectionFactory();
							connectDeviceBkp = connFactory.getConnection(ipDevBkp, FactoryAdapter.VENDOR_JUNIPER, os, deviceDetailsResponse);
							connectDeviceBkp.prepareForCommands();
							interfName = getInterfaceIpaccess(connectDeviceBkp, cpeMgmtIp, false);
							if (interfName != null && !"".equals(interfName)) {
								lastStatus = getLastStatus(connectDeviceBkp, interfName);
								interfIp = getInterfaceIp(connectDeviceBkp, interfName);
								retrieveDeviceUpTime(connectDeviceBkp, deviceDetailsResponse);
							}
							if (interfName == null || interfName.equals("")) {
								interfName = getInterfaceIpaccess(connectDeviceBkp, cpeMgmtIp, true);
								if (interfName != null && !"".equals(interfName)) {
									lastStatus = getLastStatus(connectDeviceBkp, interfName);
									interfIp = getInterfaceIp(connectDeviceBkp, interfName);
									retrieveDeviceUpTime(connectDeviceBkp, deviceDetailsResponse);
									deviceDetailsResponse.setDeviceIP(ipDevBkp);
									deviceDetailsResponse.setDeviceName(devNameBkp);
								}
							}
						}
					}
				}
			}
			if (interfName != null && !interfName.equals("")) {
				Interface interf = new Interface();
				interf.setStatus(AgentUtil.UP);
				interf.setName(interfName);
				interf.setIpaddress(interfIp);
				interf.setLastChgTime(lastStatus);
				interfaceList.add(interf);
				if(!interfaceList.isEmpty()) {
					deviceDetailsResponse.getDeviceDetails().getInterfaces().addAll(interfaceList);
				}
			} else {
				if (deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					try {
						errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.notRetrieve").trim());
					} catch (Exception e1) {
						log.error(e1,e1);
					}
					deviceDetailsResponse.setErrorResponse(errorResponse);
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
		if(connectDeviceBkp != null) {
			connectDeviceBkp.disconnect();
		}
	}

	private static String processInterfaNameForCommand(String interfaceName) {
		String ret = interfaceName;
		if(interfaceName != null && interfaceName.contains("[")) {
			ret = interfaceName.substring(0, interfaceName.indexOf("["));
		}
		return ret;
	}
}
