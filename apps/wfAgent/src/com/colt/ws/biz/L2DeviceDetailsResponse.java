package com.colt.ws.biz;

public class L2DeviceDetailsResponse implements IDeviceDetailsResponse {

	private String responseID;
	private String deviceIP;
	private String circuitID;
	private DeviceDetail deviceDetails;
	private ErrorResponse errorResponse;
	private String vendor;
	private String model;
	private String domainName;

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}
	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	/**
	 * @return the vendor
	 */
	public String getVendor() {
		return vendor;
	}
	/**
	 * @param vendor the vendor to set
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
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
