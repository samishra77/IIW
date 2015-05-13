package com.colt.connect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.apt.business.AttributeType;
import com.colt.apt.business.Device;
import com.colt.apt.business.User;
import com.colt.apt.business.util.WSBind;
import com.colt.common.aptcache.IDeviceDAO;

/**
 * @author Intelinet
 */

public class TelnetDevice {
	protected Log log;
	protected TelnetDevice telnet;

	public TelnetDevice() {
		log = LogFactory.getLog(this.getClass());
	}


	private String getConnectivityProtocol(String ipAddress) throws Exception {
		//Get protocol info from APT database
		IDeviceDAO deviceDAO = (IDeviceDAO) WSBind.bind(IDeviceDAO.class);
		Device[] dev = deviceDAO.retrieveDevicesByAddress(new User(), ipAddress);
		String protocol = "telnet";
		if(dev != null && dev[0] != null) {
			protocol = dev[0].getDeviceAttributeValue( AttributeType.CONNECT_PROTOCOL );
		}
		if(protocol != null && protocol.equalsIgnoreCase("ssh")){
			return "ssh";
		}else{
			return "telnet";
		}
	}

	public void connect(String server, int _timeout) throws Exception {
		String connectProtocol=getConnectivityProtocol(server);
		connect(server, _timeout, connectProtocol);
	}


	public void connect(String server, int _timeout, String connectProtocol) throws Exception {
		if("telnet".equalsIgnoreCase(connectProtocol)){
			telnet = new ConnectTelnet();
		}
		if("ssh".equalsIgnoreCase(connectProtocol)){
			telnet = new ConnectSSH();
		}

		telnet.connect(server, _timeout);
	}

	public String waitfor(String pattern) throws Exception {
		return telnet.waitfor(pattern);
	}

	public void write(String word) throws Exception {
		telnet.write(word);
	}


	public String sendCmd(String command, String nexttoken) throws Exception {
		telnet.sendCmd(command, nexttoken);
		return telnet.waitfor( nexttoken );
	}

	public void disconnect() {
		telnet.disconnect();
	}

	/**
	 * @throws Exception
	 */

	public String applyCommands(String commands) throws Exception {
		return applyCommands(commands, null);
	}

	public String applyCommands(String commands, String endTag) throws Exception {
		return telnet.applyCommands(commands, endTag);
	}

	public String getOutput() {
		return telnet.getOutput();

	}

	public void prepareForCommands(String vendor, String devicetype, String suffix) throws Exception {
		telnet.prepareForCommands(vendor, devicetype, suffix);
	}

	public String sendBREAK(String nexttoken) throws Exception {

		return telnet.sendBREAK(nexttoken);
	}

	public void setWaitForTimeDetails(int maxRunTime, int sleepInterval, int sleepCountMax) {
		telnet.setWaitForTimeDetails(maxRunTime,sleepInterval,sleepCountMax);
	}
}
