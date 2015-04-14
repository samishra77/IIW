package com.colt.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.colt.util.Util;
import com.colt.ws.biz.Circuit;
import com.colt.ws.biz.ProductType;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.Search;

public class AmnDAO extends DAO {

	private final int maxResult = 31;
	
	public AmnDAO(EntityManager em) {
		super(em);
	}

	public String geRegexSite (String value) {
		String site = value.trim();
		site = site.replaceAll("[^\\x01-\\x7F]+", ".*?"); // substitute any non-ASCII, no need to use Normalizer
		site = site.replaceAll("[\\x00-\\x1F]+", ""); // remove control chars
		site = site.replaceAll("[ _:;%.{}()\\[\\]\\+-]+",".*?"); // substitute other chars
		//site = site.replaceAll("'","''"); // escape single quotes
		site = site.replace("^\\.\\*\\?",""); // strip leading 'match all' for performance
		return site;
	}

	public Response retrieveCircuits(Search search) {
		Response response = new Response();
		//site 1 pattern
		String site = null;
		if (search.getAddress() != null && !search.getAddress().equals("")) {
			site = geRegexSite(search.getAddress());
		}
		 
		//site 2 pattern
		String site2 = null;
		if (search.getAddress2() != null && !search.getAddress2().equals("")) {
			site2 = geRegexSite(search.getAddress2());
		}

		String sql = "select unique i.circ_path_inst_id, i.circ_path_hum_id as CIRCUIT_ID, i.order_num as ORDER_NUMBER, i.customer_id as CUSTOMER, i.service_menu as PRODUC_TYPE, " +
				"j.SITE_HUM_ID as A_SITE_HUM_ID, k.SITE_HUM_ID as Z_SITE_HUM_ID, i.status, j.City as A_City, i.A_Side_Site_ID as ASIDE_SITE, i.Z_Side_Site_ID as ZSIDE_SITE, k.City as Z_City " +
				"from AMN.ie_circ_path_inst i, AMN.ie_site_inst j, AMN.ie_site_inst k " +
				"where i.status = 'Live' and ";
				if( (search.getService() != null && !"".equals(search.getService())) && (search.getOrder() != null && !"".equals(search.getOrder())) ) {
					sql+= "i.circ_path_hum_id like :service and i.order_num like :order and ";
				} else if( search.getOrder() != null && !"".equals(search.getOrder()) ) {
					sql+= "i.order_num like :order and ";
				} else if( search.getService() != null && !"".equals(search.getService()) ) {
					sql+= "i.circ_path_hum_id like :service and ";
				} else if( (search.getCustomer() != null && !"".equals(search.getCustomer())) &&
						( search.getAddress() != null && !"".equals(search.getAddress()) && search.getCity() != null && !"".equals(search.getCity()) ) ) {
					sql+= "i.customer_id like :customer and (((REGEXP_LIKE(j.SITE_HUM_ID, :site1Address, 'i') or REGEXP_LIKE(j.ADDRESS, :site1Address, 'i')) and j.City like :site1City) or ((REGEXP_LIKE(k.SITE_HUM_ID, :site1Address, 'i') or REGEXP_LIKE(k.ADDRESS, :site1Address, 'i')) and k.City like :site1City)) and ";
				} else if( (search.getCustomer() != null && !"".equals(search.getCustomer())) &&
						( search.getAddress2() != null && !"".equals(search.getAddress2()) && search.getCity2() != null && !"".equals(search.getCity2()) ) ) {
					sql+= "i.customer_id like :customer and (((REGEXP_LIKE(j.SITE_HUM_ID, :site2Address, 'i') or REGEXP_LIKE(j.ADDRESS, :site2Address, 'i')) and j.City like :site2City) or ((REGEXP_LIKE(k.SITE_HUM_ID, :site2Address, 'i') or REGEXP_LIKE(k.ADDRESS, :site2Address, 'i')) and k.City like :site2City)) and ";
				}
				sql+= "NOT REGEXP_LIKE (i.circ_path_hum_id, '-P|-AP') and i.A_Side_Site_ID = j.Site_Inst_ID and i.Z_Side_Site_ID = k.Site_Inst_ID";
		
		Query query = em.createNativeQuery(sql);
		if( search.getService() != null && !"".equals(search.getService()) ) {
			query.setParameter("service", search.getService());
		}
		if( search.getOrder() != null && !"".equals(search.getOrder()) ) {
			query.setParameter("order", search.getOrder());
		}
		if( (search.getCustomer() != null && !"".equals(search.getCustomer())) &&
					( search.getAddress() != null && !"".equals(search.getAddress()) && search.getCity() != null && !"".equals(search.getCity()) ) ) {
			query.setParameter("customer", 		search.getCustomer());
			query.setParameter("site1Address", 	site);
			query.setParameter("site1City", 	search.getCity());
		}
		if( (search.getCustomer() != null && !"".equals(search.getCustomer())) &&
				( search.getAddress2() != null && !"".equals(search.getAddress2()) && search.getCity2() != null && !"".equals(search.getCity2()) ) ) {
			query.setParameter("customer", 		search.getCustomer());
			query.setParameter("site2Address", 	site2);
			query.setParameter("site2City", 	search.getCity2());
		}

		List<Circuit> modelList = new ArrayList<Circuit>();
		HashMap<String, Circuit> circPathInstIDCircuit = new HashMap<String, Circuit>();
		query.setMaxResults(maxResult);
		List<Object[]> resutlList = query.getResultList();
		if(resutlList == null && resutlList.isEmpty()) {
			response.setErrorCode(Response.CODE_EMPTY);
			response.setErrorMsg("Not result found.");
			response.setStatus(Response.FAIL);
		} else if(resutlList != null && resutlList.size() >= maxResult) {
			response.setErrorCode(Response.CODE_MAXRESULT);
			response.setErrorMsg("Too Many Results.");
			response.setStatus(Response.FAIL);
		} else if(resutlList != null && resutlList.size() > 0 && resutlList.size() < maxResult) {
			Circuit circuit = null;
			HashMap<String, Circuit> circuitIDCircuit = new HashMap<String, Circuit>();
			for(Object[] o : resutlList) {
				circuit = new Circuit();
				circuit.setCircPathInstID(o[0] != null ? ((BigDecimal)o[0]).toString() : "");
				circuit.setCircuitID((String)o[1] != null ? (String)o[1] : "");
				circuit.setOrderNumber((String)o[2] != null ? (String)o[2] : "");
				circuit.setCustomer((String)o[3] != null ? (String)o[3] : "");
				circuit.setProductType(new Util().getProductType((o[4] != null) ? (String)o[4] : ""));
				circuit.setaSideSite(o[5] != null ? ((BigDecimal)o[5]).toString() : "");
				circuit.setzSideSite(o[6] != null ? ((BigDecimal)o[6]).toString() : "");
				if(!circuitIDCircuit.containsKey(circuit.getCircuitID())) { //save just services with diferents circuitIDS 
					circuitIDCircuit.put(circuit.getCircuitID(), circuit);
					if(!circPathInstIDCircuit.containsKey(circuit.getCircPathInstID())) {
						circPathInstIDCircuit.put(circuit.getCircPathInstID(), circuit);
					}
				}
			}
			sortServiceSearch(modelList, circPathInstIDCircuit);
		} else if(resutlList != null && resutlList.size() == 0) {
			response.setErrorCode(Response.CODE_EMPTY);
			response.setErrorMsg("Not result found.");
			response.setStatus(Response.FAIL);
		}
		if(!modelList.isEmpty()) {
			response.setResult(modelList);
		}

		return response;
	}

