package com.colt.dao.business;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class Userold {
	@PrimaryKey
	private int user_id;

	private String fname;
	private String lname;

	public Userold(int user_id, String fname, String lname) {
		this.user_id = user_id;
		this.fname = fname;
		this.lname = lname;
	}

	public int getUser_id() {
		return user_id;
	}

	public String getFname() {
		return fname;
	}

	public String getLname() {
		return lname;
	}

	@Override
	public String toString() {
		return "User [id=" + user_id + ", fname=" + fname + ", lname=" + lname
				+ "]";
	}
}
