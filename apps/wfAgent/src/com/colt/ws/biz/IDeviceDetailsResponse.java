package com.colt.ws.biz;

public interface IDeviceDetailsResponse {

	/**
	 * @return the domainName
	 */
	public String getDomainName();
	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName);
	/**
	 * @return the vendor
	 */
	public String getVendor();
	/**
	 * @param vendor the vendor to set
	 */
	public void setVendor(String vendor);
	/**
	 * @return the model
	 */
	public String getModel();
	/**
	 * @param model the model to set
	 */
	public void setModel(String model);

	/**
	 * @return the associatedDeviceIp
	 */
	public String getAssociatedDeviceIp();
	/**
	 * @param associatedDeviceIp the associatedDeviceIp to set
	 */
	public void setAssociatedDeviceIp(String associatedDeviceIp);
	/**
	 * @return the deviceIP
	 */
	public String getDeviceIP();
	/**
	 * @param deviceIP the deviceIP to set
	 */
	public void setDeviceIP(String deviceIP);

	/**
	 * @return the circuitID
	 */
	public String getCircuitID();
	/**
	 * @param circuitID the circuitID to set
	 */
	public void setCircuitID(String circuitID);
	/**
	 * @return the deviceDetails
	 */
	public DeviceDetail getDeviceDetails();
	/**
	 * @param deviceDetails the deviceDetails to set
	 */
	public void setDeviceDetails(DeviceDetail deviceDetails);
	/**
	 * @return the responseID
	 */
	public String getResponseID();
	/**
	 * @param responseID the responseID to set
	 */
	public void setResponseID(String responseID) ;
	/**
	 * @return the errorResponse
	 */
	public ErrorResponse getErrorResponse();
	/**
	 * @param errorResponse the errorResponse to set
	 */
	public void setErrorResponse(ErrorResponse errorResponse);
	/**
	 * @return the os
	 */
	public String getOs();
	/**
	 * @param os the os to set
	 */
	public void setOs(String os);
	/**
	 * @return the deviceName
	 */
	public String getDeviceName();
	/**
	 * @param os the deviceName to set
	 */
	public void setDeviceName(String deviceName);

}