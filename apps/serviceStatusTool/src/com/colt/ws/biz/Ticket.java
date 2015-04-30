package com.colt.ws.biz;

public class Ticket {

	private String 	coltReference;
	private String customerReference;
	private String status;
	private String ticketDescription;
	private String opened;
	private String restored;
	private String type;
	private String reportedBy;
	private String priority;
	public String getColtReference() {
		return coltReference;
	}
	public void setColtReference(String coltReference) {
		this.coltReference = coltReference;
	}
	public String getCustomerReference() {
		return customerReference;
	}
	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTicketDescription() {
		return ticketDescription;
	}
	public void setTicketDescription(String ticketDescription) {
		this.ticketDescription = ticketDescription;
	}
	public String getOpened() {
		return opened;
	}
	public void setOpened(String opened) {
		this.opened = opened;
	}
	public String getRestored() {
		return restored;
	}
	public void setRestored(String restored) {
		this.restored = restored;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getReportedBy() {
		return reportedBy;
	}
	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (coltReference != null && !"".equals(coltReference)) {
			sb.append("coltReference" + coltReference + " ");
		}
		if (status != null && !"".equals(status)) {
			sb.append("status" + status + " ");
		}
		if (ticketDescription != null && !"".equals(ticketDescription)) {
			sb.append("ticketDescription" + ticketDescription + " ");
		}
		if (opened != null && !"".equals(opened)) {
			sb.append("opened" + opened + " ");
		}
		if (restored != null && !"".equals(restored)) {
			sb.append("restored" + restored + " ");
		}
		if (type != null && !"".equals(type)) {
			sb.append("type" + type + " ");
		}
		if (reportedBy != null && !"".equals(reportedBy)) {
			sb.append("reportedBy" + reportedBy + " ");
		}
		if (priority != null && !"".equals(priority)) {
			sb.append("priority" + priority + " ");
		}
		sb.append("]");
		return sb.toString();
	}
}
