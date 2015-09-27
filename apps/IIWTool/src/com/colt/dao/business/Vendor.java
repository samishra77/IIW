package com.colt.dao.business;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class Vendor extends Item{
	private static final long serialVersionUID = 1;
	
	@PrimaryKey
	private int vendorid;
	
	private String vendorname;

	public Vendor(int vendorid, String vendorname){
		this.vendorid = vendorid;
		this.vendorname = vendorname;
	}

	public int getVendorid() {
		return vendorid;
	}

	public void setVendorid(int vendorid) {
		this.vendorid = vendorid;
	}

	public String getVendorname() {
		return vendorname;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

}