	private void sortServiceSearch(List<Circuit> modelList, HashMap<String, Circuit> circPathInstIDCircuit ) {
		if(modelList != null && circPathInstIDCircuit != null && !circPathInstIDCircuit.isEmpty()) {
			Set<String> circPathInstIDs = circPathInstIDCircuit.keySet();
			if(circPathInstIDs != null && !circPathInstIDs.isEmpty()) {
				List<String> circPathInstIDList = new ArrayList<String>(circPathInstIDs);
				Collections.sort(circPathInstIDList);
				for(String circPathInstID : circPathInstIDList) {
					if(circPathInstIDCircuit.containsKey(circPathInstID)) {
						modelList.add(circPathInstIDCircuit.get(circPathInstID));
					}
				}
			}
		}
	}

	/**
	 * If cid has more than 17 characters returns string with only 17 characters 
	 * @param cid
	 * @return String
	 */
	private String processCIDOHS(String cid) {
		if(cid != null && cid.length() > 17) {
			cid = cid.substring(0, 17);
		}
		return cid;
	}


	public Response retrieveServiceDetails(String circPathInstID) {
		Response response = new Response();
		
		String sql = "SELECT CIRC_PATH_INST_ID, CIRC_PATH_HUM_ID as CIRCUITID, ORDER_NUM, SERVICE_MENU AS PRODUCT_TYPE, CIRC_PATH_REV_NBR as REVISION_NUMBER, " +
				"STATUS, BANDWIDTH, TYPE as CATEGORY, CUSTOMER_ID, TRUNK_GROUP, MANAGEMENTTEAM, MONITORING, OSMORDERNO " +
				"FROM IE_CIRC_PATH_INST " +
				"WHERE CIRC_PATH_INST_ID =  :circPathInstID";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("circPathInstID", circPathInstID );

		Circuit circuit = new Circuit();
		List<Object[]> resutlList = query.getResultList();
		if(resutlList == null || resutlList.isEmpty()) {
			response.setErrorCode(Response.CODE_EMPTY);
			response.setErrorMsg("Not result found.");
			response.setStatus(Response.FAIL);
		} else if(resutlList != null && resutlList.size() == 1) {
			for(Object[] o : resutlList) {
				circuit.setCircPathInstID(o[0] != null ? ((BigDecimal)o[0]).toString() : "");
				circuit.setCircuitID((String)o[1] != null ? (String)o[1] : "");
				circuit.setOrderNumber((String)o[2] != null ? (String)o[2] : "");
				circuit.setServiceMenu((String)o[3] != null ? (String)o[3] : "");
				circuit.setProductType(new Util().getProductType((o[3] != null) ? (String)o[3] : ""));
				circuit.setRevisionNumber(o[4] != null ? ((BigDecimal)o[4]).toString() : "");
				circuit.setStatus((String)o[5] != null ? (String)o[5] : "");
				circuit.setBandWidth((String)o[6] != null ? (String)o[6] : "");
				circuit.setCategory((String)o[7] != null ? (String)o[7] : "");
				circuit.setCustomer((String)o[8] != null ? (String)o[8] : "");
				circuit.setTrunkGroup((String)o[9] != null ? (String)o[9] : "");
				circuit.setManagementTeam((String)o[10] != null ? (String)o[10] : "");
				circuit.setPerformanceMonitoring((String)o[11] != null ? (String)o[11] : "");
				response.setResult(circuit);
			}
			if(circuit.getOsmOrderNO() != null && !"".equals(circuit.getOsmOrderNO())) {
				fetchFromSiebelOrder(circuit);
			} else {
				fetchFromOHSContractRelatedTables(circuit);
			}
		} else {
			response.setErrorCode(Response.CODE_NOTONE);
			response.setErrorMsg("More than one result found.");
			response.setStatus(Response.FAIL);
		}

		response.setResult(circuit);
		return response;
	}

