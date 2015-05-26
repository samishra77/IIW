package com.colt.ws.biz;

public abstract class DeviceDetailsWSResponse {

	/**
	 * @return the deviceIP
	 */
	public abstract String getDeviceIP();
	/**
	 * @param deviceIP the deviceIP to set
	 */
	public abstract void setDeviceIP(String deviceIP);

	/**
	 * @return the circuitID
	 */
	public abstract String getCircuitID();
	/**
	 * @param circuitID the circuitID to set
	 */
	public abstract void setCircuitID(String circuitID);
	/**
	 * @return the deviceDetails
	 */
	public abstract DeviceDetailWS getDeviceDetails();
	/**
	 * @param deviceDetails the deviceDetails to set
	 */
	public abstract void setDeviceDetails(DeviceDetailWS deviceDetails);
	/**
	 * @return the responseID
	 */
	public abstract String getResponseID();
	/**
	 * @param responseID the responseID to set
	 */
	public abstract void setResponseID(String responseID) ;

	/**
	 * @return the errorResponse
	 */
	public abstract ErrorWSResponse getErrorResponse();
	/**
	 * @param errorResponse the errorResponse to set
	 */
	public abstract void setErrorResponse(ErrorWSResponse errorResponse);

	public abstract void setErrorResponse(ErrorResponse errorResponse);

}
