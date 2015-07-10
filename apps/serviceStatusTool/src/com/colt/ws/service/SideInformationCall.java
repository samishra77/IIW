package com.colt.ws.service;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import com.colt.util.SstConfig;
import com.colt.util.Util;
import com.colt.ws.biz.ASideInformation;
import com.colt.ws.biz.Circuit;
import com.colt.ws.biz.ProductType;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.SideInformation;
import com.colt.ws.biz.ZSideInformation;

public class SideInformationCall {

	protected Properties messages;

	public SideInformationCall(Properties messages) {
		this.messages = messages;
	}

	public String sideInformationCallProcess(Circuit circuit) throws Exception {
		String result = null;
		String url = SstConfig.getDefaultInstance().getProperty("ws.pathViewer.url");
		String serviceType = circuit.getProductType();
		String amnCheckerResponse = null;
		String subcategory = null;
		if (serviceType != null && serviceType.toUpperCase().contains("LANLINK")) {
			if (circuit.getCircuitID() != null && !circuit.getCircuitID().trim().equals("")) {
				Util util = new Util();
				amnCheckerResponse = getInfoFromAmnChecker(circuit);
				subcategory = util.getValue(amnCheckerResponse, "<subcategory>", "</subcategory>");
			}
		}
		URL u = new URL(url);
		URLConnection uc = u.openConnection();
		HttpURLConnection connection = (HttpURLConnection) uc;
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.connect();

		OutputStream out = connection.getOutputStream();
		Writer wout = new BufferedWriter(new OutputStreamWriter(out));
		wout.write("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ws='http://ws.pathviewer.colt.com/'>");
		wout.write("<soapenv:Header/>");
		wout.write("<soapenv:Body>");
		wout.write("<ws:retrieveEndInformation>");
		wout.write("<circPathInstID>" + circuit.getCircPathInstID() + "</circPathInstID>");
		wout.write("<circPathHumID></circPathHumID>");
		if (serviceType != null && serviceType.toUpperCase().contains("LANLINK") && subcategory != null && !subcategory.equals("")) {
			wout.write("<subCategory>"+subcategory+"</subCategory>");
		}
		if (serviceType != null && !serviceType.equals("")){
			wout.write("<serviceType>"+serviceType+"</serviceType>");
		}
		wout.write("<leg></leg>");
		wout.write("</ws:retrieveEndInformation>");
		wout.write("</soapenv:Body>");
		wout.write("</soapenv:Envelope>");
		wout.flush();
		InputStream in  = connection.getInputStream();
		result = new Util().getStringFromInputStream(in);
		return result;
	}

	public String getInfoFromAmnChecker(Circuit circuit) throws Exception {
		String result = null;
		String url = SstConfig.getDefaultInstance().getProperty("ws.amnChecker.url");
		URL u = new URL(url);
		URLConnection uc = u.openConnection();
		HttpURLConnection connection = (HttpURLConnection) uc;

		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.connect();

		OutputStream out = connection.getOutputStream();
		Writer wout = new BufferedWriter(new OutputStreamWriter(out));

		wout.write("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ws='http://ws.amnchecker.colt.com/'>");
		wout.write("<soapenv:Header/>");
		wout.write("<soapenv:Body>");
		wout.write("<ws:amnChecker>");
		wout.write("<arg0>Circuit</arg0>");
		wout.write("<arg1>"+ circuit.getCircuitID() + "</arg1>");
		wout.write("<arg2>0</arg2>");
		wout.write("</ws:amnChecker>");
		wout.write("</soapenv:Body>");
		wout.write("</soapenv:Envelope>");
		wout.flush();
		InputStream in  = connection.getInputStream();
		result = new Util().getStringFromInputStream(in);
		return result;
	}

