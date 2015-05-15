package com.colt.ws.biz;

public interface IDeviceDetailsResponse {

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
	 * @return the wanIP
	 */
	public String getWanIP();
	/**
	 * @param wanIP the wanIP to set
	 */
	public void setWanIP(String wanIP);
	/**
	 * @return the errorResponse
	 */
	public ErrorResponse getErrorResponse();
	/**
	 * @param errorResponse the errorResponse to set
	 */
	public void setErrorResponse(ErrorResponse errorResponse);

}
