package com.colt.adapters;

import com.colt.connect.TelnetDevice;

public class HuaweiAdapter extends Adapter {

	@Override
	public void fetch(String ipAddress) throws Exception {
		if(ipAddress != null && !"".equals(ipAddress)) {
			TelnetDevice telnetdev = new TelnetDevice();
			String output = "";
			String command = "";
			try {
				telnetdev.connect(ipAddress, 15, "telnet");
				telnetdev.prepareForCommands("cisco", "pe", "routertools");
				output = telnetdev.applyCommands(command);
				telnetdev.disconnect();
			} catch (Exception e) {
				try {
					telnetdev.connect(ipAddress, 15, "ssh");
					telnetdev.prepareForCommands("cisco", "pe", "routertools");
					output = telnetdev.applyCommands(command);
					telnetdev.disconnect();
				} catch (Exception e2) {
					throw e2;
				}
			}
			if(!"".equals(output)) {
				// to do alg output
			}
		}
	}
}
