package com.colt.adapters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.colt.connect.ConnectSSH;
import com.colt.connect.ConnectTelnet;
import com.colt.util.AgentUtil;
import com.colt.util.DeviceCommand;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.Interface;

public class CiscoXRAdapter extends Adapter {

	@Override
	public DeviceDetail fetch(String circuitID, String ipAddress) throws Exception {
		DeviceDetail deviceDetail = new DeviceDetail();
		if(ipAddress != null && !"".equals(ipAddress) && circuitID != null && !"".equals(circuitID)) {
			try {
				ConnectTelnet telnetdev = new ConnectTelnet();
				telnetdev.connect(ipAddress, 15);
				telnetdev.prepareForCommands(FactoryAdapter.VENDOR_CISCO);
				executeCommands(telnetdev, null, ipAddress, circuitID, deviceDetail);
			} catch (Exception e) {
				try {
					ConnectSSH sshdev = new ConnectSSH();
					sshdev.connect(ipAddress, 15);
					sshdev.prepareForCommands(FactoryAdapter.VENDOR_CISCO);
					executeCommands(null, sshdev, ipAddress, circuitID, deviceDetail);
				} catch (Exception e2) {
					throw e2;
				}
			}
		}
		return deviceDetail;
	}

	private void executeCommands(ConnectTelnet telnetdev, ConnectSSH sshdev, String ipAddress, String circuitID, DeviceDetail deviceDetail) throws Exception {
		retrieveDeviceUpTime(telnetdev, sshdev, deviceDetail);
		if(ipAddress != null && !"".equals(ipAddress)) {
			retrieveWanInterface(telnetdev, sshdev, ipAddress, deviceDetail);
		}
		if(circuitID != null && !"".equals(circuitID)) {
			retrieveCircuitInterface(telnetdev, sshdev, circuitID, deviceDetail);
		}
		if(telnetdev != null) {
			telnetdev.disconnect();
		} else if(sshdev != null) {
			sshdev.disconnect();
		}
	}

	private void retrieveDeviceUpTime(ConnectTelnet telnetdev, ConnectSSH sshdev, DeviceDetail deviceDetail) throws Exception {
		String command = DeviceCommand.getDefaultInstance().getProperty("cisco.showDeviceUptime").trim();
		if(command != null && !"".equals(command)) {
			String output = "";
			if(telnetdev != null) {
				output = telnetdev.applyCommands(command);
			} else if(sshdev != null) {
				output = sshdev.applyCommands(command);
			}
			if(output != null && !"".equals(output)) {
				String[] array = null;
				if(output.indexOf("\n") > -1) {
					array = output.split("\n");
				} else if(output.indexOf("\r\n") > -1) {
					array = output.split("\r\n");
				} else {
					array = new String[] {output};
				}
				
				array = output.split("\r\n");
				if(array != null && array.length > 0) {
					for(String a : array) {
						if(a.contains("uptime is")) {
							String[] uptime = a.split("uptime is");
							if(uptime != null && uptime.length > 1) {
								uptime[1] = uptime[1].trim();
								uptime = uptime[1].split(",");
								if(uptime != null && uptime.length > 2) {
									if(uptime[0].contains("week") && uptime[1].contains("day")) {
										String[] weekArray = uptime[0].split(" ");
										String[] dayArray = uptime[1].split(" ");
										if(weekArray != null && weekArray.length > 0 && dayArray != null && dayArray.length > 1) {
											int parseWeek = Integer.valueOf(uptime[0].split(" ")[0]) * 7;
											int parseDay = Integer.valueOf(uptime[1].split(" ")[1]);
											int totalDay = parseWeek + parseDay;
											String sysUpTime = "";
											if(totalDay != 1) {
												sysUpTime = totalDay + " days" + uptime[2] + uptime[3];
											} else {
												sysUpTime = totalDay + " day" + uptime[2] + uptime[3];
											}
											deviceDetail.setTime(sysUpTime);
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void retrieveWanInterface(ConnectTelnet telnetdev, ConnectSSH sshdev, String ipAddress, DeviceDetail deviceDetail) throws Exception {
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.xr.showIpInterfaces").trim(), ipAddress);
		String output = "";
		if(telnetdev != null) {
			output = telnetdev.applyCommands(command);
		} else if(sshdev != null) {
			output = sshdev.applyCommands(command);
		}

		if(output != null && !"".equals(output)) {
			List<Interface> interfaceList = new ArrayList<Interface>();
			Interface interf = null;
			//split each line
			String[] outputArray = null;
			if(output.indexOf("\n") > -1) {
				outputArray = output.split("\n");
			} else if(output.indexOf("\r\n") > -1) {
				outputArray = output.split("\r\n");
			} else {
				outputArray = new String[] {output};
			}
			//remove headers
			List<String> outputResultList = new ArrayList<String>();
			for (int i = 0; i < outputArray.length; i++) {
				if(i > 6) {
					outputResultList.add(outputArray[i]);
				}
			}
			//process data
			if(!outputResultList.isEmpty()) {
				List<String> values = null;
				for(String line : outputResultList) {
					line = line.trim();
					String[] lineArray = line.split(" ");
					values = new ArrayList<String>();
					for(String l : lineArray) {
						if(!" ".equals(l) && !"".equals(l)) {
							values.add(l);
						}
					}
					interf = new Interface();
					interf.setIpaddress(ipAddress);
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
					}
					interfaceList.add(interf);
				}
			}
			if(!interfaceList.isEmpty()) {
				deviceDetail.getInterfaces().addAll(interfaceList);
			}
		}
	}

	private void retrieveCircuitInterface(ConnectTelnet telnetdev, ConnectSSH sshdev, String circuitID, DeviceDetail deviceDetail) throws Exception {
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.showInterfaceDescription").trim(), circuitID);
		String output = "";
		if(telnetdev != null) {
			output = telnetdev.applyCommands(command);
		} else if(sshdev != null) {
			output = sshdev.applyCommands(command);
		}

		if(output != null && !"".equals(output)) {
			List<Interface> interfaceList = new ArrayList<Interface>();
			Interface interf = null;
			String[] array = output.split("\r\n");
			if(array != null && array.length > 0) {
				List<String> values = null;
				for(String line : array) {
					if(line.contains("SID["+circuitID+"]")) {
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
										interf.setName(interfaceData[i].substring(1, interfaceData[i].length()));
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
							break;
						}
					}
				}
			}
			if(!interfaceList.isEmpty()) {
				deviceDetail.getInterfaces().addAll(interfaceList);
			}
		}
	}
}
