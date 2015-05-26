package com.colt.ws.biz;

import java.util.ArrayList;
import java.util.List;

public class ErrorWSResponse {

	private int code;
	private String message;
	private String[] failedPings;
	private String[] failedSnmp;
	private String[] failedConn;

	public static final int CODE_UNKNOWN = 0;
	public static final int CONNECTION_FAILED  = 1;
	

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
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
	 * @return the failedConn
	 */
	public String[] getFailedConn() {
		return failedConn;
	}
	/**
	 * @param failedConn the failedConn to set
	 */
	public void setFailedConn(String[] failedConn) {
		this.failedConn = failedConn;
	}

	public void setFailedConn(List<String> failedConn) {
		if(failedConn != null) {
			this.failedConn = failedConn.toArray(new String[failedConn.size()]);
		} else {
			this.failedConn = null;
		}
	}
	/**
	 * @return the failedSnmp
	 */
	public String[] getFailedSnmp() {
		return failedSnmp;
	}
	/**
	 * @param failedSnmp the failedSnmp to set
	 */
	public void setFailedSnmp(String[] failedSnmp) {
		this.failedSnmp = failedSnmp;
	}

	public void setFailedSnmp(List<String> failedSnmp) {
		if(failedSnmp != null) {
			this.failedSnmp = failedSnmp.toArray(new String[failedSnmp.size()]);
		} else {
			this.failedSnmp = null;
		}
	}
	/**
	 * @return the failedPings
	 */
	public String[] getFailedPings() {
		return failedPings;
	}
	/**
	 * @param failedPings the failedPings to set
	 */
	public void setFailedPings(List<String> failedPings) {
		if(failedPings != null) {
			this.failedPings = failedPings.toArray(new String[failedPings.size()]);
		} else {
			this.failedPings = null;
		}
	}
}
