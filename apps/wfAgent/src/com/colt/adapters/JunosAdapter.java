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

public class JunosAdapter extends Adapter {

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
		retrieveLastStatusChange(circuitID, ipAddress, deviceDetail);
	}

	private void retrieveDeviceUpTime(ConnectTelnet telnetdev, ConnectSSH sshdev, DeviceDetail deviceDetail) throws Exception {
		String command = DeviceCommand.getDefaultInstance().getProperty("junos.showDeviceUptime").trim();
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
								deviceDetail.setTime(day + hour + minute);
								break;
							}
						}
					}
				}
			}
		}
	}

	private void retrieveWanInterface(ConnectTelnet telnetdev, ConnectSSH sshdev, String ipAddress, DeviceDetail deviceDetail) throws Exception {
		String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaces").trim(), ipAddress);
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
			 if(output.indexOf("\r\n") > -1) {
				outputArray = output.split("\r\n");
			} else {
				outputArray = new String[] {output};
			}

			//process data
			if(outputArray != null && outputArray.length > 1) {
				List<String> values = null;
				String line = outputArray[1].trim();
				String[] lineArray = line.split(" ");
				values = new ArrayList<String>();
				for(String l : lineArray) {
					if(!" ".equals(l) && !"".equals(l)) {
						values.add(l);
					}
				}
				interf = new Interface();
				interf.setIpaddress("192.168.0.5");
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
					if(line.contains("L3Circuit[FRA/FRA/IA-127158]")) {
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
