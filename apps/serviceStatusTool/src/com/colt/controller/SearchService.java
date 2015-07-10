package com.colt.controller;

import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

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
import com.colt.ws.biz.ProductType;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.Search;
import com.colt.ws.biz.SideInformation;
import com.colt.ws.biz.SiebelCallRequest;
import com.colt.ws.biz.Ticket;
import com.colt.ws.service.SideInformationCall;
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
		UsageTracking usageTracking = new UsageTracking("service-details", username, "[service:]");
		log.info("[" + username + "] Entering method getServiceDetail()");
		Response response = null;
		try {
			AmnDAO amnDAO = new AmnDAO(em, messages, username);
			response = amnDAO.retrieveServiceDetails(id);
			Circuit circuit = (Circuit) response.getResult();
			if (circuit.getCircPathInstID() != null) {
				usageTracking.setResultsFetched(1);
				usageTracking.setParams("[service:" + circuit.getCircuitID() + "]");
			}
			if(circuit.getRelatedOrderNumber() != null && !"".equals((circuit.getRelatedOrderNumber()))) {
				if(circuit.getRelatedOrderNumber().contains("\n")) {
					circuit.setRelatedOrderNumber(circuit.getRelatedOrderNumber().replaceAll("\n", " "));
				}
				StringTokenizer st = new StringTokenizer(circuit.getRelatedOrderNumber(), ";,& ");
				while(st.hasMoreTokens()) {
					circuit.getRelatedOrderNumberList().add(st.nextToken());
				}
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
		UsageTracking usageTracking = new UsageTracking("tickets-details", username, "[service:" + circuit.getCircuitID() + " OrderNumber:" + circuit.getOrderNumber() + " OCN:" + circuit.getCustomerOCN() + "]");
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

	@RequestMapping(value = "/getSideInformation", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getSideInformation(@RequestBody Circuit circuit, @RequestParam String username) throws Exception {
		UsageTracking usageTracking = new UsageTracking("side-information", username, circuit.toString());
		log.info("[" + username + "] Entering method getSideInformation()");
		Response response = null;
		try {
			SideInformationCall pwv = new SideInformationCall(messages);
			String pathViewerResponse = pwv.sideInformationCallProcess(circuit);
			response = pwv.retrieveResponseSideInformation(pathViewerResponse, circuit.getProductType());
			if (response.getResult() != null) {
				usageTracking.setResultsFetched(1);
			}
			if (response.getResult() != null) {
				SideInformation si = (SideInformation) response.getResult();
				if (si != null && si.getzSideInformation() != null) {
					if (si.getzSideInformation().getInstId() != null && !"".equals(si.getzSideInformation().getInstId())) {
						AmnDAO amnDAO = new AmnDAO(em, messages, username);
						String networkObject = amnDAO.getNetworkObject(si.getzSideInformation().getInstId());
						if (networkObject != null && !"".equals(networkObject)) {
							si.getzSideInformation().setXngDeviceName(networkObject);
						}
					}
				}
				if (si != null && si.getaSideInformation() != null) {
					if (si.getaSideInformation().getInstId() != null && !"".equals(si.getaSideInformation().getInstId())) {
						AmnDAO amnDAO = new AmnDAO(em, messages, username);
						String networkObject = amnDAO.getNetworkObject(si.getaSideInformation().getInstId());
						if (networkObject != null && !"".equals(networkObject)) {
							si.getaSideInformation().setXngDeviceName(networkObject);
						}
					}
				}
			}
			response = processDevNameASideInformation(response, circuit, username);
			response = processDevNameZSideInformation(response, circuit, username);
		} catch (Exception e) {
			log.error("[" + username + "] " + e, e);
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
			usageTracking.setStatus(UsageTracking.ERROR);
		}
		log.info("[" + username + "] Exit method getSideInformation()");
		usageTracking.write();
		return response;
	}

	private Response processDevNameASideInformation(Response response,Circuit circuit, String username) {
		if (response != null) {
			AmnDAO amnDAO = new AmnDAO(em, messages, username);
			String deviceName = "";
			SideInformation si = (SideInformation) response.getResult();
			String routerId = "";
			if (si != null && si.getaSideInformation() != null) {
				routerId = si.getaSideInformation().getXngDeviceName();
			}
			if (routerId != null && !"".equals(routerId)) {
				if (circuit != null && circuit.getProductType() != null && !"".equals(circuit.getProductType())) {
					if(circuit.getProductType().equalsIgnoreCase(ProductType.IP_ACCESS.value())) {
						deviceName = routerId + ".ia.colt.net";
					} else {
						if (circuit.getProductType().equalsIgnoreCase(ProductType.LANLINK.value())) {
							if (si.getaSideInformation().getVendor() != null) {
								if (si.getaSideInformation().getVendor().equalsIgnoreCase("Accedian") || si.getaSideInformation().getVendor().equalsIgnoreCase("Overture")){ 
									deviceName = routerId + ".lanlink.dcn.colt.net";
								}
								if (si.getaSideInformation().getVendor().equalsIgnoreCase("Actelis") || si.getaSideInformation().getVendor().equalsIgnoreCase("Atrica")) {
									deviceName = routerId;
								}
							}
						}
					}
				}
			}
			if (circuit != null && circuit.getProductType() != null && !"".equals(circuit.getProductType())) {
				String circuitId = amnDAO.processCircuitId(circuit.getCircuitID());
				if(circuit.getProductType().equalsIgnoreCase(ProductType.CPE_SOLUTIONS.value())) {
					deviceName = amnDAO.getFqdnCpeSol(circuit, circuitId);
				} else if(circuit.getProductType().equalsIgnoreCase(ProductType.IPVPN.value())) {
					deviceName = amnDAO.getFqdnIPVPN(circuit, circuitId);
				}
			}
			if (deviceName != null && !"".equals(deviceName)) {
				if (si != null && si.getaSideInformation() != null) {
					si.getaSideInformation().setDeviceName(deviceName);
				}
				response.setResult(si);
			}
		}
		return response;
	}

	private Response processDevNameZSideInformation(Response response,Circuit circuit, String username) {
		if (response != null) {
			String deviceName = "";
			SideInformation si = (SideInformation) response.getResult();
			String routerId = "";
			if (si != null && si.getzSideInformation() != null) {
				routerId = si.getzSideInformation().getXngDeviceName();
			}
			if (routerId != null && !"".equals(routerId)) {
				if (circuit != null && circuit.getProductType() != null && circuit.getProductType().equalsIgnoreCase(ProductType.LANLINK.value())) {
					if (si.getzSideInformation().getVendor() != null) {
						if (si.getzSideInformation().getVendor().equalsIgnoreCase("Accedian") || si.getzSideInformation().getVendor().equalsIgnoreCase("Overture")) {
							deviceName = routerId + ".lanlink.dcn.colt.net";
						} 
						if (si.getzSideInformation().getVendor().equalsIgnoreCase("Actelis") || si.getzSideInformation().getVendor().equalsIgnoreCase("Atrica")) {
							deviceName = routerId;
						}
					}
				} else {
					deviceName = "lo0-" + routerId + ".router.colt.net";
				}
			}
			if (deviceName != null && !"".equals(deviceName)) {
				if (si != null && si.getzSideInformation() != null) {
					si.getzSideInformation().setDeviceName(deviceName);
				}
				response.setResult(si);
			}
		}
		return response;
	}

	@RequestMapping(value = "/getCircuitsByOrderNumber", method = RequestMethod.GET, headers = "Accept=application/json")
	public Object getCircuitsByOrderNumber(@RequestParam String orderNumber, @RequestParam String username) throws Exception {
		UsageTracking usageTracking = new UsageTracking("circuits-order-number", username, "[OrderNumber:" + orderNumber + "]");
		log.info("[" + username + "] Entering method getCircuitsByOrderNumber()");
		Response response = null;
		try {
			AmnDAO amnDAO = new AmnDAO(em, messages, username);
			Search search = new Search();
			search.setOrder(orderNumber);
			response = amnDAO.retrieveCircuits(search);
			long resultsFetched = 0;
			if (response.getResult() != null) {
				resultsFetched = ((List<Circuit>) response.getResult()).size();
				usageTracking.setResultsFetched(resultsFetched);
			}
		} catch (Exception e) {
			log.error("[" + username + "] " + e, e);
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
			usageTracking.setStatus(UsageTracking.ERROR);
		}
		log.info("[" + username + "] Exit method getCircuitsByOrderNumber()");
		usageTracking.write();
		return response;
	}
}
