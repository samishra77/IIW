package com.colt.ws.service;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringEscapeUtils;

import com.colt.util.SstConfig;
import com.colt.util.Util;
import com.colt.ws.biz.SiebelCallRequest;
import com.colt.ws.biz.Ticket;

public class SiebelCall {

	public List<Ticket> getTicketList(String response) {
		List<Ticket> toReturn = new ArrayList<Ticket>();
		if (response.contains("gtr:tickets")) {
			int init = response.indexOf("<gtr:tickets>");
			int end = response.indexOf("</gtr:tickets>")+"</gtr:tickets>".length();
			String parseResult = response.substring(init,end);
			boolean run = true;
			String aux;
			while (run) {
				if (parseResult.contains("<gtr:list>")) {
					int ini = parseResult.indexOf("<gtr:list>");
					int fim = parseResult.indexOf("</gtr:list>")+"</gtr:list>".length();
					aux = parseResult.substring(ini,fim);

					//get values
					Util u = new Util();
					Ticket tkt = new Ticket();
					tkt.setColtReference(u.getValue(aux, "<gtr:coltReference>", "</gtr:coltReference>"));
					tkt.setCustomerReference(u.getValue(aux, "<gtr:customerReference>", "</gtr:customerReference>"));
					tkt.setOpened(u.getValue(aux, "<gtr:startDateTime>", "</gtr:startDateTime>"));
					tkt.setRestored(u.getValue(aux, "<gtr:restoredDateTime>", "</gtr:restoredDateTime>"));
					tkt.setPriority(u.getValue(aux, "<gtr:priority>", "</gtr:priority>"));

					String reportedBy = u.getValue(aux, "<ct:firstName xmlns:ct=\"http://www.colt.net/xml/ct/ns/v3.0\">", "</ct:firstName>");
					reportedBy += " " + u.getValue(aux, "<ct:lastName xmlns:ct=\"http://www.colt.net/xml/ct/ns/v3.0\">", "</ct:lastName>");

					tkt.setReportedBy(reportedBy);
					tkt.setStatus( u.getValue(aux, "<gtr:ticketStatus>", "</gtr:ticketStatus>"));
					tkt.setTicketDescription(u.getValue(aux, "<gtr:description>", "</gtr:description>"));
					tkt.setType(u.getValue(aux, "<gtr:ticketType>", "</gtr:ticketType>"));

					toReturn.add(tkt);
					end = parseResult.indexOf("</gtr:tickets>")+"</gtr:tickets>".length();
					parseResult = parseResult.substring(fim,end);
					
				} else {
					run = false;
				}
			}
		}
		return toReturn;
	}