	private void fetchFromSiebelOrder(Circuit circuit) {
		if(circuit.getCircuitID() != null && !"".equals(circuit.getCircuitID())) {
			String sql = "select LEGAL_PARTY_NAME as CUSTOMER, PRODUCT_NAME, SERVICE_DESC, LEGAL_PARTY_OCN as OCN, D_RELATED_ORDER_NO " +
					"from AMN.IE_SIEBEL_OSM_ORDERS " +
					"where XNG_CIRCUIT_ID like :circuitID";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuit.getCircuitID()+"%");
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() == 1) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[3] != null ? (String)o[3] : "");
					circuit.setRelatedOrderNumber((String)o[4] != null ? (String)o[4] : "");
				}
			}
		}
	}

	private void fetchFromOHSContractRelatedTables(Circuit circuit) {
		if(circuit != null && circuit.getProductType() != null && !"".equals(circuit.getProductType()) ) {
			if(circuit.getProductType().equalsIgnoreCase(ProductType.HSS.value())) {
				getCircuitHSS(circuit);
			} else if(circuit.getProductType().equalsIgnoreCase(ProductType.LANLINK.value())) {
				getCircuitLANLINK(circuit);
			} else if(circuit.getProductType().equalsIgnoreCase(ProductType.IP_ACCESS.value()) || circuit.getProductType().equalsIgnoreCase(ProductType.IPVPN.value())) {
				getCircuitIP(circuit);
			} else if(circuit.getProductType().equalsIgnoreCase(ProductType.CPE_SOLUTIONS.value())) {
				getCircuitCPESOLUTIONS(circuit);
			} else {
				getCircuitOthers(circuit);
			}
		}
	}

	private void getCircuitHSS(Circuit circuit) {
		if(circuit.getCircuitID() != null && !"".equals(circuit.getCircuitID())) {
			String sql = "select a.LEGAL_CUSTOMER, a.OCN, a.PRODUCT_NAME " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_HSS_SERVICE b " +
					"where a.CONTRACT_NO = b.CONTRACT_NO and a.CIRCUIT_REFERENCE_5D = :circuitID and a.CURR_REVISION = 'YES' ORDER BY a.CIRCUIT_REFERENCE_5D";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuit.getCircuitID());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() == 1) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setCustomerOCN((String)o[1] != null ? (String)o[1] : "");
					circuit.setProductName((String)o[2] != null ? (String)o[2] : "");
				}
			}
		}
	}

	private void getCircuitLANLINK(Circuit circuit) {
		if(circuit.getCircuitID() != null && !"".equals(circuit.getCircuitID())) {
			String sql = "select a.LEGAL_CUSTOMER, a.PRODUCT_NAME, a.OCN, b.RELATED_CONTRACT_NO_ " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_LINK_LAN_ORDER b " +
					"where a.CIRCUIT_REFERENCE_5D = b.CIRCUIT_REFERENCE and a.CIRCUIT_REFERENCE_5D = :circuitID and a.CURR_REVISION = 'YES' and b.CURRENT_REVISION = 'YES' ORDER BY a.CIRCUIT_REFERENCE_5D";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuit.getCircuitID());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() == 1) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
					circuit.setRelatedOrderNumber((String)o[3] != null ? (String)o[3] : "");
				}
			}
		}
	}

	private void getCircuitIP(Circuit circuit) {
		if(circuit.getCircuitID() != null && !"".equals(circuit.getCircuitID())) {
			String sql = "select a.LEGAL_CUSTOMER, a.PRODUCT_NAME, a.OCN, b.RELATED_CONTRACT_NO " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_IP_DATA_ORDER b " +
					"where a.CIRCUIT_REFERENCE_5D = b.CIRCUIT_REFERENCE and a.CIRCUIT_REFERENCE_5D = :circuitID and a.CURR_REVISION = 'YES' and b.CURRENT_REVISION = 'YES' ORDER BY a.CIRCUIT_REFERENCE_5D";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuit.getCircuitID());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() == 1) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
					circuit.setRelatedOrderNumber((String)o[3] != null ? (String)o[3] : "");
				}
			}
		}
	}

	private void getCircuitCPESOLUTIONS(Circuit circuit) {
		if(circuit.getCircuitID() != null && !"".equals(circuit.getCircuitID())) {
			String sql = "select a.LEGAL_CUSTOMER, a.PRODUCT_NAME, a.OCN, b.RELATED_ORDER_NO_ " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_CPESOL_ORDER b " +
					"where a.CONTRACT_NO = b.ORDER_NO_ and a.CIRCUIT_REFERENCE_5D = :circuitID and a.CURR_REVISION = 'YES' and b.CURRENT_REVISION = 'YES' ORDER BY a.CIRCUIT_REFERENCE_5D";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuit.getCircuitID());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() == 1) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
					circuit.setRelatedOrderNumber((String)o[3] != null ? (String)o[3] : "");
				}
			}
		}
	}

	private void getCircuitOthers(Circuit circuit) {
		if(circuit.getCircuitID() != null && !"".equals(circuit.getCircuitID())) {
			String sql = "select LEGAL_CUSTOMER, PRODUCT_NAME, OCN " +
					"from AMN.IE_OHS_CONTRACT " +
					"where CONTRACT_NO = <Order Number> and CIRCUIT_REFERENCE_5D = :circuitID and CURR_REVISION = 'YES'";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuit.getCircuitID());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() == 1) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
				}
			}
		}
	}

}
