package com.colt.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.colt.dao.AmnDAO;
import com.colt.util.Util;
import com.colt.ws.biz.Circuit;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.Search;
import com.colt.ws.biz.SiebelCallRequest;
import com.colt.ws.biz.Ticket;
import com.colt.ws.service.SiebelCall;

@RestController
public class SearchService {

	@PersistenceContext(unitName = "mainDatabase")
	private EntityManager em;

	@RequestMapping(value = "/getCircuits", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getCircuits(@RequestBody Search search) {
		Response response = null;
		try {
			AmnDAO amnDAO = new AmnDAO(em);
			response = amnDAO.retrieveCircuits(search);
		} catch (Exception e) {
			e.printStackTrace();
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getServiceDetail", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getServiceDetail(@RequestBody String id) {
		Response response = null;
		try {
			AmnDAO amnDAO = new AmnDAO(em);
			response = amnDAO.retrieveServiceDetails(id);
		} catch (Exception e) {
			e.printStackTrace();
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getTickets", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getTickets(@RequestBody Circuit circuit) {
		Response response = new Response();
		try {
			SiebelCallRequest sielbelRequest = new SiebelCallRequest();
			sielbelRequest.setCircuitServiceID(circuit.getCircuitID());
			sielbelRequest.setOcn(circuit.getCustomerOCN());
			SiebelCall siebel = new SiebelCall();
			String siebelResponse = siebel.siebelCallProcess(sielbelRequest);
			List<Ticket> tickets = siebel.getTicketList(siebelResponse);
			if(tickets != null && !tickets.isEmpty()) {
				response.setStatus(Response.SUCCESS);
				response.setResult(tickets);
			} else {
				response.setStatus(Response.FAIL);
				response.setErrorCode(Response.CODE_EMPTY);
				response.setErrorMsg("Not result found.");
			}
		} catch (Exception e) {
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}
}
