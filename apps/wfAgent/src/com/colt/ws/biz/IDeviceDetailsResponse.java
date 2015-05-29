package com.colt.ws.biz;

public interface IDeviceDetailsResponse {

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

}
