package com.colt.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.colt.ws.biz.DeviceDetailsWSResponse;

@WebService
public interface IAdapterWrapper {

	@WebMethod
	public DeviceDetailsWSResponse fetch(@WebParam(name="vendor") String vendor, @WebParam(name="model") String model, @WebParam(name="circuitID") String circuitID, @WebParam(name="deviceIP") String deviceIP);

}
