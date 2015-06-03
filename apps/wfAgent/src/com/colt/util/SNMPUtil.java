package com.colt.util;

import java.io.File;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

	public boolean discoverVendor(String type, String ipAddress, String model, String vendor, IDeviceDetailsResponse deviceDetailsResponse, String deviceName) {
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
							isSameVendor = true;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return isSameVendor;
	}

	private boolean discoverVendorByCliCommand(String deviceIP, String vendor, IDeviceDetailsResponse deviceDetailsResponse) {
		boolean isSameVendor = false;
		if(deviceIP != null && !"".equals(deviceIP)) {
			ConnectDevice connectDevice = null;
			try {
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

	public void retrieveLastStatusChange(String ipAddress, IDeviceDetailsResponse deviceDetailsResponse) {
		Map<String, Interface> ifAliasMap = retrieveInterfaceIdssByNames(ipAddress, deviceDetailsResponse);
		if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
			retrieveInterfaceLastStatusChange(ifAliasMap, ipAddress, deviceDetailsResponse);
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
					String l3CircuitParam = circuitID;
					String sidParam = "SID["+circuitID+"]";
					ifAliasMap = new HashMap<String, Interface>();
					for(String line : outputList) {
						if(line.contains(l3CircuitParam) || line.contains(sidParam)) {
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

	public void retrieveInterfaceLastStatusChange(Map<String, Interface> ifAliasMap, String deviceIP, IDeviceDetailsResponse deviceDetailsResponse) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
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
								String ifLastStatusChange = getIfValue(line);
								if(ifLastStatusChange != null) {
									int index = ifLastStatusChange.lastIndexOf(")") + 1;
									if(index != -1) {
										ifLastStatusChange = ifLastStatusChange.substring(index, ifLastStatusChange.length());
										ifAliasMap.get(ifAlias).setLastChgTime(ifLastStatusChange.trim());
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
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifPhysAddress." + ifAlias + " ";
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
								String ifIpAddress = getIfValue(line);
								if(ifIpAddress != null) {
									ifAliasMap.get(ifAlias).setIpaddress(ifIpAddress);
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
					errorResponse.setMessage(MessageFormat.format(MessagesErrors.getDefaultInstance().getProperty("error.snmp.ifPhysAddress").trim(), e.toString()));
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
						for(String line : outputList) {
							sysUpTime = getIfValue(line);
							if(sysUpTime != null && !"".equals(sysUpTime)) {
								if(sysUpTime.contains(" days,")) {
									sysUpTime = sysUpTime.replace(" days,", "d,");
								} else if(sysUpTime.contains(" day,")) {
									sysUpTime = sysUpTime.replace(" day,", "d,");
								}
								List<String> tokens = new ArrayList<String>();
								StringTokenizer st = new StringTokenizer(sysUpTime, ",:");
								while(st.hasMoreTokens()) {
									tokens.add(st.nextToken());
								}
								sysUpTime = tokens.get(0) + " " + tokens.get(1).trim() + "h " + tokens.get(2) + "m ";
								return sysUpTime;
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

}
