package com.colt.ws.biz;

public class SiebelCallRequest {

	private String searchMethod;
	private String searchType;
	private String coltReference;
	private String custReference;
	private String circuitServiceID;
	private String ticketStatus;
	private String ocn;
	private String bcn;
	
	private String ticketType;
	private String earliestStartDate;
	private String latestStartDate;
	private String maxRowsReq;
	private String cityTown;
	private String isPartner;
	private Partner partner;

	public String getSearchMethod() {
		return searchMethod;
	}
	public void setSearchMethod(String searchMethod) {
		this.searchMethod = searchMethod;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getColtReference() {
		return coltReference;
	}
	public void setColtReference(String coltReference) {
		this.coltReference = coltReference;
	}
	public String getCustReference() {
		return custReference;
	}
	public void setCustReference(String custReference) {
		this.custReference = custReference;
	}
	public String getCircuitServiceID() {
		return circuitServiceID;
	}
	public void setCircuitServiceID(String circuitServiceID) {
		this.circuitServiceID = circuitServiceID;
	}
	public String getTicketStatus() {
		return ticketStatus;
	}
	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}
	public String getOcn() {
		return ocn;
	}
	public void setOcn(String ocn) {
		this.ocn = ocn;
	}
	public String getBcn() {
		return bcn;
	}
	public void setBcn(String bcn) {
		this.bcn = bcn;
	}
	public String getTicketType() {
		return ticketType;
	}
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	public String getEarliestStartDate() {
		return earliestStartDate;
	}
	public void setEarliestStartDate(String earliestStartDate) {
		this.earliestStartDate = earliestStartDate;
	}
	public String getLatestStartDate() {
		return latestStartDate;
	}
	public void setLatestStartDate(String latestStartDate) {
		this.latestStartDate = latestStartDate;
	}
	public String getMaxRowsReq() {
		return maxRowsReq;
	}
	public void setMaxRowsReq(String maxRowsReq) {
		this.maxRowsReq = maxRowsReq;
	}
	public String getCityTown() {
		return cityTown;
	}
	public void setCityTown(String cityTown) {
		this.cityTown = cityTown;
	}
	public String getIsPartner() {
		return isPartner;
	}
	public void setIsPartner(String isPartner) {
		this.isPartner = isPartner;
	}
	public Partner getPartner() {
		return partner;
	}
	public void setPartner(Partner partner) {
		this.partner = partner;
	}

}
