package com.colt.aopwf;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.colt.util.AgentUtil;
import com.colt.util.AgentConfig;
import com.colt.ws.biz.DeviceDetailsRequest;

public class PingActivity implements IWorkflowProcessActivity {

	public String[] process(Map<String,Object> input) {
		String[] resp = null;
		if(pingTest(input)) {
			resp = new String[] {"PING_SUCCESS"};
		} else {
			resp = new String[] {"PING_FAIL"};
		}
		return resp;
	}

	private boolean pingTest(Map<String,Object> input) {
		boolean test = false;
		if(input.containsKey("deviceDetails")) {
			DeviceDetailsRequest deviceDetail = (DeviceDetailsRequest) input.get("deviceDetails");
			if(deviceDetail != null && deviceDetail.getIp() != null && !"".equals(deviceDetail.getIp())) {
				String ip = deviceDetail.getIp();
				Map<String, Boolean> ipStatus = ping(ip);
				if(ipStatus != null && !ipStatus.isEmpty()) {
					if(ipStatus.containsKey(ip) && ipStatus.get(ip)) {
						deviceDetail.setStatus(AgentUtil.UP);
						test = true;
					} else {
						deviceDetail.setStatus(AgentUtil.DOWN);
						test = false;
					}
				}
			}
		}
		return test;
	}

	/**
	 * 
	 * @param target
	 * @return map of result, key is ip and value is a booelan where TRUE device is alive and FALSE device is unreachable
	 */
	private Map<String, Boolean> ping(String target) {
		Map<String, Boolean> ipStatus = new HashMap<String, Boolean>();
		if (target != null && !"".equals(target)) {
			ipStatus.put(target, isReachable(target));
		}
		return ipStatus;
	}

	private boolean isReachable(String hostname) {
		boolean status = false;
		try {
			InetAddress addr = InetAddress.getByName(hostname);
			status = addr.isReachable(3000);
		} catch (Exception e) {
		}
		return status;
	}
}
