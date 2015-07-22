package com.colt.util;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.connect.ConnectDevice;
import com.colt.ws.biz.ErrorResponse;
import com.colt.ws.biz.IDeviceDetailsResponse;

public class ConnectionFactory {

	private Log log = LogFactory.getLog(ConnectionFactory.class);

	public ConnectDevice getConnection(String deviceIP, String vendor, String os, IDeviceDetailsResponse deviceDetailsResponse) throws IOException {
		ConnectDevice connectDevice = null;
		try {
			connectDevice = new ConnectDevice();
			connectDevice.connect(deviceIP, 30, "ssh", vendor, os);
			return connectDevice;
		} catch (Exception e) {
			try {
				connectDevice.disconnect();
				connectDevice = new ConnectDevice();
				connectDevice.connect(deviceIP, 30, "telnet", vendor, os);
				return connectDevice;
			} catch (Exception e2) {
				connectDevice.disconnect();
				log.error(e,e);
				if(deviceDetailsResponse.getErrorResponse() == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setMessage(MessagesErrors.getDefaultInstance().getProperty("connection.failed"));
					errorResponse.setCode(ErrorResponse.CONNECTION_FAILED);
					deviceDetailsResponse.setErrorResponse(errorResponse);
				}
				deviceDetailsResponse.getErrorResponse().getFailedConn().add(deviceIP);
			}
		}
		return connectDevice;
	}
}
