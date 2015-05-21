package com.colt.util;

public class MessagesErrors extends AbstractPropertiesAgentConfig {

	private static MessagesErrors _instance = null;

	@Override
	protected String getConfigFile() {
		return "/conf/messagesErrors.properties";
	}

	public static MessagesErrors getDefaultInstance() {
		if (_instance == null) {
			_instance = new MessagesErrors();
		}

		return _instance;
	}

}