	public String siebelCallProcess(SiebelCallRequest req) throws Exception {
		String result = null;
		String url = null;
		String action = null;
		//set default values----------------
		Date d_end = new Date();
		GregorianCalendar gc_end = new GregorianCalendar();
		gc_end.setTime(d_end);
		
		GregorianCalendar gc_start = new GregorianCalendar();
		gc_start.setTime(d_end);
		gc_start.add(GregorianCalendar.DAY_OF_MONTH, -7);

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
		req.setSearchMethod("FILTERS");
		req.setSearchType("EXACT");
		req.setTicketStatus("All");
		req.setEarliestStartDate(sdf.format(gc_start.getTime()));
		req.setLatestStartDate(sdf.format(gc_end.getTime()));

		url = SstConfig.getDefaultInstance().getProperty("ws.siebel.url");
		action = SstConfig.getDefaultInstance().getProperty("ws.siebel.action");

		URL u = new URL(url);
		URLConnection uc = u.openConnection();
		String userpass = null;

		userpass = SstConfig.getDefaultInstance().getProperty("ws.siebel.user") + ":" + SstConfig.getDefaultInstance().getProperty("ws.siebel.pass");
		
		String basicAuth = "Basic "    + new String(DatatypeConverter.printBase64Binary(userpass.getBytes()));
		uc.setRequestProperty("Authorization", basicAuth);
		HttpURLConnection connection = (HttpURLConnection) uc;
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		///connection.setRequestProperty("SOAPAction", action);
		connection.connect();
		OutputStream out = connection.getOutputStream();
		Writer wout = new BufferedWriter(new OutputStreamWriter(out));

		wout.write("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:v1='http://www.colt.net/xml/ns/tickets/v1' xmlns:v2='http://www.colt.net/xml/ns/getTicketList/v2.2'>");
		wout.write("<soapenv:Header/>");
		wout.write("<soapenv:Body>");
		wout.write("<v1:getTicketList>");

		wout.write("<ticketListRequest>");
		wout.write("<v2:getTicketListRequest>");
		wout.write("<v2:TicketInput>");
		
		wout.write("<v2:searchMethod>"+StringEscapeUtils.escapeXml(req.getSearchMethod())+"</v2:searchMethod>");
		wout.write("<v2:searchType>"+StringEscapeUtils.escapeXml(req.getSearchType())+"</v2:searchType>");
		if (req.getColtReference() != null) {
			wout.write("<v2:coltReference>"+StringEscapeUtils.escapeXml(req.getColtReference())+"</v2:coltReference>");
		}
		if (req.getCustReference() != null) {
			wout.write("<v2:custReference>"+StringEscapeUtils.escapeXml(req.getCustReference())+"</v2:custReference>");
		}
		if (req.getCircuitServiceID() != null) {
			wout.write("<v2:circuitServiceID>"+StringEscapeUtils.escapeXml(req.getCircuitServiceID())+"</v2:circuitServiceID>");
		}
		if (req.getTicketStatus() != null) {
			wout.write("<v2:ticketStatus>"+StringEscapeUtils.escapeXml(req.getTicketStatus())+"</v2:ticketStatus>");
		}
		if (req.getTicketType() != null) {
			wout.write("<v2:ticketType>"+StringEscapeUtils.escapeXml(req.getTicketType())+"</v2:ticketType>");
		}
		if (req.getEarliestStartDate() != null) {
			wout.write("<v2:earliestStartDate>"+StringEscapeUtils.escapeXml(req.getEarliestStartDate())+"</v2:earliestStartDate>");
		}
		if (req.getLatestStartDate() != null) { 
			wout.write("<v2:latestStartDate>"+StringEscapeUtils.escapeXml(req.getLatestStartDate())+"</v2:latestStartDate>");
		}
		if (req.getMaxRowsReq() != null) {
			wout.write("<v2:maxRowsReq>"+StringEscapeUtils.escapeXml(req.getMaxRowsReq())+"</v2:maxRowsReq>");
		}
		if (req.getCityTown() != null) { 
			wout.write("<v2:cityTown>"+StringEscapeUtils.escapeXml(req.getCityTown())+"</v2:cityTown>");
		}
		if (req.getIsPartner() != null) {
			wout.write("<v2:isPartner>"+StringEscapeUtils.escapeXml(req.getIsPartner())+"</v2:isPartner>");
		}
		if (req.getPartner() != null && req.getPartner().getPairs() != null) {
			wout.write("<v2:partner>");
			wout.write("<v2:pairs>");
			wout.write("<v2:OCN>"+StringEscapeUtils.escapeXml(req.getPartner().getPairs().getOcn())+"</v2:OCN>");
			wout.write("<v2:BCN>"+StringEscapeUtils.escapeXml(req.getPartner().getPairs().getBcn())+"</v2:BCN>");
			wout.write("</v2:pairs>");
			wout.write("</v2:partner>");
		}

		wout.write("<v2:oracleCustomerNumber>");
		wout.write("<v2:pairs>");
		if (req.getOcn() != null) {
			wout.write("<v2:OCN>"+StringEscapeUtils.escapeXml(req.getOcn())+"</v2:OCN>");
		}
		if (req.getBcn() != null) {
			wout.write("<v2:BCN>"+StringEscapeUtils.escapeXml(req.getBcn())+"</v2:BCN>");
		}
		wout.write("</v2:pairs>");
		wout.write("</v2:oracleCustomerNumber>");
		wout.write("</v2:TicketInput>");
		wout.write("</v2:getTicketListRequest>");
		wout.write("</ticketListRequest>");
		wout.write("</v1:getTicketList>");
		wout.write("</soapenv:Body>");
		wout.write("</soapenv:Envelope>");
		wout.flush();
		InputStream in  = connection.getInputStream();
		result = new Util().getStringFromInputStream(in);
		return result;
	}

}
