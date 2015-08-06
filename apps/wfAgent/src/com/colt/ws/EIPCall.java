package com.colt.ws;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentConfig;
import com.colt.util.AgentUtil;



public class EIPCall {

	private Log log;

	public EIPCall() {
		log = LogFactory.getLog(getClass());
	}

	public List<String> process (String xngDeviceName) throws IOException {
		String response = eipRequest(xngDeviceName);
		List<String> list = getIpAddressList(response);
		return list;
	}

	private static String long2ip(long value) {
		return ((value >> 24) & 0xFF) + "." + ((value >> 16) & 0xFF) + "." + ((value >> 8) & 0xFF) + "." + (value & 0xFF);
	}

	private List<String> getIpAddressList(String response) {
		List<String> ret = new ArrayList<String>();
		if (response.toString().contains("<item")) {
			int init = response.indexOf("<item");
			String parseResult = response.substring(init,response.length());
			boolean run = true;
			String aux;
			while (run) {
				if (parseResult.contains("<item xsi:type=\"ns2:ip_address_list_out\">")) {
					int ini = parseResult.indexOf("<item xsi:type=\"ns2:ip_address_list_out\">");
					int fim = parseResult.indexOf("</item>") + "</item>".length();
					aux = parseResult.substring(ini,fim);
					AgentUtil u = new AgentUtil();
					String valuehex = u.getValue(aux, "<ip_addr xsi:type=\"xsd:string\">", "</ip_addr>");
					try {
						long ipLong = Long.parseLong(String.valueOf(new BigInteger(valuehex, 16)));
						ret.add(long2ip (ipLong));
					} catch (Exception e) {
						log.error(e,e);
					}
					parseResult = parseResult.substring(fim,parseResult.length());
				} else {
					run = false;
				}
			}
		}
		return ret;
	}

	private String eipRequest (String xngDeviceName) throws IOException {
		String result = null;
		String url = null;

		url = AgentConfig.getDefaultInstance().getProperty("ws.eip.url");
		URL u = new URL(url);
		URLConnection uc = u.openConnection();
		String pass = AgentConfig.getDefaultInstance().getProperty("ws.eip.pass");
		String username = AgentConfig.getDefaultInstance().getProperty("ws.eip.user");
		String action = AgentConfig.getDefaultInstance().getProperty("ws.eip.action");

		HttpURLConnection connection = (HttpURLConnection) uc;
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("SOAPAction", action);

		connection.connect();
		OutputStream out = connection.getOutputStream();
		Writer wout = new BufferedWriter(new OutputStreamWriter(out));

		wout.write("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:IPM_SOAP\">");
		wout.write("<soapenv:Header/>");
		wout.write("<soapenv:Body>");
		wout.write("<urn:ip_address_list soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");

		wout.write("<input xsi:type=\"urn:ip_address_list_in\" xmlns:urn=\"urn:IPM\">");
		wout.write("<auth_login xsi:type=\"xsd:string\">"+ StringEscapeUtils.escapeXml(username)+"</auth_login>");
		wout.write("<auth_password xsi:type=\"xsd:string\">"+ StringEscapeUtils.escapeXml(pass) +"</auth_password>");
		wout.write("<WHERE xsi:type=\"xsd:string\">name='"+ StringEscapeUtils.escapeXml(xngDeviceName) +"'</WHERE>");
		wout.write("</input>");
		wout.write("</urn:ip_address_list>");
		wout.write("</soapenv:Body>");
		wout.write("</soapenv:Envelope>");
		wout.flush();
		InputStream in  = connection.getInputStream();
		result = new AgentUtil().getStringFromInputStream(in);
		return result;
	}

}
