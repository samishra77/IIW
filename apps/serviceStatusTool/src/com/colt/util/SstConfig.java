package com.colt.util;

import java.util.Properties;


public class SstConfig extends AbstractPropertiesConfig{

	Properties appProp = new Properties();
	Properties logProp = new Properties();
	private static SstConfig _instance = null;

	@Override
	protected String getConfigFile() {
		return "/conf/application.properties";
	}

	public static SstConfig getDefaultInstance() {
		if (_instance == null) {
			_instance = new SstConfig();
		}

		return _instance;
	}

}
