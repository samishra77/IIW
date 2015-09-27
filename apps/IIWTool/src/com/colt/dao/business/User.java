package com.colt.dao.business;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class User extends Item{
	private static final long serialVersionUID = 1;
	@PrimaryKey
	private int userid;

	private String username;
	private String address;
	private String city;
	private String country;
	private String dateofbirth;
	private String email;
	private String firstname;
	private String lastname;
	private String gender;
	private String maritalstatus;
	private String mobile;
	private String password;
	private String region;
	private String zipcode;
	private int offeringid;

	public User(int userid, String username, String address, String city, String country, String dateofbirth, String email, String firstname, String lastname, String gender, String maritalstatus, String mobile, String password, String region, String zipcode, int offeringid) {
		this.userid = userid;
		this.username = username;
		this.address = address;
		this.city = city;
		this.country = country;
		this.dateofbirth = dateofbirth;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.gender = gender;
		this.maritalstatus = maritalstatus;
		this.mobile = mobile;
		this.password = password;
		this.region = region;
		this.zipcode = zipcode;
		this.offeringid = offeringid;
	}
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getDateofbirth() {
		return dateofbirth;
	}
	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMaritalstatus() {
		return maritalstatus;
	}
	public void setMaritalstatus(String maritalstatus) {
		this.maritalstatus = maritalstatus;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public int getOfferingid() {
		return offeringid;
	}

	public void setOfferingid(int offeringid) {
		this.offeringid = offeringid;
	}

}