	public Response retrieveResponseSideInformation(String res, String productType) {
		Response response = new Response();
		SideInformation sideInformation = null;
		Util u = new Util();
		if (res.contains("hashArray")) {
			int init = res.indexOf("<hashArray>");
			int end = res.indexOf("</hashArray>") + "</hashArray>".length();
			String parseResult = res.substring(init,end);
			sideInformation = new SideInformation();
			List<String> items = new ArrayList<String>();
			int initItem = parseResult.indexOf("<item>");
			int endItem = parseResult.indexOf("</item>") + "</item>".length();
			items.add(parseResult.substring(initItem, endItem));
			int initLastItem = parseResult.lastIndexOf("<item>");
			int endLastItem = parseResult.lastIndexOf("</item>") + "</item>".length();
			if (initItem != -1 && initLastItem != -1) {
				if (initItem != initLastItem) {
					items.add(parseResult.substring(initLastItem, endLastItem));
				}
			}
			for (int z = 0; z < items.size(); z++) {
				int initKey = items.get(z).indexOf("<key>");
				int endKey = items.get(z).indexOf("</key>") + "</key>".length();
				String key = items.get(z).substring(initKey, endKey);
				String resultKey = u.getValue(key, "<key>", "</key>");
				int initValue = items.get(z).indexOf("<value>");
				int endValue = items.get(z).indexOf("</value>") + "</value>".length();
				String resultValue = u.getValue(items.get(z).substring(initValue, endValue), "<value>", "</value>");
				resultValue = resultValue.substring(1, resultValue.length() -1);
				String[] itemValueArray = resultValue.split(", ");
				this.populateSideInformation(sideInformation, resultKey, itemValueArray, productType);
			}
			response.setStatus(Response.SUCCESS);
		} else if (res.contains("errorCode")) {
			int initErrorMsg = res.indexOf("<errorMsg>");
			int endErrorMsg = res.indexOf("</errorMsg>") + "</errorMsg>".length();
			int initResponseStatus = res.indexOf("<responseStatus>");
			int endResponseStatus = res.indexOf("</responseStatus>") + "</responseStatus>".length();
			response.setErrorCode(Response.CODE_UNKNOWN);
			String responseStatus = u.getValue(res.substring(initResponseStatus, endResponseStatus), "<responseStatus>", "</responseStatus>");
			if ("SUCCESS".equals(responseStatus)) {
				response.setStatus(Response.SUCCESS);
			} else if ("ERROR".equals(responseStatus)) {
				response.setStatus(Response.FAIL);
			}
			response.setErrorMsg(u.getValue(res.substring(initErrorMsg, endErrorMsg), "<errorMsg>", "</errorMsg>"));
		}
		response.setResult(sideInformation);
		return response;
	}

	private void populateSideInformation(SideInformation sideInformation, String key, String[] kv, String productType) {
		if (key.equals("AEND")) {
			sideInformation.setaSideInformation(populateASideInformation(kv));
		} else if (key.equals("ZEND")) {
			sideInformation.setzSideInformation(populateZSideInformation(kv, productType));
		}
	}

	private ASideInformation populateASideInformation(String[] itemValueArray) {
		ASideInformation aSideInformation = new ASideInformation();
		aSideInformation.setType(messages.getProperty("serviceData.aSide.siteType.value"));
		for (int i = 0; i < itemValueArray.length; i++) {
			String[] kv = itemValueArray[i].split("=");
			if (kv[0].equals("port")) {
				String port = kv[1];
				if (port != null && port.startsWith("[")) {
					port = port.substring(1,port.length()-1);
				}
				aSideInformation.setPort(port);
			}
			if (kv[0].equals("vendor")) {
				aSideInformation.setVendor(kv[1]);
			}
			if (kv[0].equals("model")) {
				aSideInformation.setModel(kv[1]);
			}
			if (kv[0].equals("name")) {
				aSideInformation.setXngDeviceName(kv[1]);
			}
			if (kv[0].equals("inst")) {
				aSideInformation.setInstId(kv[1]);
			}
		}
		return aSideInformation;
	}

	private ZSideInformation populateZSideInformation(String[] itemValueArray, String productType) {
		ZSideInformation zSideInformation = new ZSideInformation();
		if(productType != null && ProductType.LANLINK.value().equalsIgnoreCase(productType)) {
			zSideInformation.setType(messages.getProperty("serviceData.aSide.siteType.value"));
		} else {
			zSideInformation.setType(messages.getProperty("serviceData.zSide.siteType.value"));
		}
		for (int i = 0; i < itemValueArray.length; i++) {
			String[] kv = itemValueArray[i].split("=");
			if (kv[0].equals("port")) {
				String port = kv[1];
				if (port != null && port.startsWith("[")) {
					port = port.substring(1,port.length()-1);
				}
				zSideInformation.setPort(port);
			}
			if (kv[0].equals("vendor")) {
				zSideInformation.setVendor(kv[1]);
			}
			if (kv[0].equals("name")) {
				zSideInformation.setXngDeviceName(kv[1]);
			}
			if (kv[0].equals("model")) {
				zSideInformation.setModel(kv[1]);
			}
			if (kv[0].equals("inst")) {
				zSideInformation.setInstId(kv[1]);
			}
		}
		return zSideInformation;
	}
}
