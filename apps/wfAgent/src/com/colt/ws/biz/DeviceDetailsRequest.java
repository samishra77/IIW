package com.colt.ws.biz;

public class DeviceDetailsRequest {

	public static final String TYPE_CPE = "CPE";
	public static final String TYPE_PE = "PE";
	public static final String SERVICE_TYPE_LAN_LINK = "LANLink";

	private String requestID;
	private String seibelUserID;
	private String name;
	private DeviceType deviceType;
	private String circuitID;
	private String serviceId;
	private String ip;
	private String type;
	private String associatedDevice;
	private String associatedDeviceIp;
	private String serviceType;
	private String portName;
	private String productName;

	
	/**
	 * @return the associatedDeviceIp
	 */
	public String getAssociatedDeviceIp() {
		return associatedDeviceIp;
	}
	/**
	 * @param associatedDeviceIp the associatedDeviceIp to set
	 */
	public void setAssociatedDeviceIp(String associatedDeviceIp) {
		this.associatedDeviceIp = associatedDeviceIp;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the requestID
	 */
	public String getRequestID() {
		return requestID;
	}
	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	/**
	 * @return the seibelUserID
	 */
	public String getSeibelUserID() {
		return seibelUserID;
	}
	/**
	 * @param seibelUserID the seibelUserID to set
	 */
	public void setSeibelUserID(String seibelUserID) {
		this.seibelUserID = seibelUserID;
	}
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
	 * @return the deviceType
	 */
	public DeviceType getDeviceType() {
		return deviceType;
	}
	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
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
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAssociatedDevice() {
		return associatedDevice;
	}
	public void setAssociatedDevice(String associatedDevice) {
		this.associatedDevice = associatedDevice;
	}
	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}
	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}
	/**
	 * @param portName the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
}
