package com.colt.ws.biz;

import java.util.List;

public class DeviceDetailWS {

	private String status;
	private String time;
	private Interface[] interfaces;

	
	/**
	 * @return the interfaces
	 */
	public Interface[] getInterfaces() {
		return interfaces;
	}
	/**
	 * @param interfaces the interfaces to set
	 */
	public void setInterfaces(Interface[] interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * @param interfaces the interfaces to set
	 */
	public void setInterfaces(List<Interface> interfaces) {
		if(interfaces != null) {
			this.interfaces = interfaces.toArray(new Interface[interfaces.size()]);
		} else {
			this.interfaces = null;
		}
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
