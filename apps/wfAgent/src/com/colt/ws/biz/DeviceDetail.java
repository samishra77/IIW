package com.colt.ws.biz;

import java.util.ArrayList;
import java.util.List;

public class DeviceDetail {

	private String status;
	private String time;
	private List<Interface> interfaces;
	/**
	 * @return the interfaceList
	 */
	public List<Interface> getInterfaces() {
		if(interfaces == null) {
			interfaces = new ArrayList<Interface>();
		}
		return interfaces;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
}
