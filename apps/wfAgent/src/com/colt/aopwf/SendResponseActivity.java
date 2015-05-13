package com.colt.aopwf;

import java.util.Map;

public class SendResponseActivity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		return new String[] {"RESPONSE"};
	}

}
