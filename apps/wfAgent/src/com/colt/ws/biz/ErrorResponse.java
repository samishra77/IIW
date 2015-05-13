package com.colt.ws.biz;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

	private String code;
	private String message;
	private List<String> failedPings;
	private List<String> failedSnmp;
	private List<String> failedConn;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the failedPings
	 */
	public List<String> getFailedPings() {
		if(failedPings == null) {
			failedPings = new ArrayList<String>();
		}
		return failedPings;
	}
	/**
	 * @return the failedSnmp
	 */
	public List<String> getFailedSnmp() {
		if(failedSnmp == null) {
			failedSnmp = new ArrayList<String>();
		}
		return failedSnmp;
	}
	/**
	 * @return the failedConn
	 */
	public List<String> getFailedConn() {
		if(failedConn == null) {
			failedConn = new ArrayList<String>();
		}
		return failedConn;
	}
}