package com.colt.ws.biz;

public class Circuit {

	private String circuitID;
	private String orderNumber;
	private String productType;

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
}
