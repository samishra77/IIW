package com.colt.dao.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class Offering extends Item{
	private static final long serialVersionUID = 1;
	@PrimaryKey
	private int offeringid;
	
	private String offeringname;
	private int subcategoryid;
	private int vendorid;
	private String region;
	Map<String, String> spec_value = new HashMap<String, String>();

	public Offering(int offeringid, String offeringname, int subcategoryid, int vendorid, String region, Map<String,String> spec_value){
		this.offeringid = offeringid;
		this.offeringname = offeringname;
		this.subcategoryid = subcategoryid;
		this.vendorid = vendorid;
		this.region = region;
		this.spec_value = spec_value;
	}

	public int getOfferingid() {
		return offeringid;
	}

	public void setOfferingid(int offeringid) {
		this.offeringid = offeringid;
	}

	public String getOfferingname() {
		return offeringname;
	}

	public void setOfferingname(String offeringname) {
		this.offeringname = offeringname;
	}

	public int getSubcategoryid() {
		return subcategoryid;
	}

	public void setSubcategoryid(int subcategoryid) {
		this.subcategoryid = subcategoryid;
	}

	public int getVendorid() {
		return vendorid;
	}

	public void setVendorid(int vendorid) {
		this.vendorid = vendorid;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Map<String, String> getSpec_value() {
		return spec_value;
	}

	public void setSpec_value(Map<String, String> spec_value) {
		this.spec_value = spec_value;
	}

}
