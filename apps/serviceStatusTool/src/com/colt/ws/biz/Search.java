package com.colt.ws.biz;

import com.colt.util.Util;

public class Search {

	private String order;
	private String service;
	private String customer;
	private String address;
	private String city;
	private String address2;
	private String city2;

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
		if(order != null) {
			order = order.trim();
		}
		this.order = order;
	}
	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		if(service != null) {
			service = service.trim();
		}
		this.service = service;
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
		if(customer != null) {
			customer = customer.trim();
		}
		this.customer = customer;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		if(address != null) {
			address = address.trim();
		}
		this.address = address;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		if(city != null) {
			city = city.trim();
		}
		this.city = city;
	}
	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}
	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		if(address2 != null) {
			address2 = address2.trim();
		}
		this.address2 = address2;
	}
	/**
	 * @return the city2
	 */
	public String getCity2() {
		return city2;
	}
	/**
	 * @param city2 the city2 to set
	 */
	public void setCity2(String city2) {
		if(city2 != null) {
			city2 = city2.trim();
		}
		this.city2 = city2;
	}

	@Override
	public String toString() {
		return Util.serializeObj(this);
	}

}
