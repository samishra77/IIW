package com.colt.ws.biz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.colt.util.SstConfig;

public class Response {

	private String status;
	private String errorMsg;
	private String errorCode;
	private Object result;
	private String[] supportedLanlinkVendorAList = null;
	private String[] supportedLanlinkVendorZList = null;
	private String[] supportedIpServiceVendorAList = null;
	private String[] supportedIpServiceVendorZList = null;
	

	public final static String CODE_EMPTY = "0";
	public final static String CODE_MAXRESULT = "1";
	public final static String CODE_UNKNOWN = "2";
	public final static String SUCCESS = "success";
	public final static String FAIL = "fail";

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	public String[] getSupportedLanlinkVendorAList() throws IOException {
		if(supportedLanlinkVendorAList == null) {
			supportedLanlinkVendorAList = SstConfig.getDefaultInstance().getProperty("lanlinkSupportedVendorsA").split(",");
		}
		return supportedLanlinkVendorAList;
	}

	public String[] getSupportedLanlinkVendorZList() throws IOException {
		if(supportedLanlinkVendorZList == null) {
			supportedLanlinkVendorZList = SstConfig.getDefaultInstance().getProperty("lanlinkSupportedVendorsZ").split(",");
		}
		return supportedLanlinkVendorZList;
	}

	public String[] getSupportedIpServiceVendorAList() throws IOException {
		if(supportedIpServiceVendorAList == null) {
			supportedIpServiceVendorAList = SstConfig.getDefaultInstance().getProperty("ipServiceSupportedVendorsA").split(",");
		}
		return supportedIpServiceVendorAList;
	}

	public String[] getSupportedIpServiceVendorZList() throws IOException {
		if(supportedIpServiceVendorZList == null) {
			supportedIpServiceVendorZList = SstConfig.getDefaultInstance().getProperty("ipServiceSupportedVendorsZ").split(",");
		}
		return supportedIpServiceVendorZList;
	}
}
