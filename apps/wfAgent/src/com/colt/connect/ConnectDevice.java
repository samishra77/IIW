package com.colt.connect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Intelinet
 */

public class ConnectDevice {
	protected Log log;
	protected ConnectDevice connectDevice;

	public ConnectDevice() {
		log = LogFactory.getLog(this.getClass());
	}

	public void connect(String server, int _timeout, String connectProtocol) throws Exception {
		if("telnet".equalsIgnoreCase(connectProtocol)){
			connectDevice = new ConnectTelnet();
		}
		if("ssh".equalsIgnoreCase(connectProtocol)){
			connectDevice = new ConnectSSH();
		}
		connectDevice.connect(server, _timeout, connectProtocol);
	}

	public String waitfor(String pattern) throws Exception {
		return connectDevice.waitfor(pattern);
	}

	public void write(String word) throws Exception {
		connectDevice.write(word);
	}


	public String sendCmd(String command, String nexttoken) throws Exception {
		connectDevice.sendCmd(command, nexttoken);
		return connectDevice.waitfor( nexttoken );
	}

	public void disconnect() {
		connectDevice.disconnect();
	}

	/**
	 * @throws Exception
	 */

	public String applyCommands(String commands) throws Exception {
		return applyCommands(commands, null);
	}

	public String applyCommands(String commands, String endTag) throws Exception {
		return connectDevice.applyCommands(commands, endTag);
	}

	public String getOutput() {
		return connectDevice.getOutput();

	}

	public void prepareForCommands(String vendor) throws Exception {
		connectDevice.prepareForCommands(vendor);
	}

	public String sendBREAK(String nexttoken) throws Exception {

		return connectDevice.sendBREAK(nexttoken);
	}

	public void setWaitForTimeDetails(int maxRunTime, int sleepInterval, int sleepCountMax) {
		connectDevice.setWaitForTimeDetails(maxRunTime,sleepInterval,sleepCountMax);
	}
}

