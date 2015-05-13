package com.colt.util;

public class AgentConfig extends AbstractPropertiesAgentConfig {

	private static AgentConfig _instance = null;

	@Override
	protected String getConfigFile() {
		return "/conf/agentApplication.properties";
	}

	public static AgentConfig getDefaultInstance() {
		if (_instance == null) {
			_instance = new AgentConfig();
		}

		return _instance;
	}

}
