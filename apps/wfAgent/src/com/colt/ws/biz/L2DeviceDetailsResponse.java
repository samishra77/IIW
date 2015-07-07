package com.colt.ws.biz;

public class L2DeviceDetailsResponse implements IDeviceDetailsResponse {

	private String responseID;
	private String deviceIP;
	private String circuitID;
	private DeviceDetail deviceDetails;
	private ErrorResponse errorResponse;

	/**
	 * @return the deviceIP
	 */
	public String getDeviceIP() {
		return deviceIP;
	}
	/**
	 * @param deviceIP the deviceIP to set
	 */
	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}
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
	
	@Override
	public String getOs() {
		return null;
	}
	@Override
	public void setOs(String os) {
		
	}
	@Override
	public String getDeviceName() {
		return null;
	}
	@Override
	public void setDeviceName(String deviceName) {
	}
	@Override
	public String getAssociatedDeviceIp() {
		return null;
	}
	@Override
	public void setAssociatedDeviceIp(String associatedDeviceIp) {
	}
}
