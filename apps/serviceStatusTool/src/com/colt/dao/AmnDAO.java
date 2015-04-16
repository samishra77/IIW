package com.colt.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

	private final int maxResult = 30;
	
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
			sql+= " and i.circ_path_hum_id like ? and i.order_num like ? ";
		} else if( search.getOrder() != null && !"".equals(search.getOrder()) ) {
			sql+= "and i.order_num like ? ";
		} else if( search.getService() != null && !"".equals(search.getService()) ) {
			sql+= "and i.circ_path_hum_id like ? ";
		} else if(search.getCustomer() != null && !"".equals(search.getCustomer())) {
			sql+= "and i.customer_id like ? ";
			if( (search.getAddress() != null && !"".equals(search.getAddress())) || (search.getCity() != null && !"".equals(search.getCity())) ) {
				sql+= " and (( ";
				if( search.getAddress() != null && !"".equals(search.getAddress()) )  {
					sql+= " (REGEXP_LIKE(j.SITE_HUM_ID, ?, 'i')  or REGEXP_LIKE(j.ADDRESS, ?, 'i')) ";
				}
				if( search.getCity() != null && !"".equals(search.getCity()) )  {
					if(search.getAddress() != null && !"".equals(search.getAddress()) ) {
						sql+= " and ";
					}
					sql+= " j.City like ? ";
				}
				sql+= " ) or ( "; 
				if( search.getAddress() != null && !"".equals(search.getAddress()) )  {
					sql+= "(REGEXP_LIKE(k.SITE_HUM_ID, ?, 'i') or REGEXP_LIKE(k.ADDRESS, ?, 'i')) ";
				}
				if( search.getCity() != null && !"".equals(search.getCity()) )  {
					if(search.getAddress() != null && !"".equals(search.getAddress()) ) {
						sql+= " and ";
					}
					sql+= " k.City like ? ";
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
					sql+= " j.City like ? ";
				}
				sql+= " ) or ( "; 
				if( search.getAddress2() != null && !"".equals(search.getAddress2()) )  {
					sql+= "(REGEXP_LIKE(k.SITE_HUM_ID, ?, 'i') or REGEXP_LIKE(k.ADDRESS, ?, 'i')) ";
				}
				if( search.getCity2() != null && !"".equals(search.getCity2()) )  {
					if(search.getAddress2() != null && !"".equals(search.getAddress2()) ) {
						sql+= " and ";
					}
					sql+= " k.City like ? ";
				}
				sql+= " )) ";
			}
		}
		sql+= " and NOT REGEXP_LIKE (i.circ_path_hum_id, '-P|-AP') and i.A_Side_Site_ID = j.Site_Inst_ID and i.Z_Side_Site_ID = k.Site_Inst_ID";

		Connection conn = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			prepStmt = conn.prepareStatement(sql);
			int idx = 1;
			if( search.getService() != null && !"".equals(search.getService()) ) {
				prepStmt.setString(idx++, search.getService());
			}
			if( search.getOrder() != null && !"".equals(search.getOrder()) ) {
				prepStmt.setString(idx++, search.getOrder());
			}
			if( search.getCustomer() != null && !"".equals(search.getCustomer())) {
				prepStmt.setString(idx++, search.getCustomer());
			}

			if( search.getAddress() != null && !"".equals(search.getAddress())) {
				prepStmt.setString(idx++, site);
				prepStmt.setString(idx++, site);
			}
			if( search.getCity() != null && !"".equals(search.getCity())) {
				prepStmt.setString(idx++, search.getCity());
			}
			if( search.getAddress() != null && !"".equals(search.getAddress())) {
				prepStmt.setString(idx++, site);
				prepStmt.setString(idx++, site);
			}
			if( search.getCity() != null && !"".equals(search.getCity())) {
				prepStmt.setString(idx++, search.getCity());
			}

			if( search.getAddress2() != null && !"".equals(search.getAddress2())) {
				prepStmt.setString(idx++, site2);
				prepStmt.setString(idx++, site2);
			}
			if( search.getCity2() != null && !"".equals(search.getCity2())) {
				prepStmt.setString(idx++, search.getCity2());
			}
			if( search.getAddress2() != null && !"".equals(search.getAddress2())) {
				prepStmt.setString(idx++, site2);
				prepStmt.setString(idx++, site2);
			}
			if( search.getCity2() != null && !"".equals(search.getCity2())) {
				prepStmt.setString(idx++, search.getCity2());
			}

			List<Circuit> modelList = new ArrayList<Circuit>();
			HashMap<String, Circuit> circPathInstIDCircuit = new HashMap<String, Circuit>();

			rs = prepStmt.executeQuery();
			Circuit circuit = null;
			List<String> circuitIDList = new ArrayList<String>();
			while (rs.next()) {
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
					response.setErrorMsg("Too Many Results.");
					response.setStatus(Response.FAIL);
					break;
				}
			}

			if(circPathInstIDCircuit.size() == 0) {
				response.setErrorCode(Response.CODE_EMPTY);
				response.setErrorMsg("No result found.");
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
				"STATUS, BANDWIDTH, TYPE as CATEGORY, CUSTOMER_ID, TRUNK_GROUP, MANAGEMENTTEAM, MONITORING, OSMORDERNO " +
				"FROM IE_CIRC_PATH_INST " +
				"WHERE CIRC_PATH_INST_ID =  :circPathInstID";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("circPathInstID", circPathInstID );

		Circuit circuit = new Circuit();
		List<Object[]> resutlList = query.getResultList();
		if(resutlList == null || resutlList.isEmpty()) {
			response.setErrorCode(Response.CODE_EMPTY);
			response.setErrorMsg("No result found.");
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
				circuit.setPerformanceMonitoring((String)o[11] != null ? (String)o[11] : "");
				response.setResult(circuit);
			}
			if(circuit.getOsmOrderNO() != null && !"".equals(circuit.getOsmOrderNO())) {
				fetchFromSiebelOrder(circuit);
			} else {
				fetchFromOHSContractRelatedTables(circuit);
			}
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
			if(resutlList != null && resutlList.size() > 0) {
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
		} else {
			getCircuitOthers(circuit);
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
			if(resutlList != null && resutlList.size() > 0) {
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
			if(resutlList != null && resutlList.size() > 0) {
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
			if(resutlList != null && resutlList.size() > 0) {
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
			if(resutlList != null && resutlList.size() > 0) {
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
		if(circuit != null && circuit.getCircuitID() != null && !"".equals(circuit.getCircuitID()) && circuit.getOrderNumber() != null && !"".equals(circuit.getOrderNumber()) ) {
			String sql = "select LEGAL_CUSTOMER, PRODUCT_NAME, OCN " +
					"from AMN.IE_OHS_CONTRACT " +
					"where CONTRACT_NO = :orderNumber and CIRCUIT_REFERENCE_5D = :circuitID and CURR_REVISION = 'YES'";

			Query query = em.createNativeQuery(sql);
			query.setParameter("circuitID", circuit.getCircuitID());
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

}
