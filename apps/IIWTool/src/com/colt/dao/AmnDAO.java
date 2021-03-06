package com.colt.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.PamStatusCalculator;
import com.colt.util.Util;
import com.colt.ws.biz.Circuit;
import com.colt.ws.biz.ProductType;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.Search;

public class AmnDAO extends DAO {

	private final int maxResult = 60;
	private Log log = LogFactory.getLog(AmnDAO.class);

	public AmnDAO(EntityManager em, Properties resource, String username) {
		super(em, resource, username);
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

	public Response retrieveCircuits(Search search) throws Exception {
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

		String sql = "select i.circ_path_inst_id as circPathInstID, i.circ_path_hum_id as CIRCUIT_ID, i.order_num as ORDER_NUMBER, i.customer_id as CUSTOMER, i.service_menu as PRODUC_TYPE, " +
				"j.SITE_HUM_ID as A_SITE_HUM_ID, k.SITE_HUM_ID as Z_SITE_HUM_ID, i.status, j.City as A_City, i.A_Side_Site_ID as ASIDE_SITE, i.Z_Side_Site_ID as ZSIDE_SITE, k.City as Z_City " +
				"from AMN.ie_circ_path_inst i, AMN.ie_site_inst j, AMN.ie_site_inst k " +
				"where i.status = 'Live' ";
		if( (search.getService() != null && !"".equals(search.getService())) && (search.getOrder() != null && !"".equals(search.getOrder())) ) {
			sql+= " and upper(i.circ_path_hum_id) like ? and upper(i.order_num) like  ? ";
		} else if( search.getOrder() != null && !"".equals(search.getOrder()) ) {
			sql+= "and upper(i.order_num) like ? ";
		} else if( search.getService() != null && !"".equals(search.getService()) ) {
			sql+= "and upper(i.circ_path_hum_id) like ? ";
		} else if(search.getCustomer() != null && !"".equals(search.getCustomer())) {
			sql+= "and upper(i.customer_id) like ? ";
			if( (search.getAddress() != null && !"".equals(search.getAddress())) || (search.getCity() != null && !"".equals(search.getCity())) ) {
				sql+= " and (( ";
				if( search.getAddress() != null && !"".equals(search.getAddress()) )  {
					sql+= " (REGEXP_LIKE(j.SITE_HUM_ID, ?, 'i')  or REGEXP_LIKE(j.ADDRESS, ?, 'i')) ";
				}
				if( search.getCity() != null && !"".equals(search.getCity()) )  {
					if(search.getAddress() != null && !"".equals(search.getAddress()) ) {
						sql+= " and ";
					}
					sql+= " upper(j.City) like ? ";
				}
				sql+= " ) or ( "; 
				if( search.getAddress() != null && !"".equals(search.getAddress()) )  {
					sql+= "(REGEXP_LIKE(k.SITE_HUM_ID, ?, 'i') or REGEXP_LIKE(k.ADDRESS, ?, 'i')) ";
				}
				if( search.getCity() != null && !"".equals(search.getCity()) )  {
					if(search.getAddress() != null && !"".equals(search.getAddress()) ) {
						sql+= " and ";
					}
					sql+= " upper(k.City) like ? ";
				}
				sql+= " )) ";
			}
			if( (search.getAddress2() != null && !"".equals(search.getAddress2())) || (search.getCity2() != null && !"".equals(search.getCity2())) ) {
				sql+= " and (( ";
				if( search.getAddress2() != null && !"".equals(search.getAddress2()) )  {
					sql+= " (REGEXP_LIKE(j.SITE_HUM_ID, ?, 'i')  or REGEXP_LIKE(j.ADDRESS, ?, 'i')) ";
				}
				if( search.getCity2() != null && !"".equals(search.getCity2()) )  {
					if(search.getAddress2() != null && !"".equals(search.getAddress2()) ) {
						sql+= " and ";
					}
					sql+= " upper(j.City) like ? ";
				}
				sql+= " ) or ( "; 
				if( search.getAddress2() != null && !"".equals(search.getAddress2()) )  {
					sql+= "(REGEXP_LIKE(k.SITE_HUM_ID, ?, 'i') or REGEXP_LIKE(k.ADDRESS, ?, 'i')) ";
				}
				if( search.getCity2() != null && !"".equals(search.getCity2()) )  {
					if(search.getAddress2() != null && !"".equals(search.getAddress2()) ) {
						sql+= " and ";
					}
					sql+= " upper(k.City) like ? ";
				}
				sql+= " )) ";
			}
		}
		sql+= " and NOT REGEXP_LIKE (i.circ_path_hum_id, '-P|-AP') and i.A_Side_Site_ID = j.Site_Inst_ID and i.Z_Side_Site_ID = k.Site_Inst_ID";

		Connection conn = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		log.info("[" + username + "] " + sql + "\n" + search);
		try {
			conn = getConnection();
			prepStmt = conn.prepareStatement(sql);
			int idx = 1;
			if( search.getService() != null && !"".equals(search.getService()) ) {
				prepStmt.setString(idx++, search.getService().toUpperCase());
			}
			if( search.getOrder() != null && !"".equals(search.getOrder()) ) {
				String[] split = search.getOrder().split("/");
				if (split != null && split.length > 0) {
					search.setOrder(split[0]);
				}
				char lastCharacter = search.getOrder().charAt(search.getOrder().length() -1);
				if (lastCharacter != "%".charAt(0)) {
					search.setOrder(search.getOrder() + "%");
				}
				prepStmt.setString(idx++, search.getOrder().toUpperCase());
			}
			if( search.getCustomer() != null && !"".equals(search.getCustomer())) {
				prepStmt.setString(idx++, search.getCustomer().toUpperCase());
			}

			if( search.getAddress() != null && !"".equals(search.getAddress())) {
				prepStmt.setString(idx++, site);
				prepStmt.setString(idx++, site);
			}
			if( search.getCity() != null && !"".equals(search.getCity())) {
				prepStmt.setString(idx++, search.getCity().toUpperCase());
			}
			if( search.getAddress() != null && !"".equals(search.getAddress())) {
				prepStmt.setString(idx++, site);
				prepStmt.setString(idx++, site);
			}
			if( search.getCity() != null && !"".equals(search.getCity())) {
				prepStmt.setString(idx++, search.getCity().toUpperCase());
			}

			if( search.getAddress2() != null && !"".equals(search.getAddress2())) {
				prepStmt.setString(idx++, site2);
				prepStmt.setString(idx++, site2);
			}
			if( search.getCity2() != null && !"".equals(search.getCity2())) {
				prepStmt.setString(idx++, search.getCity2().toUpperCase());
			}
			if( search.getAddress2() != null && !"".equals(search.getAddress2())) {
				prepStmt.setString(idx++, site2);
				prepStmt.setString(idx++, site2);
			}
			if( search.getCity2() != null && !"".equals(search.getCity2())) {
				prepStmt.setString(idx++, search.getCity2().toUpperCase());
			}

			List<Circuit> modelList = new ArrayList<Circuit>();
			HashMap<String, Circuit> circPathInstIDCircuit = new HashMap<String, Circuit>();

			long time = System.currentTimeMillis();
			rs = prepStmt.executeQuery();
			Circuit circuit = null;
			List<String> circuitIDList = new ArrayList<String>();
			int count = 0;
			while (rs.next()) {
				count++;
				if(!circuitIDList.contains(processCIDOHS(rs.getString("CIRCUIT_ID") != null ? rs.getString("CIRCUIT_ID") : ""))) { //save just services with diferents circuitIDS 
					circuit = new Circuit();
					circuit.setCircPathInstID(rs.getBigDecimal("circPathInstID") != null ? rs.getBigDecimal("circPathInstID").toString() : "");
					circuit.setCircuitID(rs.getString("CIRCUIT_ID") != null ? rs.getString("CIRCUIT_ID") : "");
					circuit.setOrderNumber(rs.getString("ORDER_NUMBER") != null ? rs.getString("ORDER_NUMBER") : "");
					circuit.setCustomer(rs.getString("CUSTOMER") != null ? rs.getString("CUSTOMER") : "");
					circuit.setProductType(new Util().getProductType(rs.getString("PRODUC_TYPE") != null ? rs.getString("PRODUC_TYPE") : ""));
					circuit.setaSideSite(rs.getString("A_SITE_HUM_ID") != null ? rs.getString("A_SITE_HUM_ID") : "");
					circuit.setzSideSite(rs.getString("Z_SITE_HUM_ID") != null ? rs.getString("Z_SITE_HUM_ID") : "");
					circuitIDList.add(circuit.getCircuitID());
					if(!circPathInstIDCircuit.containsKey(circuit.getCircPathInstID())) {
						circPathInstIDCircuit.put(circuit.getCircPathInstID(), circuit);
					}
				}
				if (circPathInstIDCircuit.size() > maxResult) {
					response.setErrorCode(Response.CODE_MAXRESULT);
					response.setErrorMsg(getMessages().getProperty("global.error.toManyResults"));
					response.setStatus(Response.FAIL);
					break;
				}
			}
			log.info("[" + username + "] query time: " + (System.currentTimeMillis() - time) + " | db rows: " + count + " | map size: " + circPathInstIDCircuit.size());

			if(circPathInstIDCircuit.size() == 0) {
				response.setErrorCode(Response.CODE_EMPTY);
				response.setErrorMsg(getMessages().getProperty("global.error.resultNotFound"));
				response.setStatus(Response.FAIL);
			} else if(circPathInstIDCircuit.size() <= maxResult) {
				sortServiceSearch(modelList, circPathInstIDCircuit);
				response.setResult(modelList);
			}
		} finally {
			if(rs != null) {
				rs.close();
			}
			if(prepStmt != null) {
				prepStmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
		return response;
	}

	private void sortServiceSearch(List<Circuit> modelList, HashMap<String, Circuit> circPathInstIDCircuit ) {
		if(modelList != null && circPathInstIDCircuit != null && !circPathInstIDCircuit.isEmpty()) {
			Set<String> circPathInstIDs = circPathInstIDCircuit.keySet();
			if(circPathInstIDs != null && !circPathInstIDs.isEmpty()) {
				List<String> circPathInstIDList = new ArrayList<String>(circPathInstIDs);
				if(circPathInstIDList.size() > 1) {
					Collections.sort(circPathInstIDList);
				}
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
				"STATUS, BANDWIDTH, TYPE as CATEGORY, CUSTOMER_ID, TRUNK_GROUP, MANAGEMENTTEAM, MONITORING, OSMORDERNO, IN_SERVICE, PERF_MONITORING " +
				"FROM AMN.IE_CIRC_PATH_INST " +
				"WHERE CIRC_PATH_INST_ID =  :circPathInstID";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("circPathInstID", circPathInstID );

		Circuit circuit = new Circuit();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		List<Object[]> resutlList = query.getResultList();
		String pam = "";
		if(resutlList == null || resutlList.isEmpty()) {
			response.setErrorCode(Response.CODE_EMPTY);
			response.setErrorMsg(getMessages().getProperty("global.error.resultNotFound"));
			response.setStatus(Response.FAIL);
		} else {
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
				pam = (String)o[11] != null ? (String)o[11] : "";
				circuit.setOsmOrderNO((String)o[12] != null ? (String)o[12] : "");
				circuit.setInServiceSince(o[13] != null ? df.format((Date)o[13]) : "");
				circuit.setPerformanceMonitoring((String)o[14] != null ? (String)o[14] : "");
				response.setResult(circuit);
			}
			String circuitId = null;
			if (circuit != null) {
				circuitId = processCircuitId(circuit.getCircuitID());
			}
			if(circuit.getOsmOrderNO() != null && !"".equals(circuit.getOsmOrderNO())) {
				fetchFromSiebelOrder(circuit, circuitId);
			} else {
				fetchFromOHSContractRelatedTables(circuit, circuitId);
			}
			try {
				String[] dateArray = circuit.getInServiceSince().split("/");
				if(dateArray != null && dateArray.length > 2) {
					int month = Integer.valueOf(dateArray[1]);
					if(month > 0) {
						month = month - 1;
					}
					Date date = PamStatusCalculator.getDate(Integer.valueOf(dateArray[2]), month, Integer.valueOf(dateArray[0]));
					circuit.setPamStatus(PamStatusCalculator.getPamEnabledSprint1(circuit.getCategory(), circuit.getServiceMenu(), circuit.getProductName(), pam, date));
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}

		response.setResult(circuit);
		return response;
	}

	public String processCircuitId(String circuitId) {
		String retrieve = "";
		if (circuitId != null && !"".equals(circuitId)) {
			if (circuitId.length() >= 17) {
				retrieve = circuitId.substring(0, 17);
			} else {
				retrieve = circuitId;
			}
		}
		return retrieve;
	}

	private void fetchFromSiebelOrder(Circuit circuit, String circuitId) {
		if(circuitId != null && !"".equals(circuitId)) {
			String sql = "select LEGAL_PARTY_NAME as CUSTOMER, SERVICE_DESC, LEGAL_PARTY_OCN as OCN, D_RELATED_ORDER_NO, NETWORK_ID, RESILIENCE_OPTION " +
					"from AMN.IE_SIEBEL_OSM_ORDERS " +
					"where SUBSTR(XNG_CIRCUIT_ID,1,17) like :circuitID";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId + "%");
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() > 0) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
					circuit.setRelatedOrderNumber((String)o[3] != null ? (String)o[3] : "");
					circuit.setServiceId((String)o[4] != null ? (String)o[4] : "");
					circuit.setResilienceType((String)o[5] != null ? (String)o[5] : "");
				}
			}
		}
	}

	private void fetchFromOHSContractRelatedTables(Circuit circuit, String circuitId){
		recapProcessServicesWithoutProduct(circuit, circuitId);
		if(circuit != null && circuit.getProductType() != null && !"".equals(circuit.getProductType()) ) {
			if(circuit.getProductType().equalsIgnoreCase(ProductType.HSS.value())) {
				getCircuitHSS(circuit , circuitId);
			} else if(circuit.getProductType().equalsIgnoreCase(ProductType.LANLINK.value())) {
				getCircuitLANLINK(circuit, circuitId);
			} else if(circuit.getProductType().equalsIgnoreCase(ProductType.IP_ACCESS.value()) || circuit.getProductType().equalsIgnoreCase(ProductType.IPVPN.value())) {
				getCircuitIP(circuit, circuitId);
			} else if(circuit.getProductType().equalsIgnoreCase(ProductType.CPE_SOLUTIONS.value())) {
				getCircuitCPESOLUTIONS(circuit, circuitId);
			} else {
				getCircuitOthers(circuit, circuitId);
			}
		} else {
			getCircuitOthers(circuit, circuitId);
		}
	}
	
	/**
	 * Try to identify the ProductType of services at the beginning lacked ProductType.
	 * @param HashMap<String ProductType, List<ServiceAttribType>> productServiceList
	 * @throws Exception
	 */
	private void recapProcessServicesWithoutProduct(Circuit circuit, String circuitId){
		if(circuit != null && (circuit.getProductType() == null || "".equals(circuit.getProductType())) && circuitId != null && !"".equals(circuitId) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber()) ) {
			String sql = "select distinct LEGAL_CUSTOMER, PRODUCT_NAME, OCN " +
					"from AMN.IE_OHS_CONTRACT " +
					"where CONTRACT_NO = :orderNumber and SUBSTR(CIRCUIT_REFERENCE_5D,1,17) = :circuitID";
			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId);
			query.setParameter("orderNumber", circuit.getOrderNumber());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() > 0) {
				for(Object[] o : resutlList) {
					circuit.setProductType(new Util().getProductType((String)o[1] != null ? (String)o[1] : ""));
					circuit.setServiceMenu((String)o[1] != null ? (String)o[1] : "");
					break;
				}
			}
		}

	}
	


	private void getCircuitHSS(Circuit circuit, String circuitId) {
		if(circuitId != null && !"".equals(circuitId) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber())) {
			String sql = "select a.LEGAL_CUSTOMER, a.OCN, b.SERVICE_DETAILS " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_HSS_SERVICE b " +
					"where a.CONTRACT_NO = b.CONTRACT_NO and a.CONTRACT_NO = :orderNumber and SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17) = :circuitID ORDER BY a.CIRCUIT_REFERENCE_5D";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId);
			query.setParameter("orderNumber", circuit.getOrderNumber());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() > 0) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setCustomerOCN((String)o[1] != null ? (String)o[1] : "");
					circuit.setProductName((String)o[2] != null ? (String)o[2] : "");
				}
			}
		}
	}

	private void getCircuitLANLINK(Circuit circuit, String circuitId) {
		if(circuitId != null && !"".equals(circuitId) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber())) {
			String sql = "select a.LEGAL_CUSTOMER, b.SERVICE_DETAILS, a.OCN, b.RELATED_CONTRACT_NO_ , b.SERVICE_ID, b.RESILIENCY " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_LINK_LAN_ORDER b " +
					"where a.CIRCUIT_REFERENCE_5D = b.CIRCUIT_REFERENCE and a.CONTRACT_NO = b.CONTRACT_NO and a.CONTRACT_NO = :orderNumber and SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17) = :circuitID ORDER BY a.CIRCUIT_REFERENCE_5D";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId);
			query.setParameter("orderNumber", circuit.getOrderNumber());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() > 0) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
					circuit.setRelatedOrderNumber((String)o[3] != null ? (String)o[3] : "");
					circuit.setServiceId((String)o[4] != null ? (String)o[4] : "");
					circuit.setResilienceType((String)o[5] != null ? (String)o[5] : "");
				}
			}
		}
	}

	private void getCircuitIP(Circuit circuit, String circuitId) {
		if(circuitId != null && !"".equals(circuitId) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber())) {
			String sql = "select a.LEGAL_CUSTOMER, b.SERVICE_DETAILS, a.OCN, b.RELATED_CONTRACT_NO, b.SERVICE_ID, b.RESILIENCY " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_IP_DATA_ORDER b " +
					"where a.CIRCUIT_REFERENCE_5D = b.CIRCUIT_REFERENCE and a.CONTRACT_NO = b.CONTRACT_NO and a.CONTRACT_NO = :orderNumber and SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17) = :circuitID ORDER BY a.CIRCUIT_REFERENCE_5D";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId);
			query.setParameter("orderNumber", circuit.getOrderNumber());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() > 0) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
					circuit.setRelatedOrderNumber((String)o[3] != null ? (String)o[3] : "");
					if (circuit.getProductType().equalsIgnoreCase(ProductType.IPVPN.value())) {
						circuit.setServiceId((String)o[4] != null ? (String)o[4] : "");
					}
					circuit.setResilienceType((String)o[5] != null ? (String)o[5] : "");
				}
			}
		}
	}

	private void getCircuitCPESOLUTIONS(Circuit circuit, String circuitId) {
		if(circuitId != null && !"".equals(circuitId) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber())) {
			String sql = "select distinct a.LEGAL_CUSTOMER, b.SERVICE_OPTIONS, a.OCN, b.RELATED_ORDER_NO_ , b.RESILIENCE_OPTION, SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17) " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_CPESOL_ORDER b " +
					"where a.CONTRACT_NO = b.ORDER_NO_ and a.CONTRACT_NO = :orderNumber and SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17) = :circuitID  ORDER BY SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17)";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId);
			query.setParameter("orderNumber", circuit.getOrderNumber());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() > 0) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
					circuit.setRelatedOrderNumber((String)o[3] != null ? (String)o[3] : "");
					circuit.setResilienceType(o[4] != null ? ((BigDecimal)o[4]).toString() : "");
				}
			}
		}
	}

	private void getCircuitOthers(Circuit circuit, String circuitId) {
		if(circuit != null && circuitId != null && !"".equals(circuitId) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber()) ) {
			String sql = "select distinct LEGAL_CUSTOMER, PRODUCT_NAME, OCN " +
					"from AMN.IE_OHS_CONTRACT " +
					"where CONTRACT_NO = :orderNumber and SUBSTR(CIRCUIT_REFERENCE_5D,1,17) = :circuitID";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId);
			query.setParameter("orderNumber", circuit.getOrderNumber());
			List<Object[]> resutlList = query.getResultList();
			if(resutlList != null && resutlList.size() > 0) {
				for(Object[] o : resutlList) {
					circuit.setCustomer((String)o[0] != null ? (String)o[0] : "");
					circuit.setProductName((String)o[1] != null ? (String)o[1] : "");
					circuit.setCustomerOCN((String)o[2] != null ? (String)o[2] : "");
				}
			}
		}
	}

	public String getFqdnCpeSol(Circuit circuit, String circuitId) {
		String result = "";
		if(circuitId != null && !"".equals(circuitId) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber())) {
			String sql = "select distinct b.PRIMARY_DEVICE_ID, SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17) " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_CPESOL_ORDER b " +
					"where a.CONTRACT_NO = b.ORDER_NO_ and a.CONTRACT_NO = :orderNumber and SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17) = :circuitID ORDER BY SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17)";
			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId);
			query.setParameter("orderNumber", circuit.getOrderNumber());
			List<Object[]> resultList = query.getResultList();
			if(resultList != null && resultList.size() > 0) {
				for (int i = 0; i < resultList.size() ; i++) {
					Object[] resu = (Object[]) resultList.get(i);
					if (resu != null && resu[0] != null) {
						result = (String) resu[0];
						break;
					}
				}
			}
		}
		return result;
	}

	public String getFqdnIPVPN(Circuit circuit, String circuitId) {
		String result = "";
		if(circuitId != null && !"".equals(circuitId) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber())) {
			String sql = "select b.DEVICE_ID " +
					"from AMN.IE_OHS_CONTRACT a, AMN.IE_OHS_IP_DATA_ORDER b " +
					"where a.CIRCUIT_REFERENCE_5D = b.CIRCUIT_REFERENCE and a.CONTRACT_NO = b.CONTRACT_NO and a.CONTRACT_NO = :orderNumber and SUBSTR(a.CIRCUIT_REFERENCE_5D,1,17) = :circuitID ORDER BY a.CIRCUIT_REFERENCE_5D";
			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuitId);
			query.setParameter("orderNumber", circuit.getOrderNumber());
			List<String> resultList = query.getResultList();
			if(resultList != null && resultList.size() > 0) {
				for (int i = 0; i < resultList.size() ; i++) {
					String resu = (String) resultList.get(i);
					if (resu != null) {
						result = resu;
						break;
					}
				}
			}
		}
		return result;
	}

	public String getNetworkObjectName(String instId) {
		String result = "";
		String sql = "Select NETWORK_OBJECT_NAME from AMN.IE_EQUIP_INST where Equip_Inst_Id = :instId";
		Query query = em.createNativeQuery(sql);
		query.setParameter("instId", instId);
		List<String> resultList = query.getResultList();
		if(resultList != null && resultList.size() > 0) {
			for (int i = 0; i < resultList.size() ; i++) {
				String resu = (String) resultList.get(i);
				if (resu != null) {
					result = resu;
					break;
				}
			}
		}
		return result;
	}

	public String[] getNetworkObject(String instId) {
		String sql = "Select Network_Object, IP_ADDRESS_MGMT, IP_ADDRESS_LAN from AMN.IE_EQUIP_INST where Equip_Inst_Id = :instId";
		Query query = em.createNativeQuery(sql);
		query.setParameter("instId", instId);
		List<Object[]> resultList = query.getResultList();
		if(resultList != null && resultList.size() > 0) {
			for(Object[] o : resultList) {
				String networkObject = (String)o[0] != null ? (String)o[0] : "";
				String addressMgmt = (String)o[1] != null ? (String)o[1] : "";
				String addressLan = (String)o[2] != null ? (String)o[2] : "";
				String result[] = {networkObject, addressMgmt, addressLan};
				return result;
			}
		}
		return null;
	}
}
