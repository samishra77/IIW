package com.colt.aopwf;

import java.util.Map;

public class EMSAPIActivity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		return emsapiFetch(input);
	}

	private String[] emsapiFetch(Map<String,Object> input) {
		String[] resp = null;
		resp = new String[] {"SENDRESPONSE"};
		return resp;
	}
}
