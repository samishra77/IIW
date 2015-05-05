package com.colt.controller;

import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.colt.dao.AmnDAO;
import com.colt.util.UsageTracking;
import com.colt.ws.biz.Circuit;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.Search;
import com.colt.ws.biz.SiebelCallRequest;
import com.colt.ws.biz.Ticket;
import com.colt.ws.service.SiebelCall;

@RestController
public class SearchService {

	@Resource(name="messages")
	private Properties messages;

	@PersistenceContext(unitName = "mainDatabase")
	private EntityManager em;

	private Log log = LogFactory.getLog(SearchService.class);

	@RequestMapping(value = "/getCircuits", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getCircuits(@RequestBody Search search, @RequestParam String username) throws Exception {
	UsageTracking usageTracking = new UsageTracking("search-circuits", username, search.toString());
		log.info("[" + username + "] Entering method getCircuits()");
		Response response = null;
		try {
			AmnDAO amnDAO = new AmnDAO(em, messages, username);
			response = amnDAO.retrieveCircuits(search);
			long resultsFetched = 0;
			if (response.getResult() != null) {
				resultsFetched = ((List<Circuit>) response.getResult()).size();
			}
			usageTracking.setResultsFetched(resultsFetched);
		} catch (Exception e) {
			log.error("[" + username + "] " + e, e);
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
			usageTracking.setStatus(UsageTracking.ERROR);
		}
		log.info("[" + username + "] Exit method getCircuits()");
		usageTracking.write();
		return response;
	}

	@RequestMapping(value = "/getServiceDetail", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getServiceDetail(@RequestBody String id, @RequestParam String username) throws Exception {
		UsageTracking usageTracking = new UsageTracking("service-details", username, "[id:" + id + "]");
		log.info("[" + username + "] Entering method getServiceDetail()");
		Response response = null;
		try {
			AmnDAO amnDAO = new AmnDAO(em, messages, username);
			response = amnDAO.retrieveServiceDetails(id);
			Circuit circuit = (Circuit) response.getResult();
			if (circuit.getCircPathInstID() != null) {
				usageTracking.setResultsFetched(1);
			}
		} catch (Exception e) {
			log.error("[" + username + "] " + e, e);
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
			usageTracking.setStatus(UsageTracking.ERROR);
		}
		log.info("[" + username + "] Exit method getServiceDetail()");
		usageTracking.write();
		return response;
	}

	@RequestMapping(value = "/getTickets", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getTickets(@RequestBody Circuit circuit, @RequestParam String username) throws Exception {
		UsageTracking usageTracking = new UsageTracking("tickets-details", username, "[CircuitID:" + circuit.getCircuitID() + " OrderNumber:" + circuit.getOrderNumber() + " OCN:" + circuit.getCustomerOCN() + "]");
		log.info("[" + username + "] Entering method getTickets()");
		Response response = new Response();
		try {
			SiebelCallRequest sielbelRequest = new SiebelCallRequest();
			sielbelRequest.setCircuitServiceID(circuit.getOrderNumber());
			sielbelRequest.setOcn(circuit.getCustomerOCN());
			SiebelCall siebel = new SiebelCall();
			String siebelResponse = siebel.siebelCallProcess(sielbelRequest);
			List<Ticket> tickets = siebel.getTicketList(siebelResponse);
			long resultsFetched = 0;
			if (tickets != null) {
				resultsFetched = tickets.size();
			}
			usageTracking.setResultsFetched(resultsFetched);
			if(tickets != null && !tickets.isEmpty()) {
				response.setStatus(Response.SUCCESS);
				response.setResult(tickets);
			} else {
				response.setStatus(Response.FAIL);
				response.setErrorCode(Response.CODE_EMPTY);
				response.setErrorMsg("No result found.");
			}
		} catch (Exception e) {
			log.error("[" + username + "] " + e, e);
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
			usageTracking.setStatus(UsageTracking.ERROR);
		}
		log.info("[" + username + "] Exit method getTickets()");
		usageTracking.write();
		return response;
	}
}
