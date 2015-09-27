package com.colt.dao.business;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class Questionbank extends Item{
	private static final long serialVersionUID = 1;
	@PrimaryKey
	private int questionid;
	
	private String createdby;
	private String createddate;
	private String descr;
	private int subcategoryid;

	public Questionbank(int questionid, String createdby, String createddate, String descr, int subcategoryid){
		this.questionid = questionid;
		this.createdby = createdby;
		this.createddate = createddate;
		this.descr = descr;
		this.subcategoryid = subcategoryid;
	}

	public int getQuestionid() {
		return questionid;
	}

	public void setQuestionid(int questionid) {
		this.questionid = questionid;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getCreateddate() {
		return createddate;
	}

	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public int getSubcategoryid() {
		return subcategoryid;
	}

	public void setSubcategoryid(int subcategoryid) {
		this.subcategoryid = subcategoryid;
	}

}
