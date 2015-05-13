package com.colt.aopwf;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentUtil;
import com.colt.util.DeviceCommand;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

public class SNMPFetchActivity implements IWorkflowProcessActivity {

	private Log log = LogFactory.getLog(SNMPFetchActivity.class);
	private String community = "";

	public String[] process(Map<String,Object> input) {
		return snmpFetch(input);
	}

	private String[] snmpFetch(Map<String,Object> input) {
		String[] resp = null;
		if(input != null && input.containsKey("deviceDetails")) {
			DeviceDetailsRequest deviceDetails = (DeviceDetailsRequest) input.get("deviceDetails");
			try {
				community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
				String command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("snmpwalk").trim(), community, deviceDetails.getIp(), "ifAlias");
				List<String> outputList = AgentUtil.runLocalCommand(command);
				if(outputList != null && !outputList.isEmpty()) {
					Map<String, Interface> ifAliasMap = new HashMap<String, Interface>();
					String l3CircuitParam = "L3Circuit["+deviceDetails.getCircuitID()+"]";
					String sidParam = "SID["+deviceDetails.getCircuitID()+"]";
					for(String line : outputList) {
						if(line.contains(l3CircuitParam) || line.contains(sidParam)) {
							String ifAlias = getIfAlias(line);
							if(ifAlias != null && !"".equals(ifAlias)) {
								ifAliasMap.put(ifAlias, new Interface());
							}
						}
					}
					retrieveInterfaceName(ifAliasMap, deviceDetails.getIp());
					retrieveInterfaceLastStatusChange(ifAliasMap, deviceDetails.getIp());
					retrieveInterfaceIpAddress(ifAliasMap, deviceDetails.getIp());
					retrieveInterfaceOperStatus(ifAliasMap, deviceDetails.getIp());
					String sysUpTime = retrieveInterfaceSysUpTime(deviceDetails.getIp());
					if(input.containsKey("l3DeviceDetails")) {
						L3DeviceDetailsResponse l3DeviceDetails = (L3DeviceDetailsResponse) input.get("l3DeviceDetails");
						if(sysUpTime != null && !"".equals(sysUpTime)) {
							l3DeviceDetails.getDeviceDetails().setTime(sysUpTime);
						}
						for(String key : ifAliasMap.keySet()) {
							l3DeviceDetails.getDeviceDetails().getInterfaces().add(ifAliasMap.get(key));
						}
					}
				}
			} catch (Exception e) {
				log.error(e,e);
			}
			resp = new String[] {"SENDRESPONSE"};
		}
		return resp;
	}

	private void retrieveInterfaceName(Map<String, Interface> ifAliasMap, String deviceIP) {
		if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
			try {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifDescr." + ifAlias + " ";
				}
				String command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("snmpget").trim(), community, deviceIP, arg);
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
			} catch (Exception e) {
				log.error(e,e);
			}
		}
	}

	private void retrieveInterfaceLastStatusChange(Map<String, Interface> ifAliasMap, String deviceIP) {
		if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
			try {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifLastChange." + ifAlias + " ";
				}
				String command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("snmpget").trim(), community, deviceIP, arg);
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
			} catch (Exception e) {
				log.error(e,e);
			}
		}
	}

	private void retrieveInterfaceIpAddress(Map<String, Interface> ifAliasMap, String deviceIP) {
		if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
			try {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifPhysAddress." + ifAlias + " ";
				}
				String command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("snmpget").trim(), community, deviceIP, arg);
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
			} catch (Exception e) {
				log.error(e,e);
			}
		}
	}

	private void retrieveInterfaceOperStatus(Map<String, Interface> ifAliasMap, String deviceIP) {
		if(ifAliasMap != null && !ifAliasMap.isEmpty() && deviceIP != null && !"".equals(deviceIP)) {
			try {
				String arg = "";
				for(String ifAlias : ifAliasMap.keySet()) {
					arg+= "ifOperStatus." + ifAlias + " ";
				}
				String command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("snmpget").trim(), community, deviceIP, arg);
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
			} catch (Exception e) {
				log.error(e,e);
			}
		}
	}

	private String retrieveInterfaceSysUpTime(String deviceIP) {
		String sysUpTime = null;
		if(deviceIP != null && !"".equals(deviceIP)) {
			try {
				String command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("snmpwalk").trim(), community, deviceIP, "sysUpTime");
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
			} catch (Exception e) {
				log.error(e,e);
			}
		}
		return sysUpTime;
	}

	private String getIfAlias(String line) {
		if(line != null && (line.contains("= Timeticks:") || line.contains("= STRING:") ||  line.contains("= INTEGER:")) ) {
			String[] split = line.split("=");
			if(split != null && split.length > 0) {
				split[0] = split[0].trim();
				int index = split[0].lastIndexOf(".")+1;
				if(index != -1) { 
					return split[0].substring(index, split[0].length());
				}
			}
		}
		return null;
	}

	private String getIfValue(String line) {
		if(line != null) {
			String splitRegex = "";
			if(line.contains("= Timeticks:")) {
				splitRegex = "= Timeticks:";
			} else if(line.contains("= STRING:")) {
				splitRegex = "= STRING:";
			} else if(line.contains("= INTEGER:")){
				splitRegex = "= INTEGER:";
			}

			String[] split = line.split(splitRegex);
			if(split != null && split.length > 1) {
				split[1] = split[1].trim();
				if(splitRegex.equals("= Timeticks:") && split[1].lastIndexOf(")") > 0) {
					split[1] = split[1].substring(split[1].lastIndexOf(")")+1, split[1].length());
				}
				return split[1].trim();
			}
		}
		return null;
	}
}
