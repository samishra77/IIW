package com.colt.adapters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.colt.util.AgentUtil;
import com.colt.util.DeviceCommand;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.Interface;


public abstract class Adapter {

	public abstract DeviceDetail fetch(String circuitID, String ipAddress) throws Exception;

	protected void retrieveLastStatusChange(String circuitID, String ipAddress, DeviceDetail deviceDetail) throws Exception {
		String community = DeviceCommand.getDefaultInstance().getProperty("community").trim();
		String command = MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("snmpwalk").trim(), community, ipAddress, "ifAlias");
		List<String> outputList = AgentUtil.runLocalCommand(command);
		if(outputList != null && !outputList.isEmpty()) {
			Map<String, Interface> ifAliasMap = new HashMap<String, Interface>();
			String l3CircuitParam = "L3Circuit["+circuitID+"]";
			String sidParam = "Cct["+circuitID+"]";
			for(String line : outputList) {
				if(line.contains(l3CircuitParam) || line.contains(sidParam)) {
					String ifAlias = getIfAlias(line);
					if(ifAlias != null && !"".equals(ifAlias)) {
						ifAliasMap.put(ifAlias, new Interface());
					}
				}
			}

			retrieveSNMPInterfaceName(ifAliasMap, ipAddress, community);
			retrieveSNMPInterfaceLastStatusChange(ifAliasMap, ipAddress, community);
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

	private void retrieveSNMPInterfaceName(Map<String, Interface> ifAliasMap, String deviceIP, String community) throws Exception {
		if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
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
		}
	}

	private void retrieveSNMPInterfaceLastStatusChange(Map<String, Interface> ifAliasMap, String deviceIP, String community) throws Exception {
		if(ifAliasMap != null && !ifAliasMap.isEmpty()) {
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
		}
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
