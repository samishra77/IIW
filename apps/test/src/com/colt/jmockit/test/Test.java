package com.colt.jmockit.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;

import com.colt.adapters.CiscoIOSAdapter;
import com.colt.aopwf.FetchAPTDeviceIPActivity;
import com.colt.aopwf.PingActivity;
import com.colt.aopwf.SNMPFetchActivity;
import com.colt.apt.business.Device;
import com.colt.apt.business.User;
import com.colt.connect.ConnectDevice;
import com.colt.connect.ConnectSSH;
import com.colt.connect.ConnectTelnet;
import com.colt.jmockit.util.Util;
import com.colt.util.AgentUtil;
import com.colt.util.SNMPUtil;
import com.colt.ws.biz.DeviceDetail;
import com.colt.ws.biz.DeviceDetailsRequest;
import com.colt.ws.biz.DeviceType;
import com.colt.ws.biz.IDeviceDetailsResponse;
import com.colt.ws.biz.Interface;
import com.colt.ws.biz.L3DeviceDetailsResponse;

import electric.registry.Registry;

public class Test {

	@org.junit.Test
	public void pingCPEPEFailsTest() throws Exception {
		String[] resp = new String[] {"PING_FAIL"};
		
		new MockUp<PingActivity>() {
			@Mock
			Map<String, Boolean> ping(String target){
				Assert.assertNotNull(target);
				Map<String, Boolean> ipStatus = new HashMap<String, Boolean>();
				ipStatus.put(target, Boolean.FALSE);
				return ipStatus;
			}
		};

		List<DeviceDetailsRequest> listDeviceDetail = new ArrayList<DeviceDetailsRequest>();
		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.200");
		listDeviceDetail.add(deviceDetail);

		deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("PE");
		deviceDetail.setIp("192.168.0.201");
		listDeviceDetail.add(deviceDetail);

		PingActivity ping = new PingActivity();
		Map<String,Object> input = null;
		for(DeviceDetailsRequest dd : listDeviceDetail) {
			input = new HashMap<String,Object>();
			input.put("deviceDetails", dd);
			Assert.assertArrayEquals(resp, ping.process(input));
		}
	}

	@org.junit.Test
	public void pingCPEPassPEFailsTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		PingActivity ping = new PingActivity();
		String[] respSuccess = new String[] {"PING_SUCCESS"};
		String[] respFail = new String[] {"PING_FAIL"};
		
		new MockUp<PingActivity>() {
			@Mock
			Map<String, Boolean> ping(String target){
				Assert.assertNotNull(target);
				Map<String, Boolean> ipStatus = new HashMap<String, Boolean>();
				if(target.equals("192.168.0.200")) {
					ipStatus.put(target, Boolean.TRUE);
				} else {
					ipStatus.put(target, Boolean.FALSE);
				}
				return ipStatus;
			}
		};

		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.200");
		input.put("deviceDetails", deviceDetail);
		Assert.assertArrayEquals(respSuccess, ping.process(input));

		deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("PE");
		deviceDetail.setIp("192.168.0.201");
		input.put("deviceDetails", deviceDetail);
		Assert.assertArrayEquals(respFail, ping.process(input));
	}

	@org.junit.Test
	public void pingCPEFailsPEPassTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		PingActivity ping = new PingActivity();
		String[] respSuccess = new String[] {"PING_SUCCESS"};
		String[] respFail = new String[] {"PING_FAIL"};
		
		new MockUp<PingActivity>() {
			@Mock
			Map<String, Boolean> ping(String target){
				Assert.assertNotNull(target);
				Map<String, Boolean> ipStatus = new HashMap<String, Boolean>();
				if(target.equals("192.168.0.200")) {
					ipStatus.put(target, Boolean.TRUE);
				} else {
					ipStatus.put(target, Boolean.FALSE);
				}
				return ipStatus;
			}
		};

		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("PE");
		deviceDetail.setIp("192.168.0.200");
		input.put("deviceDetails", deviceDetail);
		Assert.assertArrayEquals(respSuccess, ping.process(input));

		deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.201");
		input.put("deviceDetails", deviceDetail);
		Assert.assertArrayEquals(respFail, ping.process(input));
	}

	@org.junit.Test
	public void pingCPEPassPEPassTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		PingActivity ping = new PingActivity();
		String[] respSuccess = new String[] {"PING_SUCCESS"};
	
		
		new MockUp<PingActivity>() {
			@Mock
			Map<String, Boolean> ping(String target){
				Assert.assertNotNull(target);
				Map<String, Boolean> ipStatus = new HashMap<String, Boolean>();
				ipStatus.put(target, Boolean.TRUE);
				return ipStatus;
			}
		};

		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.200");
		input.put("deviceDetails", deviceDetail);
		Assert.assertArrayEquals(respSuccess, ping.process(input));

		deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("PE");
		deviceDetail.setIp("192.168.0.201");
		input.put("deviceDetails", deviceDetail);
		Assert.assertArrayEquals(respSuccess, ping.process(input));
	}

	@org.junit.Test
	public void getCPEIPTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		FetchAPTDeviceIPActivity fetchAPT = new FetchAPTDeviceIPActivity();
		new MockUp<Registry>() {
			@Mock
			Object bind(String url, Class cl){
				Assert.assertNotNull(url);
				Assert.assertNotNull(cl);
				return new DeviceDAO();
			}
		};
		new MockUp<DeviceDAO>() {
			@Mock
			Device[] retrieveDevicesByName(User user, String deviceName){
				Device deviceExpected = new Device();
				Random random = new Random();
				Device device = new Device();
				int oct = (random.nextInt(253)+1);
				deviceExpected.setAddress("");
				Assert.assertNotNull(user);
				Assert.assertNotNull(deviceName);
				device.setAddress("192.168.0." + oct );
				deviceExpected.setAddress("192.168.0." + oct);
				Device[] deviceArray = new Device[]{device};
				Assert.assertEquals(deviceExpected.getAddress(), deviceArray[0].getAddress());
				return deviceArray;
			}
		};
		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.200");
		input.put("deviceDetails", deviceDetail);
		fetchAPT.process(input);
	}

	@org.junit.Test
	public void getPEIPTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		FetchAPTDeviceIPActivity fetchAPT = new FetchAPTDeviceIPActivity();
		new MockUp<Registry>() {
			@Mock
			Object bind(String url, Class cl){
				Assert.assertNotNull(url);
				Assert.assertNotNull(cl);
				return new DeviceDAO();
			}
		};
		new MockUp<DeviceDAO>() {
			@Mock
			Device[] retrieveDevicesByName(User user, String deviceName){
				Device deviceExpected = new Device();
				Random random = new Random();
				Device device = new Device();
				int oct = (random.nextInt(253)+1);
				deviceExpected.setAddress("");
				Assert.assertNotNull(user);
				Assert.assertNotNull(deviceName);
				device.setAddress("192.168.0." + oct );
				deviceExpected.setAddress("192.168.0." + oct);
				Device[] deviceArray = new Device[]{device};
				Assert.assertEquals(deviceExpected.getAddress(), deviceArray[0].getAddress());
				return deviceArray;
			}
		};
		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("PE");
		deviceDetail.setIp("192.168.0.201");
		input.put("deviceDetails", deviceDetail);
		fetchAPT.process(input);
	}

	@org.junit.Test
	public void getNoRecordCPEAndPEResultTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		FetchAPTDeviceIPActivity fetchAPT = new FetchAPTDeviceIPActivity();
		new MockUp<Registry>() {
			@Mock
			Object bind(String url, Class cl){
				Assert.assertNotNull(url);
				Assert.assertNotNull(cl);
				return new DeviceDAO();
			}
		};
		new MockUp<DeviceDAO>() {
			@Mock
			Device[] retrieveDevicesByName(User user, String deviceName){
				Device deviceExpected = new Device();
				Random random = new Random();
				Device device = new Device();
				int oct = (random.nextInt(253)+1);
				deviceExpected.setAddress("");
				Assert.assertNotNull(user);
				Assert.assertNotNull(deviceName);
				device.setAddress("192.168.0." + oct );
				deviceExpected.setAddress("192.168.0." + oct);
				if(deviceName.equals("name_device_teste2268885392_cpe")) {
					return null;
				} else {
					Device[] deviceArray = new Device[]{device};
					return deviceArray;
				}
			}
		};
		List<DeviceDetailsRequest> listDeviceDetail = new ArrayList<DeviceDetailsRequest>();
		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392_cpe");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.201");
		listDeviceDetail.add(deviceDetail);
		deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392_pe");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("PE");
		deviceDetail.setIp("192.168.0.200");
		listDeviceDetail.add(deviceDetail);
		for (DeviceDetailsRequest ddr : listDeviceDetail) {
			input.put("deviceDetails", ddr);
			fetchAPT.process(input);
		}
	}

	@org.junit.Test
	public void getNoRecordPEAndCPEResultTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		FetchAPTDeviceIPActivity fetchAPT = new FetchAPTDeviceIPActivity();
		new MockUp<Registry>() {
			@Mock
			Object bind(String url, Class cl){
				Assert.assertNotNull(url);
				Assert.assertNotNull(cl);
				return new DeviceDAO();
			}
		};
		new MockUp<DeviceDAO>() {
			@Mock
			Device[] retrieveDevicesByName(User user, String deviceName){
				Device deviceExpected = new Device();
				Random random = new Random();
				Device device = new Device();
				int oct = (random.nextInt(253)+1);
				deviceExpected.setAddress("");
				Assert.assertNotNull(user);
				Assert.assertNotNull(deviceName);
				device.setAddress("192.168.0." + oct );
				deviceExpected.setAddress("192.168.0." + oct);
				if(deviceName.equals("name_device_teste2268885392_cpe")) {
					Device[] deviceArray = new Device[]{device};
					return deviceArray;
				} else {
					return null;
				}
			}
		};
		List<DeviceDetailsRequest> listDeviceDetail = new ArrayList<DeviceDetailsRequest>();
		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392_cpe");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.201");
		listDeviceDetail.add(deviceDetail);
		deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392_pe");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("PE");
		deviceDetail.setIp("192.168.0.200");
		listDeviceDetail.add(deviceDetail);
		for (DeviceDetailsRequest ddr : listDeviceDetail) {
			input.put("deviceDetails", ddr);
			fetchAPT.process(input);
		}
	}

	@org.junit.Test
	public void getNoRecordCPEandPETest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		FetchAPTDeviceIPActivity fetchAPT = new FetchAPTDeviceIPActivity();
		new MockUp<Registry>() {
			@Mock
			Object bind(String url, Class cl){
				Assert.assertNotNull(url);
				Assert.assertNotNull(cl);
				return new DeviceDAO();
			}
		};
		new MockUp<DeviceDAO>() {
			@Mock
			Device[] retrieveDevicesByName(User user, String deviceName){
				Device deviceExpected = new Device();
				Random random = new Random();
				Device device = new Device();
				int oct = (random.nextInt(253)+1);
				deviceExpected.setAddress("");
				Assert.assertNotNull(user);
				Assert.assertNotNull(deviceName);
				device.setAddress("192.168.0." + oct );
				deviceExpected.setAddress("192.168.0." + oct);
				return null;
			}
		};
		List<DeviceDetailsRequest> listDeviceDetail = new ArrayList<DeviceDetailsRequest>();
		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392_cpe");
		deviceDetail.setIp("192.168.0.201");
		listDeviceDetail.add(deviceDetail);
		deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392_pe");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("PE");
		deviceDetail.setIp("192.168.0.200");
		listDeviceDetail.add(deviceDetail);
		for (DeviceDetailsRequest ddr : listDeviceDetail) {
			input.put("deviceDetails", ddr);
			fetchAPT.process(input);
		}
		
	}

	@org.junit.Test
	public void snmpFailExceptionTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		SNMPFetchActivity snmpFetch = new SNMPFetchActivity();

		new MockUp<AgentUtil>() {
			@Mock
			List<String> runLocalCommand(String cmd) throws Exception {
				throw new Exception();
			}
		};

		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.200");
		input.put("deviceDetails", deviceDetail);

		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		input.put("deviceDetailsResponse", deviceDetailsResponse);
		input.put("snmpVersion", 3);

		snmpFetch.process(input);
		Assert.assertNotNull(deviceDetailsResponse.getErrorResponse());
	}

	@org.junit.Test
	public void snmpFailTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		SNMPFetchActivity snmpFetch = new SNMPFetchActivity();

		new MockUp<AgentUtil>() {
			@Mock
			List<String> runLocalCommand(String cmd) throws Exception {
				Assert.assertNotNull(cmd);
				Util util = new Util();
				String fileNameByCMD = "";
				if(cmd.contains("ifAlias")) {
					fileNameByCMD = "cpe/v3/fail/snmpOutIfAlias.txt";
				} else if(cmd.contains("sysUpTime")) {
					fileNameByCMD = "cpe/v3/success/snmpOutSysUpTime.txt";
				}
				List<String> output = util.readFileTestOutPut(fileNameByCMD);
				return output;
			}
		};

		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("IPC00011");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setServiceType("IPVPN");
		deviceDetail.setIp("192.168.0.200");
		input.put("deviceDetails", deviceDetail);

		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		deviceDetailsResponse.setCircuitID(deviceDetail.getCircuitID());
		deviceDetailsResponse.setDeviceIP(deviceDetail.getIp());
		deviceDetailsResponse.setDeviceDetails(new DeviceDetail());
		input.put("deviceDetailsResponse", deviceDetailsResponse);
		input.put("snmpVersion", 3);

		snmpFetch.process(input);
		Assert.assertNotNull(deviceDetailsResponse.getDeviceDetails());
		Assert.assertNotNull(deviceDetailsResponse.getDeviceDetails().getTime());
		Assert.assertEquals("197d 4h 29m ", deviceDetailsResponse.getDeviceDetails().getTime());
	}

	@org.junit.Test
	public void snmpPassTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		SNMPFetchActivity snmpFetch = new SNMPFetchActivity();
	
		new MockUp<AgentUtil>() {
			@Mock
			List<String> runLocalCommand(String cmd) throws Exception {
				Assert.assertNotNull(cmd);
				Util util = new Util();
				String fileNameByCMD = "";
				if(cmd.contains("ifAlias")) {
					fileNameByCMD = "cpe/v2/success/snmpOutIfAlias.txt";
				} else if(cmd.contains("ifDescr")) {
					fileNameByCMD = "cpe/v2/success/snmpOutifDescr.txt";
				} else if(cmd.contains("ifLastChange")) {
					fileNameByCMD = "cpe/v2/success/snmpOutifLastChange.txt";
				} else if(cmd.contains("ipAdEntIfIndex")) {
					fileNameByCMD = "cpe/v2/success/snmpOutifPhysAddress.txt";
				} else if(cmd.contains("ifOperStatus")) {
					fileNameByCMD = "cpe/v2/success/snmpOutifOperStatus.txt";
				} else if(cmd.contains("sysUpTime")) {
					fileNameByCMD = "cpe/v2/success/snmpOutSysUpTime.txt";
				}
				List<String> output = util.readFileTestOutPut(fileNameByCMD);
				return output;
			}
		};

		DeviceDetailsRequest deviceDetail = new DeviceDetailsRequest();
		deviceDetail.setCircuitID("BER/BER/IA-129771");
		deviceDetail.setName("name_device_teste2268885392");
		deviceDetail.setRequestID("1");
		deviceDetail.setSeibelUserID("danielamas@intelinet.com.br");
		deviceDetail.setDeviceType(new DeviceType());
		deviceDetail.getDeviceType().setVendor("Cisco");
		deviceDetail.getDeviceType().setModel("2741");
		deviceDetail.setType("CPE");
		deviceDetail.setIp("192.168.0.200");
		deviceDetail.setServiceType("IPVPN");
		input.put("deviceDetails", deviceDetail);

		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		deviceDetailsResponse.setDeviceIP("62.96.36.230");
		deviceDetailsResponse.setCircuitID("BER/BER/IA-129771");
		deviceDetailsResponse.setDeviceDetails(new DeviceDetail());
		input.put("deviceDetailsResponse", deviceDetailsResponse);
		input.put("snmpVersion", 2);
		input.put("community", "8ef01");
		

		snmpFetch.process(input);
		Assert.assertNotNull(deviceDetailsResponse.getDeviceDetails());
		Assert.assertNotNull(deviceDetailsResponse.getDeviceDetails().getTime());
		Assert.assertNotNull(deviceDetailsResponse.getDeviceDetails().getInterfaces());
		Assert.assertEquals(deviceDetailsResponse.getDeviceDetails().getInterfaces().size(), 1);
		Assert.assertEquals(deviceDetailsResponse.getDeviceDetails().getInterfaces().get(0).getIpaddress(), "172.22.40.82");
	}

	@org.junit.Test
	public void snmpLastStatusChangeForCLIInterfacesTest() throws Exception {
		Map<String,Object> input = new HashMap<String,Object>();
		SNMPUtil snmpUtil = new SNMPUtil(2, "PE", "IPVPN");
	
		new MockUp<AgentUtil>() {
			@Mock
			List<String> runLocalCommand(String cmd) throws Exception {
				Assert.assertNotNull(cmd);
				Util util = new Util();
				String fileNameByCMD = "";
				if(cmd.contains("ifAlias")) {
					fileNameByCMD = "cpe/v2/success/snmpOutIfAlias.txt";
				} else if(cmd.contains("ifDescr")) {
					fileNameByCMD = "cpe/v2/success/snmpOutifDescr.txt";
				} else if(cmd.contains("ifLastChange")) {
					fileNameByCMD = "cpe/v2/success/snmpOutifLastChange.txt";
				} else if(cmd.contains("ifPhysAddress")) {
					fileNameByCMD = "cpe/v2/success/snmpOutifPhysAddress.txt";
				} else if(cmd.contains("ifOperStatus")) {
					fileNameByCMD = "cpe/v2/success/snmpOutifOperStatus.txt";
				} else if(cmd.contains("sysUpTime")) {
					fileNameByCMD = "cpe/v2/success/snmpOutSysUpTime.txt";
				}
				List<String> output = util.readFileTestOutPut(fileNameByCMD);
				return output;
			}
		};

		DeviceDetail deviceDetails = new DeviceDetail();
		IDeviceDetailsResponse deviceDetailsResponse = new L3DeviceDetailsResponse();
		deviceDetailsResponse.setDeviceIP("192.168.0.200");
		deviceDetailsResponse.setCircuitID("CES/LON/IA-171851");
		deviceDetailsResponse.setDeviceDetails(deviceDetails);

		Interface interf = new Interface();
		interf.setName("ge-0/0/2.122");
		deviceDetails.getInterfaces().add(interf);

		interf = new Interface();
		interf.setName("ge-0/1/9.299");
		deviceDetails.getInterfaces().add(interf);

		snmpUtil.retrieveLastStatusChange("192.168.0.200", deviceDetailsResponse);

		Assert.assertNotNull(deviceDetailsResponse);
		Assert.assertNotNull(deviceDetailsResponse.getDeviceDetails().getInterfaces().get(0).getLastChgTime());
		Assert.assertEquals(deviceDetailsResponse.getDeviceDetails().getInterfaces().get(0).getLastChgTime(), "0:00:48.01");
		
		Assert.assertNotNull(deviceDetailsResponse.getDeviceDetails().getInterfaces().get(1).getLastChgTime());
		Assert.assertEquals(deviceDetailsResponse.getDeviceDetails().getInterfaces().get(1).getLastChgTime(), "0:00:54.21");

	}

	@org.junit.Test
	public void cliTelnetPassTest() throws Exception {
		CiscoIOSAdapter ciscoIOSAdapter = new CiscoIOSAdapter();
		
		new MockUp<ConnectTelnet>() {
			@Mock
			void connect(String server, int _timeout, String connectProtocol) throws Exception {
				Assert.assertNotNull(server);
			}
		};

		new MockUp<ConnectTelnet>() {
			@Mock
			void prepareForCommands(String vendor) throws Exception {
				Assert.assertNotNull(vendor);
			}
		};

		new MockUp<CiscoIOSAdapter>() {
			@Mock
			void executeCommands(ConnectDevice connectDevice, String ipAddress, String circuitID, int snmpVersion, IDeviceDetailsResponse deviceDetailsResponse, String deviceName) {
				Assert.assertNotNull(connectDevice);
				Assert.assertNotNull(ipAddress);
				Assert.assertNotNull(circuitID);
				Assert.assertNotNull(deviceDetailsResponse);
				Assert.assertNotNull(deviceName);
			}
		};
		IDeviceDetailsResponse deviceDetailsResponse = ciscoIOSAdapter.fetch("CES/LON/IA-171851", "192.168.0.5", 2, "name_device_teste2268885392", null);
		Assert.assertNull(deviceDetailsResponse.getCircuitID());
		Assert.assertNull(deviceDetailsResponse.getResponseID());
		Assert.assertNull(deviceDetailsResponse.getDeviceDetails());
		Assert.assertNull(deviceDetailsResponse.getErrorResponse());
	}

	@org.junit.Test
	public void cliTelnetFailSShPassTest() throws Exception {
		CiscoIOSAdapter ciscoIOSAdapter = new CiscoIOSAdapter();
		
		new MockUp<ConnectTelnet>() {
			@Mock
			void connect(String server, int _timeout, String connectProtocol) throws Exception {
				Assert.assertNotNull(server);
				throw new Exception();
			}
		};

		new MockUp<ConnectSSH>() {
			@Mock
			void connect(String server, int _timeout, String connectProtocol) throws Exception {
				Assert.assertNotNull(server);
			}
		};

		new MockUp<ConnectSSH>() {
			@Mock
			void prepareForCommands(String vendor) throws Exception {
				Assert.assertNotNull(vendor);
			}
		};

		new MockUp<CiscoIOSAdapter>() {
			@Mock
			void executeCommands(ConnectDevice connectDevice, String ipAddress, String circuitID, int snmpVersion, IDeviceDetailsResponse deviceDetailsResponse, String deviceName) {
				Assert.assertNotNull(connectDevice);
				Assert.assertNotNull(ipAddress);
				Assert.assertNotNull(circuitID);
				Assert.assertNotNull(deviceDetailsResponse);
				Assert.assertNotNull(deviceName);
			}
		};
		IDeviceDetailsResponse deviceDetailsResponse = ciscoIOSAdapter.fetch("CES/LON/IA-171851", "192.168.0.200", 2, "name_device_teste2268885392", null);
		Assert.assertNull(deviceDetailsResponse.getCircuitID());
		Assert.assertNull(deviceDetailsResponse.getResponseID());
		Assert.assertNull(deviceDetailsResponse.getDeviceDetails());
		Assert.assertNull(deviceDetailsResponse.getErrorResponse());
	}


	@org.junit.Test
	public void cliTelnetFailSShFailTest() throws Exception {
		CiscoIOSAdapter ciscoIOSAdapter = new CiscoIOSAdapter();
		
		new MockUp<ConnectTelnet>() {
			@Mock
			void connect(String server, int _timeout, String connectProtocol) throws Exception {
				Assert.assertNotNull(server);
				throw new Exception();
			}
		};

		new MockUp<ConnectSSH>() {
			@Mock
			void connect(String server, int _timeout, String connectProtocol) throws Exception {
				Assert.assertNotNull(server);
			}
		};

		new MockUp<ConnectSSH>() {
			@Mock
			void prepareForCommands(String vendor) throws Exception {
				Assert.assertNotNull(vendor);
				throw new Exception();
			}
		};

		try {
			IDeviceDetailsResponse deviceDetailsResponse = ciscoIOSAdapter.fetch("CES/LON/IA-171851", "192.168.0.4", 2, "name_device_teste2268885392", null);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}
}
