package com.colt.util;

public class DeviceCommand extends AbstractPropertiesAgentConfig {

	private static DeviceCommand _instance = null;

	@Override
	protected String getConfigFile() {
		return "/conf/deviceCommand.properties";
	}

	public static DeviceCommand getDefaultInstance() {
		if (_instance == null) {
			_instance = new DeviceCommand();
		}

		return _instance;
	}

}
