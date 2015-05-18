package com.colt.adapters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.connect.ConnectDevice;
import com.colt.util.AgentUtil;
import com.colt.util.DeviceCommand;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.Interface;

public class JunosAdapter extends Adapter {

	private Log log = LogFactory.getLog(CiscoIOSAdapter.class);

	@Override
	public DeviceDetail fetch(String circuitID, String ipAddress, int snmpVersion) throws Exception {
		DeviceDetail deviceDetail = new DeviceDetail();
		if(ipAddress != null && !"".equals(ipAddress) && circuitID != null && !"".equals(circuitID)) {
			ConnectDevice connectDevice = null;
			try {
				connectDevice = new ConnectDevice();
				connectDevice.connect(ipAddress, 15, "telnet");
			} catch (Exception e) {
				try {
					connectDevice = new ConnectDevice();
					connectDevice.connect(ipAddress, 15, "ssh");
				} catch (Exception e2) {
					throw e2;
				}
			}
			connectDevice.prepareForCommands(FactoryAdapter.VENDOR_JUNIPER);
			executeCommands(connectDevice, ipAddress, circuitID, deviceDetail, snmpVersion);
		}
		return deviceDetail;
	}

	private void executeCommands(ConnectDevice connectDevice, String ipAddress, String circuitID, DeviceDetail deviceDetail, int snmpVersion) {
		retrieveDeviceUpTime(connectDevice, deviceDetail);
		if(ipAddress != null && !"".equals(ipAddress)) {
			retrieveWanInterface(connectDevice, ipAddress, deviceDetail);
		}
		if(circuitID != null && !"".equals(circuitID)) {
			retrieveCircuitInterface(connectDevice, circuitID, deviceDetail);
		}

		SNMPUtil snmp = new SNMPUtil(snmpVersion);
		snmp.retrieveLastStatusChange(circuitID, ipAddress, deviceDetail);
	}

	private void retrieveDeviceUpTime(ConnectDevice connectDevice, DeviceDetail deviceDetail) {
		try {
			String command = DeviceCommand.getDefaultInstance().getProperty("junos.showDeviceUptime").trim();
			if(command != null && !"".equals(command)) {
				String output = connectDevice.applyCommands(command, ">");
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
		} catch (Exception e) {
			log.error(e,e);
		}
	}

	private void retrieveWanInterface(ConnectDevice connectDevice, String ipAddress, DeviceDetail deviceDetail) {
		try {
			String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("junos.showInterfaces").trim(), ipAddress);
			String output = connectDevice.applyCommands(command, ">");
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
				if(!interfaceList.isEmpty()) {
					deviceDetail.getInterfaces().addAll(interfaceList);
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
	}

	private void retrieveCircuitInterface(ConnectDevice connectDevice, String circuitID, DeviceDetail deviceDetail) {
		try {
			String command =  MessageFormat.format(DeviceCommand.getDefaultInstance().getProperty("cisco.showInterfaceDescription").trim(), circuitID);
			String output = connectDevice.applyCommands(command, ">");
			if(output != null && !"".equals(output)) {
				List<Interface> interfaceList = new ArrayList<Interface>();
				Interface interf = null;
				String[] array = output.split("\r\n");
				if(array != null && array.length > 0) {
					List<String> values = null;
					for(String line : array) {
						if(line.contains("L3Circuit[" + circuitID + "]")) {
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
		} catch (Exception e) {
			log.error(e,e);
		}
	}
}
