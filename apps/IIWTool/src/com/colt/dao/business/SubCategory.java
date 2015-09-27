package com.colt.dao.business;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class SubCategory extends Item{
	private static final long serialVersionUID = 1;
	@PrimaryKey
	private int subcategoryid;
	
	private String subcategoryname;
	private int categoryid;

	public SubCategory(int subcategoryid, String subcategoryname, int categoryid){
		this.subcategoryid = subcategoryid;
		this.subcategoryname = subcategoryname;
		this.categoryid = categoryid;
	}

	public int getSubcategoryid() {
		return subcategoryid;
	}

	public void setSubcategoryid(int subcategoryid) {
		this.subcategoryid = subcategoryid;
	}

	public String getSubcategoryname() {
		return subcategoryname;
	}

	public void setSubcategoryname(String subcategoryname) {
		this.subcategoryname = subcategoryname;
	}

	public int getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(int categoryid) {
		this.categoryid = categoryid;
	}

}
