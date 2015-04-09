package com.colt.ws.biz;

public class Circuit {

	private String circuitID;
	private String orderNumber;
	private String productType;
	private String customer;
	private String customerOCN;
	private String order;
	private String productName;
	private String status;
	private String category;
	private String pamStatus;
	private String performanceMonitoring;
	private String serviceMenu;
	private String inServiceSince;
	private String bandWidth;
	private String managementTeam;
	private String trunkGroup;
	private String aSideSite;
	private String zSideSite;

	/**
	 * @return the circuitID
	 */
	public String getCircuitID() {
		if(circuitID == null) {
			circuitID = "";
		}
		return circuitID;
	}

	/**
	 * @param circuitID the circuitID to set
	 */
	public void setCircuitID(String circuitID) {
		this.circuitID = circuitID;
	}

	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		if(orderNumber == null) {
			orderNumber = "";
		}
		return orderNumber;
	}

	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * @return the productType
	 */
	public String getProductType() {
		if(productType == null) {
			productType = "";
		}
		return productType;
	}

	/**
	 * @param productType the productType to set
	 */
	public void setProductType(String productType) {
		this.productType = productType;
	}

	@Override
	public String toString() {
		return "Model [circuitID:" + getCircuitID() + ", orderNumber:" + getOrderNumber() + ", productType:" + getProductType() + "]";
	}

	/**
	 * @return the customer
	 */
	public String getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
	}

	/**
	 * @return the customerOCN
	 */
	public String getCustomerOCN() {
		return customerOCN;
	}

	/**
	 * @param customerOCN the customerOCN to set
	 */
	public void setCustomerOCN(String customerOCN) {
		this.customerOCN = customerOCN;
	}

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
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
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the pamStatus
	 */
	public String getPamStatus() {
		return pamStatus;
	}

	/**
	 * @param pamStatus the pamStatus to set
	 */
	public void setPamStatus(String pamStatus) {
		this.pamStatus = pamStatus;
	}

	/**
	 * @return the performanceMonitoring
	 */
	public String getPerformanceMonitoring() {
		return performanceMonitoring;
	}

	/**
	 * @param performanceMonitoring the performanceMonitoring to set
	 */
	public void setPerformanceMonitoring(String performanceMonitoring) {
		this.performanceMonitoring = performanceMonitoring;
	}

	/**
	 * @return the serviceMenu
	 */
	public String getServiceMenu() {
		return serviceMenu;
	}

	/**
	 * @param serviceMenu the serviceMenu to set
	 */
	public void setServiceMenu(String serviceMenu) {
		this.serviceMenu = serviceMenu;
	}

	/**
	 * @return the inServiceSince
	 */
	public String getInServiceSince() {
		return inServiceSince;
	}

	/**
	 * @param inServiceSince the inServiceSince to set
	 */
	public void setInServiceSince(String inServiceSince) {
		this.inServiceSince = inServiceSince;
	}

	/**
	 * @return the bandWidth
	 */
	public String getBandWidth() {
		return bandWidth;
	}

	/**
	 * @param bandWidth the bandWidth to set
	 */
	public void setBandWidth(String bandWidth) {
		this.bandWidth = bandWidth;
	}

	/**
	 * @return the managementTeam
	 */
	public String getManagementTeam() {
		return managementTeam;
	}

	/**
	 * @param managementTeam the managementTeam to set
	 */
	public void setManagementTeam(String managementTeam) {
		this.managementTeam = managementTeam;
	}

	/**
	 * @return the trunkGroup
	 */
	public String getTrunkGroup() {
		return trunkGroup;
	}

	/**
	 * @param trunkGroup the trunkGroup to set
	 */
	public void setTrunkGroup(String trunkGroup) {
		this.trunkGroup = trunkGroup;
	}

	/**
	 * @return the aSideSite
	 */
	public String getaSideSite() {
		return aSideSite;
	}

	/**
	 * @param aSideSite the aSideSite to set
	 */
	public void setaSideSite(String aSideSite) {
		this.aSideSite = aSideSite;
	}

	/**
	 * @return the zSideSite
	 */
	public String getzSideSite() {
		return zSideSite;
	}

	/**
	 * @param zSideSite the zSideSite to set
	 */
	public void setzSideSite(String zSideSite) {
		this.zSideSite = zSideSite;
	}
}
