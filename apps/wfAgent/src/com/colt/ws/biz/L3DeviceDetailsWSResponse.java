package com.colt.ws.biz;

public class L3DeviceDetailsWSResponse extends DeviceDetailsWSResponse {

	private String responseID;
	private String deviceIP;
	private String circuitID;
	private DeviceDetailWS deviceDetails;
	private ErrorWSResponse errorResponse;

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
	public DeviceDetailWS getDeviceDetails() {
		return deviceDetails;
	}
	/**
	 * @param deviceDetails the deviceDetails to set
	 */
	public void setDeviceDetails(DeviceDetailWS deviceDetails) {
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
	public ErrorWSResponse getErrorResponse() {
		return errorResponse;
	}
	/**
	 * @param errorResponse the errorResponse to set
	 */
	public void setErrorResponse(ErrorWSResponse errorResponse) {
		this.errorResponse = errorResponse;
	}

	public void setErrorResponse(ErrorResponse errorResponse) {
		if(errorResponse != null) {
			if(errorResponse instanceof ErrorResponse) {
				if(this.errorResponse == null) {
					this.errorResponse = new ErrorWSResponse();
				}
				this.errorResponse.setCode(errorResponse.getCode());
				this.errorResponse.setMessage(errorResponse.getMessage());
				this.errorResponse.setFailedConn(errorResponse.getFailedConn());
				this.errorResponse.setFailedSnmp(errorResponse.getFailedSnmp());
				this.errorResponse.setFailedPings(errorResponse.getFailedPings());
			}
		} else {
			this.errorResponse = null;
		}
	}
}
