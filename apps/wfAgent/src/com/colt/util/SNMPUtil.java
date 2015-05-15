package com.colt.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.adapters.CiscoIOSAdapter;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.Interface;

public class SNMPUtil {

	private Log log = LogFactory.getLog(CiscoIOSAdapter.class);
	private Integer version;

	public SNMPUtil() {
		// Empty
	}

	public SNMPUtil(int version) {
		this.version = version;
	}

	public Integer getVersion() {
		return version;
	}

	public boolean discoverModel(String ipAddress, String model) {
		boolean isSameModel = false;
		try {
			if(ipAddress != null && !"".equals(ipAddress)) {
				String community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
				String command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpget").trim(), community, ipAddress, "system.sysDescr.0");
				if(command != null && !"".equals(command)) {
					List<String> outputList = AgentUtil.runLocalCommand(command);
					if(outputList != null && !outputList.isEmpty()) {
						for(String line : outputList) {
							if(line.contains("= STRING:")) {
								if(this.version == null) {
									if(version != null) {
										this.version = 2;
									}
								}
								if(model != null && line.contains(model)) {
									isSameModel = true;
								}
								break;
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
									if(this.version == null) {
										if(version != null) {
											this.version = 3;
										}
									}
									if(model != null && line.contains(model)) {
										isSameModel = true;
									}
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return isSameModel;
	}

	public void retrieveLastStatusChange(String circuitID, String ipAddress, DeviceDetail deviceDetail) {
			Map<String, Interface> ifAliasMap = retrieveIfAlias(circuitID, ipAddress);
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				
				retrieveInterfaceName(ifAliasMap, ipAddress);
				retrieveInterfaceLastStatusChange(ifAliasMap, ipAddress);

				List<Interface> snmpInterfaces = new ArrayList<Interface>();
				if(!ifAliasMap.isEmpty()) {
					for(String key : ifAliasMap.keySet()) {
						snmpInterfaces.add(ifAliasMap.get(key));
					}
				}

				if(deviceDetail.getInterfaces() != null && !deviceDetail.getInterfaces().isEmpty() && !snmpInterfaces.isEmpty()) {
					for(Interface cliInterf : deviceDetail.getInterfaces()) {
						for(Interface smpInt : snmpInterfaces) {
							if(smpInt.getName() != null && cliInterf.getName() != null && smpInt.getName().equalsIgnoreCase(cliInterf.getName())) {
								cliInterf.setLastChgTime(smpInt.getLastChgTime());
							}
						}
					}
				}
			}
	}

	public Map<String, Interface> retrieveIfAlias(String circuitID, String ipAddress) {
		Map<String, Interface> ifAliasMap = null;
		try {
			String community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
			String command = null;
			if(version != null && version == 3) {
				command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpwalk").trim(), ipAddress, "ifAlias");
			} else {
				command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpwalk").trim(), community, ipAddress, "ifAlias");
			}
			if(command != null && !"".equals(command)) {
				List<String> outputList = AgentUtil.runLocalCommand(command);
				if(outputList != null && !outputList.isEmpty()) {
					String l3CircuitParam = "L3Circuit["+circuitID+"]";
					String sidParam = "Cct["+circuitID+"]";
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
		}
		return ifAliasMap;
	}

	public void retrieveInterfaceName(Map<String, Interface> ifAliasMap, String deviceIP) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifDescr." + ifAlias + " ";
				}
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpget").trim(), deviceIP, arg);
				} else {
					String community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
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
		}
	}

	public void retrieveInterfaceLastStatusChange(Map<String, Interface> ifAliasMap, String deviceIP) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifLastChange." + ifAlias + " ";
				}
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpget").trim(), deviceIP, arg);
				} else {
					String community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
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
		}
	}

	public void retrieveInterfaceIpAddress(Map<String, Interface> ifAliasMap, String deviceIP) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifPhysAddress." + ifAlias + " ";
				}
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpget").trim(), deviceIP, arg);
				} else {
					String community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
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
		}
	}

	public void retrieveInterfaceOperStatus(Map<String, Interface> ifAliasMap, String deviceIP) {
		try {
			if(ifAliasMap != null && !ifAliasMap.isEmpty() && deviceIP != null && !"".equals(deviceIP)) {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifOperStatus." + ifAlias + " ";
				}
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpget").trim(), deviceIP, arg);
				} else {
					String community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
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
		}
	}

	public String retrieveInterfaceSysUpTime(String deviceIP) {
		String sysUpTime = null;
		try {
			if(deviceIP != null && !"".equals(deviceIP)) {
				String command = null;
				if(version != null && version == 3) {
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v3.snmpwalk").trim(), deviceIP, "sysUpTime");
				} else {
					String community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
					command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("v2.snmpwalk").trim(), community, deviceIP, "sysUpTime");
				}
				if(command != null && !"".equals(command)) {
					List<String> outputList = AgentUtil.runLocalCommand(command);
					if(outputList != null && !outputList.isEmpty()) {
						for(String line : outputList) {
							sysUpTime = getIfValue(line);
							if(sysUpTime != null && !"".equals(sysUpTime)) {
								if(sysUpTime.contains("days,")) {
									sysUpTime = sysUpTime.replace("days,", "d");
								} else if(sysUpTime.contains("day,")) {
									sysUpTime = sysUpTime.replace("day,", "d");
								}
								return sysUpTime;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
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
}
