package com.colt.ws.biz;

import java.util.ArrayList;
import java.util.List;

public class Circuit {

	private String circPathInstID;
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
	private String revisionNumber;
	private String relatedOrderNumber;
	private String osmOrderNO;
	private List<Ticket> tickets;
	private String resilienceType;
	private String serviceId;
	private List<String> relatedOrderNumberList;

	/**
	 * @return the relatedOrderNumberList
	 */
	public List<String> getRelatedOrderNumberList() {
		if(relatedOrderNumberList == null) {
			relatedOrderNumberList = new ArrayList<String>();
		}
		return relatedOrderNumberList;
	}

	/**
	 * @return the osmOrderNO
	 */
	public String getOsmOrderNO() {
		return osmOrderNO;
	}

	/**
	 * @param osmOrderNO the osmOrderNO to set
	 */
	public void setOsmOrderNO(String osmOrderNO) {
		this.osmOrderNO = osmOrderNO;
	}

	/**
	 * @return the relatedOrderNumber
	 */
	public String getRelatedOrderNumber() {
		return relatedOrderNumber;
	}

	/**
	 * @param relatedOrderNumber the relatedOrderNumber to set
	 */
	public void setRelatedOrderNumber(String relatedOrderNumber) {
		this.relatedOrderNumber = relatedOrderNumber;
	}

	/**
	 * @return the revisionNumber
	 */
	public String getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * @param revisionNumber the revisionNumber to set
	 */
	public void setRevisionNumber(String revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	/**
	 * @return the circPathInstID
	 */
	public String getCircPathInstID() {
		return circPathInstID;
	}

	/**
	 * @param circPathInstID the circPathInstID to set
	 */
	public void setCircPathInstID(String circPathInstID) {
		this.circPathInstID = circPathInstID;
	}

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
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (circPathInstID != null && !"".equals(circPathInstID)) {
			sb.append("circPathInstID:" + circPathInstID + " ");
		}
		if (circuitID != null && !"".equals(circuitID)) {
			sb.append("service:" + circuitID + " ");
		}
		if (orderNumber != null && !"".equals(orderNumber)) {
			sb.append("orderNumber:" + " ");
		}
		if (productType != null && !"".equals(productType)) {
			sb.append("productType:" + productType + " ");
		}
		if (customer != null && !"".equals(customer)) {
			sb.append("customer:" + customer + " ");
		}
		if (customerOCN != null && !"".equals(customerOCN)) {
			sb.append("customerOCN:" + customerOCN + " ");
		}
		if (order != null && !"".equals(order)) {
			sb.append("order:" + order + " ");
		}
		if (productName != null && !"".equals(productName)) {
			sb.append("productName:" + productName + " ");
		}
		if (status != null && !"".equals(status)) {
			sb.append("status:" + status + " ");
		}
		if (category != null && !"".equals(category)) {
			sb.append("category:" + category + " ");
		}
		if (pamStatus != null && !"".equals(pamStatus)) {
			sb.append("pamStatus:" + pamStatus + " ");
		}
		if (performanceMonitoring != null && !"".equals(performanceMonitoring)) {
			sb.append("performanceMonitoring:" + performanceMonitoring + " ");
		}
		if (serviceMenu != null && !"".equals(serviceMenu)) {
			sb.append("serviceMenu:" + serviceMenu + " ");
		}
		if (inServiceSince != null && !"".equals(inServiceSince)) {
			sb.append("inServiceSince:" + inServiceSince + " ");
		}
		if (bandWidth != null && !"".equals(bandWidth)) {
			sb.append("bandWidth:" + bandWidth + " ");
		}
		if (managementTeam != null && !"".equals(managementTeam)) {
			sb.append("managementTeam:" + managementTeam + " ");
		}
		if (trunkGroup != null && !"".equals(trunkGroup)) {
			sb.append("trunkGroup:" + trunkGroup + " ");
		}
		if (aSideSite != null && !"".equals(aSideSite)) {
			sb.append("aSideSite:" + aSideSite + " ");
		}
		if (zSideSite != null  && !"".equals(zSideSite)) {
			sb.append("zSideSite:" + zSideSite + " ");
		}
		if (revisionNumber != null && !"".equals(revisionNumber)) {
			sb.append("revisionNumber:" + revisionNumber + " ");
		}
		if (relatedOrderNumber != null && !"".equals(relatedOrderNumber)) {
			sb.append("relatedOrderNumber:" + relatedOrderNumber + " ");
		}
		if (osmOrderNO != null && !"".equals(osmOrderNO)) {
			sb.append("osmOrderNO:" + osmOrderNO + " ");
		}
		if (tickets != null && tickets.size() >0) {
			sb.append("tickets: ");
			for (Ticket tick : tickets) {
				sb.append(tick.toString() + " ");
			}
		}
		if (resilienceType != null && !"".equals(resilienceType)) {
			sb.append("resilienceType:" + resilienceType + " ");
		}
		if (serviceId != null && !"".equals(serviceId)) {
			sb.append("serviceId:" + serviceId + " ");
		}
		sb.append("]");
		return sb.toString();
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

	/**
	 * @return the tickets
	 */
	public List<Ticket> getTickets() {
		return tickets;
	}

	/**
	 * @param tickets the tickets to set
	 */
	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}

	/**
	 * @return the resilienceType
	 */
	public String getResilienceType() {
		return resilienceType;
	}

	/**
	 * @param resilienceType the resilienceType to set
	 */
	public void setResilienceType(String resilienceType) {
		this.resilienceType = resilienceType;
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
}
