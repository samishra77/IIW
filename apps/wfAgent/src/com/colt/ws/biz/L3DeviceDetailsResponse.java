package com.colt.ws.biz;

public class L3DeviceDetailsResponse {

	private String responseID;
	private String wanIP;
	private String circuitID;
	private DeviceDetail deviceDetails;
	private ErrorResponse errorResponse;

	/**
	 * @return the circuitID
	 */
	public String getCircuitID() {
		return circuitID;
	}
	/**
	 * @param circuitID the circuitID to set
	 */
	public void setCircuitID(String circuitID) {
		this.circuitID = circuitID;
	}
	/**
	 * @return the deviceDetails
	 */
	public DeviceDetail getDeviceDetails() {
		return deviceDetails;
	}
	/**
	 * @param deviceDetails the deviceDetails to set
	 */
	public void setDeviceDetails(DeviceDetail deviceDetails) {
		this.deviceDetails = deviceDetails;
	}
	/**
	 * @return the responseID
	 */
	public String getResponseID() {
		return responseID;
	}
	/**
	 * @param responseID the responseID to set
	 */
	public void setResponseID(String responseID) {
		this.responseID = responseID;
	}
	/**
	 * @return the wanIP
	 */
	public String getWanIP() {
		return wanIP;
	}
	/**
	 * @param wanIP the wanIP to set
	 */
	public void setWanIP(String wanIP) {
		this.wanIP = wanIP;
	}
	/**
	 * @return the errorResponse
	 */
	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}
	/**
	 * @param errorResponse the errorResponse to set
	 */
	public void setErrorResponse(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}

}
