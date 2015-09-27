package com.colt.util;

import java.util.Properties;


public class IiwConfig extends AbstractPropertiesConfig{

	Properties appProp = new Properties();
	Properties logProp = new Properties();
	private static IiwConfig _instance = null;

	@Override
	protected String getConfigFile() {
		return "/conf/application.properties";
	}

	public static IiwConfig getDefaultInstance() {
		if (_instance == null) {
			_instance = new IiwConfig();
		}

		return _instance;
	}

}
