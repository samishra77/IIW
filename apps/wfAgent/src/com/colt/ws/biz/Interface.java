package com.colt.ws.biz;

public class Interface {

	private String name;
	private String ipaddress;
	private String lastChgTime;
	private String status;
	private String opStatus;
	private String adminStatus;
	private String portStatus;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}
	/**
	 * @param ipaddress the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	/**
	 * @return the lastChgTime
	 */
	public String getLastChgTime() {
		return lastChgTime;
	}
	/**
	 * @param lastChgTime the lastChgTime to set
	 */
	public void setLastChgTime(String lastChgTime) {
		this.lastChgTime = lastChgTime;
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
	 * @return the opStatus
	 */
	public String getOpStatus() {
		return opStatus;
	}
	/**
	 * @param opStatus the opStatus to set
	 */
	public void setOpStatus(String opStatus) {
		this.opStatus = opStatus;
	}
	/**
	 * @return the adminStatus
	 */
	public String getAdminStatus() {
		return adminStatus;
	}
	/**
	 * @param adminStatus the adminStatus to set
	 */
	public void setAdminStatus(String adminStatus) {
		this.adminStatus = adminStatus;
	}
	/**
	 * @return the portStatus
	 */
	public String getPortStatus() {
		return portStatus;
	}
	/**
	 * @param portStatus the portStatus to set
	 */
	public void setPortStatus(String portStatus) {
		this.portStatus = portStatus;
	}
}
