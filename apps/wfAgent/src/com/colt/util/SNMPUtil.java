package com.colt.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.CiscoIOSAdapter;
import com.colt.adapters.FactoryAdapter;
import com.colt.connect.ConnectDevice;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;

public class SNMPUtil {

	private Log log = LogFactory.getLog(CiscoIOSAdapter.class);
	private Integer version;
	private String type;
	private String serviceType;
	private String community;
	private String os;

	private static final String grepCmd;

	static {
		// Solaris grep in path doesn't support the same features from Linux grep.
		File f = new File("/usr/xpg4/bin/grep");
		if (f.exists()) {
			grepCmd = "/usr/xpg4/bin/grep";
		} else {
			grepCmd = "grep";
		}
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public SNMPUtil() {
		// Empty
	}

	public SNMPUtil(int version) {
		this.version = version;
	}

	public SNMPUtil(int version, String type, String serviceType) {
		this.version = version;
		this.type = type;
		this.serviceType = serviceType;
	}

	public SNMPUtil(String type, String serviceType) {
		this.type = type;
		this.serviceType = serviceType;
	}

	public Integer getVersion() {
		return version;
	}

	public void discoverVendor(String type, String ipAddress, String model, String vendor, IDeviceDetailsResponse deviceDetailsResponse, String deviceName) {
		boolean isSameVendor = false;
		try {
			if(ipAddress != null && !"".equals(ipAddress)) {
				String community = this.snmpCommunity().trim();
				String command = null;
				if(community != null && !"".equals(community)) {
					List<String> communityList = AgentUtil.splitByDelimiters(community, ",");
					if(communityList != null && !communityList.isEmpty()) {
						for(String cmnt : communityList) {
							command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpget").trim(), cmnt, ipAddress, "system.sysDescr.0");
							if(command != null && !"".equals(command)) {
								List<String> outputList = AgentUtil.runLocalCommand(command);
								if(outputList != null && !outputList.isEmpty()) {
									for(String line : outputList) {
										if(line.contains("= STRING:")) {
											this.setCommunity(cmnt);
											if(this.version == null) {
												this.version = 2;
											}
											if(vendor != null && line.toUpperCase().contains(vendor.toUpperCase())) {
												isSameVendor = true;
											} else {
												log.debug("Vendor didn't match for: " + vendor);
											}
											if(line.toUpperCase().contains(FactoryAdapter.VENDOR_JUNIPER.toUpperCase())) {
												if(line.toUpperCase().contains(FactoryAdapter.JUNIPER_ERX.toUpperCase())) {
													this.setOs(FactoryAdapter.JUNIPER_ERX);
												} else {
													this.setOs(FactoryAdapter.JUNIPER_JUNOS);
												}
											} else if(line.toUpperCase().contains(FactoryAdapter.VENDOR_CISCO.toUpperCase())) {
												if(line.toUpperCase().contains(FactoryAdapter.CISCO_XR.toUpperCase())) {
													this.setOs(FactoryAdapter.CISCO_XR);
												} else {
													this.setOs(FactoryAdapter.CISCO_IOS);
												}
											}
											break;
										}
									}
									if(this.version != null) {
										break;
									}
								}
							}
						}
					}
				}
				if(this.version == null) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpget").trim(), ipAddress, "system.sysDescr.0");
					if(command != null && !"".equals(command)) {
						List<String> outputList = AgentUtil.runLocalCommand(command);
						if(outputList != null && !outputList.isEmpty()) {
							for(String line : outputList) {
								if(line.contains("= STRING:")) {
									this.version = 3;
									if(vendor != null && line.toUpperCase().contains(vendor.toUpperCase())) {
										isSameVendor = true;
									} else {
										
										log.debug("Vendor didn't match for: " + vendor);
									}
									if(line.toUpperCase().contains(FactoryAdapter.VENDOR_JUNIPER.toUpperCase())) {
										if(line.toUpperCase().contains(FactoryAdapter.JUNIPER_ERX.toUpperCase())) {
											this.setOs(FactoryAdapter.JUNIPER_ERX);
										} else {
											this.setOs(FactoryAdapter.JUNIPER_JUNOS);
										}
									} else if(line.toUpperCase().contains(FactoryAdapter.VENDOR_CISCO.toUpperCase())) {
										if(line.toUpperCase().contains(FactoryAdapter.CISCO_XR.toUpperCase())) {
											this.setOs(FactoryAdapter.CISCO_XR);
										} else {
											this.setOs(FactoryAdapter.CISCO_IOS);
										}
									}
									break;
								}
							}
						}
					}
				}

				//both snmp command fail
				if(this.version == null) {
					if(DeviceDetailsRequest.TYPE_PE.equalsIgnoreCase(type)) {
						isSameVendor = discoverVendorByCliCommand(ipAddress, vendor, deviceDetailsResponse);
						if(!isSameVendor) {
							if(deviceDetailsResponse != null && deviceDetailsResponse.getErrorResponse() == null && deviceName != null) {
								ErrorResponse errorResponse = new ErrorResponse();
								errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("validate.pe.vendorModelDevDiff"), vendor, deviceName));
								errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
								deviceDetailsResponse.setErrorResponse(errorResponse);
							}
						}
					}
				} else {
					if(!isSameVendor) {
						if(deviceDetailsResponse.getErrorResponse() == null) {
							ErrorResponse errorResponse = new ErrorResponse();
							try {
								errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("validate.vendorModelDevDiff"));
							} catch (IOException e) {
								log.error(e,e);
							}
							errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
							deviceDetailsResponse.setErrorResponse(errorResponse);
						}
					}
					if(this.getOs() == null || "".equals(this.getOs()) && FactoryAdapter.VENDOR_HUAWEI.equalsIgnoreCase(vendor)) {
						this.setOs(FactoryAdapter.HUAWEI_OS);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			if(deviceDetailsResponse != null && deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				try {
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.snmp.vendor.validation"));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	private boolean discoverVendorByCliCommand(String deviceIP, String vendor, IDeviceDetailsResponse deviceDetailsResponse) {
		boolean isSameVendor = false;
		if(deviceIP != null && !"".equals(deviceIP)) {
			ConnectDevice connectDevice = null;
			try {
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

				String prepareCommands = null;
				String endTag = null;
				if(FactoryAdapter.VENDOR_CISCO.equalsIgnoreCase(vendor)) {
					prepareCommands = FactoryAdapter.VENDOR_CISCO;
					endTag = "#";
				} else if(FactoryAdapter.VENDOR_JUNIPER.equalsIgnoreCase(vendor)) {
					prepareCommands = FactoryAdapter.VENDOR_JUNIPER;
					endTag = ">";
					vendor = "junos";
				}

				if(prepareCommands != null && endTag != null) {
					connectDevice.prepareForCommands(prepareCommands);
					String command = DeviceCommand.getDefaultInstance().getProperty("validate.cli.showVersion").trim();
					if(command != null && !"".equals(command)) {
						String output = connectDevice.applyCommands(command, endTag);
						if(output != null && !"".equals(output)) {
							String[] array = null;
							if(output.indexOf("\r\n") > -1) {
								array = output.split("\r\n");
							} else {
								array = new String[] {output};
							}
							if(array != null && array.length > 0) {
								for(String line : array) {
									line = line.trim();
									List<String> lineList = AgentUtil.splitByDelimiters(line, " ,");
									if(lineList != null && !lineList.isEmpty()) {
										if(AgentUtil.verifyItemInList(lineList.toArray(new String[lineList.size()]), vendor)) {
											isSameVendor = true;
											break;
										}
									}
								}
								for(String line : array) {
									line = line.trim();
									if(line.toUpperCase().contains(FactoryAdapter.VENDOR_JUNIPER.toUpperCase())) {
										if(line.toUpperCase().contains(FactoryAdapter.JUNIPER_ERX.toUpperCase())) {
											this.setOs(FactoryAdapter.JUNIPER_ERX);
											break;
										} else {
											this.setOs(FactoryAdapter.JUNIPER_JUNOS);
											break;
										}
									} else if(line.toUpperCase().contains(FactoryAdapter.VENDOR_CISCO.toUpperCase())) {
										if(line.toUpperCase().contains(FactoryAdapter.CISCO_XR.toUpperCase())) {
											this.setOs(FactoryAdapter.CISCO_XR);
											break;
										} else {
											this.setOs(FactoryAdapter.CISCO_IOS);
											break;
										}
									}
								}
								if(this.getOs() == null || "".equals(this.getOs()) && FactoryAdapter.VENDOR_HUAWEI.equalsIgnoreCase(vendor)) {
									this.setOs(FactoryAdapter.HUAWEI_OS);
								}
							}
						}
					}
					
				}
			} catch (Exception e) {
				log.error(e,e);
				if(deviceDetailsResponse != null && deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					try {
						errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("error.cli.vendor.validation"));
					} catch (Exception e1) {
						log.error(e1,e1);
					}
					errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
			} finally {
				if(connectDevice != null) {
					connectDevice.disconnect();
				}
			}
		}
		return isSameVendor;
	}

	public void retrieveLastStatusChange(String ipAddress, IDeviceDetailsResponse deviceDetailsResponse, String sysuptimeWithSeconds) {
		Map<String, Interface> ifAliasMap = retrieveInterfaceIdssByNames(ipAddress, deviceDetailsResponse);
		if(ifAliasMap != null && !ifAliasMap.isEmpty() && sysuptimeWithSeconds != null && !"".equals(sysuptimeWithSeconds)) {
			retrieveInterfaceLastStatusChange(ifAliasMap, ipAddress, deviceDetailsResponse, sysuptimeWithSeconds);
		}
	}

	private Interface retrieveInterfaceCLIByName(Map<String, Interface> mapIfNameInterface, String ifName) {
		if(mapIfNameInterface != null && !mapIfNameInterface.isEmpty() && ifName != null && !"".equals(ifName)) {
			for(String key : mapIfNameInterface.keySet()) {
				if(key.equalsIgnoreCase(ifName)) {
					return mapIfNameInterface.get(key);
				}
			}
		}
		return null;
	}

	private Map<String, Interface> retrieveInterfaceIdssByNames(String ipAddress, IDeviceDetailsResponse deviceDetailsResponse) {
		Map<String, Interface> ifAliasMap = null;
		if(deviceDetailsResponse != null && deviceDetailsResponse.getDeviceDetails() != null && 
				deviceDetailsResponse.getDeviceDetails().getInterfaces() != null && !deviceDetailsResponse.getDeviceDetails().getInterfaces().isEmpty() && 
				ipAddress != null & !"".equals(ipAddress)) {
			String arg = "ifDescr | " + grepCmd + " -E '";
			int i = 0;
			Map<String, Interface> mapIfNameInterface = new HashMap<String, Interface>();
			for(Interface interf : deviceDetailsResponse.getDeviceDetails().getInterfaces()) {
				if(interf.getName() != null && !"".equals(interf.getName())) {
					i++;
					if(i == 1) {
						arg+= interf.getName();
					} else {
						arg+="|" + interf.getName();
					}
					mapIfNameInterface.put(interf.getName(), interf);
				}
			}
			arg+="'";
			try {
				if(i > 0) { // has interface names in arg
					String command = null;
					if(version != null && version == 3) {
						command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpwalk").trim(), ipAddress, arg);
					} else if(community != null && !"".equals(community)) {
						command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpwalk").trim(), community, ipAddress, arg);
					}
					if(command != null && !"".equals(command)) {
						List<String> outputList = AgentUtil.runLocalCommand(command);
						if(outputList != null && !outputList.isEmpty()) {
							Interface interf = null;
							ifAliasMap = new HashMap<String, Interface>();
							for(String line : outputList) {
								String ifAlias = getIfAlias(line);
								String ifName = getIfValue(line);
								if(ifAlias != null && !"".equals(ifAlias) && ifName != null && !"".equals(ifName)) {
									interf = retrieveInterfaceCLIByName(mapIfNameInterface, ifName);
									if(interf != null) {
										ifAliasMap.put(ifAlias, interf);
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
						errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.snmp.cli.ifdescr").trim(), e.toString()));
					} catch (Exception e1) {
						log.error(e1,e1);
					}
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
			}
		}
		return ifAliasMap;
	}

	public Map<String, Interface> retrieveIfAlias(String circuitID, String ipAddress, IDeviceDetailsResponse deviceDetailsResponse) {
		Map<String, Interface> ifAliasMap = null;
		try {
			String command = null;
			if(version != null && version == 3) {
				command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpwalk").trim(), ipAddress, "ifAlias");
			} else if(community != null && !"".equals(community)) {
				command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpwalk").trim(), community, ipAddress, "ifAlias");
			}
			if(command != null && !"".equals(command)) {
				List<String> outputList = AgentUtil.runLocalCommand(command);
				if(outputList != null && !outputList.isEmpty()) {
					String l3CircuitParam = "IfType[Customer";
					String sidParam = "Customer]";
					ifAliasMap = new HashMap<String, Interface>();
					for(String line : outputList) {
						if(line.contains(l3CircuitParam) || line.contains(sidParam)) {
							String ifAlias = getIfAlias(line);
							if(ifAlias != null && !"".equals(ifAlias)) {
								ifAliasMap.put(ifAlias, new Interface());
							}
						}
					}
					// Collect all interfaces when IfType[Customer string is not configured on the device
					if(0 == ifAliasMap.size()) {
						for(String line : outputList) {							
							String ifAlias = getIfAlias(line);
							if(ifAlias != null && !"".equals(ifAlias)) {
								ifAliasMap.put(ifAlias, new Interface());
							}							
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			ErrorResponse errorResponse = null;
			if (deviceDetailsResponse.getErrorResponse() == null) {
				errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.snmp.ifAlias").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
		return ifAliasMap;
	}

	public void retrieveInterfaceName(Map<String, Interface> ifAliasMap, String deviceIP, IDeviceDetailsResponse deviceDetailsResponse) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifDescr." + ifAlias + " ";
				}
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpget").trim(), deviceIP, arg);
				} else if(community != null && !"".equals(community)) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpget").trim(), community, deviceIP, arg);
				}
				if(command != null && !"".equals(command)) {
					List<String> outputList = AgentUtil.runLocalCommand(command);
					if(outputList != null && !outputList.isEmpty()) {
						for(String line : outputList) {
							String ifAlias = getIfAlias(line);
							if(ifAliasMap.containsKey(ifAlias)) {
								String ifName = getIfValue(line);
								if(ifName != null) {
									ifAliasMap.get(ifAlias).setName(ifName);
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
				errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.snmp.ifdescr").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	public String parseTime (String entry) {
		String ret;
		String[] sArray =  entry.trim().split(",");

		int month = 0;
		int day =0;
		int year =0; 
		int hour = 0;
		int min = 0;
		int sec = 0;

		if (sArray != null) {
			for (String s : sArray) {
				String valueUpper = s.toUpperCase();
				String value= s;
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
				if(valueUpper.contains(":")) {
					String[] sArrayA = s.trim().split(":");
					hour = Integer.valueOf(sArrayA[0]);
					min = Integer.valueOf(sArrayA[1]);
					List<String> splitList = AgentUtil.splitByDelimiters(sArrayA[2], ".");
					if(splitList != null && splitList.size() > 0) {
						sec = Integer.valueOf(splitList.get(0));
					}
				}
			}
		}
		ret =  year + month + day + "d " + hour +"h "+ min +"m "+ sec +"s " ;
		return ret;
	}

	private String calc(String sysUpTime, String lastStatusChangeTime) throws ParseException {
		GregorianCalendar calendar = new GregorianCalendar();
		GregorianCalendar diffCalendar = new GregorianCalendar();
		calendar.set(1900, Calendar.JANUARY, 01, 0, 0, 0);
		diffCalendar.set(1900, Calendar.JANUARY, 01, 0, 0, 0);
		String[] seperator = sysUpTime.split(" ");
		int sysDay = Integer.parseInt(seperator[0].substring(0, seperator[0].indexOf("d")));
		int sysHour = Integer.parseInt(seperator[1].substring(0, seperator[1].indexOf("h")));
		int sysMins = Integer.parseInt(seperator[2].substring(0, seperator[2].indexOf("m")));
		seperator = lastStatusChangeTime.split(" ");
		int diffDay = Integer.parseInt(seperator[0].substring(0, seperator[0].indexOf("d")));
		int diffHour = Integer.parseInt(seperator[1].substring(0, seperator[1].indexOf("h")));
		int diffMins = Integer.parseInt(seperator[2].substring(0, seperator[2].indexOf("m")));
		if(0 != sysDay) {
			calendar.add(GregorianCalendar.DAY_OF_MONTH, sysDay);
		}
		if(0 != sysHour) {
			calendar.add(GregorianCalendar.HOUR, sysHour);
		}
		if(0 != sysMins) {
			calendar.add(GregorianCalendar.MINUTE, sysMins);
		}
		calendar.add(GregorianCalendar.DAY_OF_MONTH, -diffDay);
		calendar.add(GregorianCalendar.HOUR, -diffHour);
		calendar.add(GregorianCalendar.MINUTE, -diffMins);
		String h = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		String m = String.valueOf(calendar.get(Calendar.MINUTE));
		return (calendar.getTime().getTime() - diffCalendar.getTime().getTime()) / (60*60*24*1000) + "d " + h + "h "+ m + "m";
	}

	public void retrieveInterfaceLastStatusChange(Map<String, Interface> ifAliasMap, String deviceIP, IDeviceDetailsResponse deviceDetailsResponse, String sysUpTimeWithSeconds) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty() && sysUpTimeWithSeconds != null && !"".equals(sysUpTimeWithSeconds)) {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifLastChange." + ifAlias + " ";
				}
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpget").trim(), deviceIP, arg);
				} else if(community != null && !"".equals(community)) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpget").trim(), community, deviceIP, arg);
				}
				if(command != null && !"".equals(command)) {
					List<String> outputList = AgentUtil.runLocalCommand(command);
					if(outputList != null && !outputList.isEmpty()) {
						for(String line : outputList) {
							String ifAlias = getIfAlias(line);
							if(ifAliasMap.containsKey(ifAlias)) {
								String ifLastStatusChange = getTimeToString(line);
								if(ifLastStatusChange != null && !"".equals(ifLastStatusChange)) {
									String lastStatusChangeTime = convertTimeticks(ifLastStatusChange);
									if(lastStatusChangeTime != null && !"".equals(lastStatusChangeTime)) {
										String result = calc(sysUpTimeWithSeconds, lastStatusChangeTime);
										if (result != null) {
											ifAliasMap.get(ifAlias).setLastChgTime(result);
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
				errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.snmp.iflastchange").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	public void retrieveInterfaceIpAddress(Map<String, Interface> ifAliasMap, String deviceIP, IDeviceDetailsResponse deviceDetailsResponse) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpwalk").trim(), deviceIP, "ipAdEntIfIndex");
				} else if(community != null && !"".equals(community)) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpwalk").trim(), community, deviceIP, "ipAdEntIfIndex");
				}
				if(command != null && !"".equals(command)) {
					List<String> outputList = AgentUtil.runLocalCommand(command);
					if(outputList != null && !outputList.isEmpty()) {
						for(String line : outputList) {
							String ifAlias = getIfValue(line);
							if(ifAlias != null) {
								if(ifAliasMap.containsKey(ifAlias)) {
									if (ifAliasMap.get(ifAlias).getIpaddress() != null && !"".equals(ifAliasMap.get(ifAlias).getIpaddress())) {
										ifAliasMap.get(ifAlias).setIpaddress(ifAliasMap.get(ifAlias).getIpaddress() + ", " + getIfIP(line));
									} else {
										ifAliasMap.get(ifAlias).setIpaddress(getIfIP(line));
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
				errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.snmp.ipAdEntIfIndex").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	public void retrieveInterfaceOperStatus(Map<String, Interface> ifAliasMap, String deviceIP, IDeviceDetailsResponse deviceDetailsResponse) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty() && deviceIP != null && !"".equals(deviceIP)) {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifOperStatus." + ifAlias + " ";
				}
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpget").trim(), deviceIP, arg);
				} else if(community != null && !"".equals(community)) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpget").trim(), community, deviceIP, arg);
				}
				if(command != null && !"".equals(command)) {
					List<String> outputList = AgentUtil.runLocalCommand(command);
					if(outputList != null && !outputList.isEmpty()) {
						for(String line : outputList) {
							String ifAlias = getIfAlias(line);
							if(ifAliasMap.containsKey(ifAlias)) {
								String ifOperStatus = getIfValue(line);
								if(ifOperStatus != null) {
									if(ifOperStatus.contains("up")) {
										ifAliasMap.get(ifAlias).setStatus(AgentUtil.UP);
									} else {
										ifAliasMap.get(ifAlias).setStatus(AgentUtil.DOWN);
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
				errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.snmp.ifOperStatus").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
	}

	public String retrieveInterfaceSysUpTime(String deviceIP, IDeviceDetailsResponse deviceDetailsResponse) {
		String sysUpTime = null;
		try {
			if(deviceIP != null && !"".equals(deviceIP)) {
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpwalk").trim(), deviceIP, "sysUpTime");
				} else if(community != null && !"".equals(community)) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpwalk").trim(), community, deviceIP, "sysUpTime");
				}
				if(command != null && !"".equals(command)) {
					List<String> outputList = AgentUtil.runLocalCommand(command);
					if(outputList != null && !outputList.isEmpty()) {
						String sys = null;
						for(String line : outputList) {
							sys = getTimeToString(line);
							if(sys != null && !"".equals(sys)) {
								return sys;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			if (deviceDetailsResponse.getErrorResponse() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse = new ErrorResponse();
				errorResponse.setCode(ErrorResponse.CODE_UNKNOWN);
				try {
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.snmp.sysUpTime").trim(), e.toString()));
				} catch (Exception e1) {
					log.error(e1,e1);
				}
				deviceDetailsResponse.setErrorResponse(errorResponse);
			}
		}
		return sysUpTime;
	}

	private String getIfIP(String line) {
		String resp = "";
		if(line != null && line.contains("= INTEGER:")) {
			List<String> splitList = AgentUtil.splitByDelimiters(line, "= INTEGER:");
			if(splitList != null && splitList.size() > 0) {
				List<String> ipSplit =  AgentUtil.splitByDelimiters(splitList.get(0), ".");
				if(ipSplit != null && ipSplit.size() >= 4) {
					int indexIpStart = ipSplit.size() - 4;
					while( indexIpStart < ipSplit.size()) {
						if(indexIpStart == ipSplit.size() - 1 ) {
							resp+= ipSplit.get(indexIpStart);
						} else {
							resp+= ipSplit.get(indexIpStart) + ".";
						}
						indexIpStart++;
					}
				}
			}
		}
		return resp;
	}

	private String getIfAlias(String line) {
		String resp = "";
		if(line != null && (line.contains("= Timeticks:") || line.contains("= STRING:") ||  line.contains("= INTEGER:")) ) {
			String[] split = line.split("=");
			if(split != null && split.length > 0) {
				split[0] = split[0].trim();
				int index = split[0].lastIndexOf(".")+1;
				if(index != -1) { 
					resp = split[0].substring(index, split[0].length());
				}
			}
		}
		return resp;
	}

	private String getIfValue(String line) {
		String splitRegex = "";
		if(line != null) {
			if(line.contains("= Timeticks:")) {
				splitRegex = "= Timeticks:";
			} else if(line.contains("= STRING:")) {
				splitRegex = "= STRING:";
			} else if(line.contains("= INTEGER:")){
				splitRegex = "= INTEGER:";
			}
			if(!"".equals(splitRegex)) {
				String[] split = line.split(splitRegex);
				if(split != null && split.length > 1) {
					split[1] = split[1].trim();
					if(splitRegex.equals("= Timeticks:") && split[1].lastIndexOf(")") > 0) {
						split[1] = split[1].substring(split[1].lastIndexOf(")")+1, split[1].length());
					}
					return split[1].trim();
				}
			}
		}
		return splitRegex;
	}

	private String snmpCommunity() throws Exception {
		String result = "";
		try {
			if ( (serviceType != null && !"".equals(serviceType)) && (type != null && !"".equals(type)) ) {
				serviceType = serviceType.toUpperCase().replaceAll(" ", "_");
				String community = "community." + type.toLowerCase() + "." + serviceType;
				result = DeviceCommand.getDefaultInstance().getProperty(community).trim();
				if (result == null || "".equals(result)) {
					result = DeviceCommand.getDefaultInstance().getProperty("community").trim();
				}
			} else {
				result = DeviceCommand.getDefaultInstance().getProperty("community").trim();
			}
		} catch (Exception e) {
			result = DeviceCommand.getDefaultInstance().getProperty("community").trim();
		}
		return result;
	}

	private String convertTimeticks(String ticksvalue) {
		int days=0;
		int hrs=0;
		int mins=0;
		String d = null;

		double daysValue = Double.parseDouble(ticksvalue)/8640000;
		d = String.format("%10f",daysValue).trim();
		if(null!=d && d.contains(".")) {
			days =Integer.parseInt(( d.substring(0, d.indexOf("."))));
		} else {
			days = Integer.parseInt(d);
		}

		if(0 < days)	{
			double hrValue = Double.parseDouble(d.substring(d.indexOf('.'), d.length()))*24;
			d = String.format("%10f",hrValue).trim();
			if(null!=d && d.contains(".")) {
				hrs =Integer.parseInt(( d.substring(0, d.indexOf("."))));
			} else {
				hrs = Integer.parseInt(d);
			}
			if(0<hrs) {
				double mmValue =  Double.parseDouble(d.substring(d.indexOf('.'), d.length()))*60;
				d = String.format("%10f",mmValue).trim();
				if(null!=d && d.contains(".")) {
					mins =Integer.parseInt(( d.substring(0, d.indexOf("."))));
				} else {
					mins = Integer.parseInt(d);
				}
			}
		} else {
			double hrValue = Double.parseDouble(d.substring(d.indexOf('.'), d.length()))*24;
			d = String.format("%10f", hrValue).trim();
			if(null!=d && d.contains(".")) {
				hrs =Integer.parseInt(( d.substring(0, d.indexOf("."))));
			} else {
				hrs = Integer.parseInt(d);
			}
			if(0<hrs) {
				double mmValue =  Double.parseDouble(d.substring(d.indexOf('.'), d.length()))*60;
				d = String.format("%10f",mmValue).trim();
				if(null!=d && d.contains(".")) {
					mins =Integer.parseInt(( d.substring(0, d.indexOf("."))));
				} else {
					mins = Integer.parseInt(d);
				}
			} else {
				double mmValue =  Double.parseDouble(d.substring(d.indexOf('.'), d.length()))*60;
				d = String.format("%10f",mmValue).trim();
				if(null!=d && d.contains(".")) {
					mins =Integer.parseInt(( d.substring(0, d.indexOf("."))));
				} else {
					mins = Integer.parseInt(d);
				}
			}
		}
		return (days+"d "+hrs+"h "+mins+"m");
	}

	public String retrieveSysUpTime(String sysUpTime) {
		return convertTimeticks(sysUpTime);
	}

	private String getTimeToString(String line) {
		String splitRegex = "";
		if(line != null) {
			if(line.contains("= Timeticks:")) {
				splitRegex = "= Timeticks:";
			}
			if(!"".equals(splitRegex)) {
				String[] split = line.split(splitRegex);
				if(split != null && split.length > 1) {
					split[1] = split[1].trim();
					split = split[1].split("[\\(\\)]");
					return split[1].trim();
				}
			}
		}
		return null;
	}
}
