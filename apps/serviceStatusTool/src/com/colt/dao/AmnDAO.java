package com.colt.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.colt.util.Util;
import com.colt.ws.biz.Circuit;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.Search;

public class AmnDAO extends DAO {

	private final int maxResult = 31;
	
	public AmnDAO(EntityManager em) {
		super(em);
	}

	public Response retrieveCircuits(Search search) {
		Response response = new Response();
		
		String sql = "select unique i.circ_path_inst_id, i.circ_path_hum_id as CIRCUIT_ID, i.order_num as ORDER_NUMBER, i.customer_id as CUSTOMER, i.service_menu as PRODUC_TYPE, " +
				"i.A_Side_Site_ID as ASIDE_SITE, i.Z_Side_Site_ID as ZSIDE_SITE, i.status, j.SITE_HUM_ID as A_SITE_HUM_ID, j.City as A_City, k.SITE_HUM_ID as Z_SITE_HUM_ID, k.City as Z_City " +
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
					sql+= "i.customer_id like :customer and ((j.SITE_HUM_ID like :site1Address and j.City like :site1City) or (k.SITE_HUM_ID like :site1Address and k.City like :site1City)) and ";
				} else if( (search.getCustomer() != null && !"".equals(search.getCustomer())) &&
						( search.getAddress2() != null && !"".equals(search.getAddress2()) && search.getCity2() != null && !"".equals(search.getCity2()) ) ) {
					sql+= "i.customer_id like :customer and ((j.SITE_HUM_ID like :site2Address and j.City like :site2City) or (k.SITE_HUM_ID like :site2Address and k.City like :site2City)) and ";
				}
				sql+= "NOT REGEXP_LIKE (i.circ_path_hum_id, '-P|-AP') and i.A_Side_Site_ID = j.Site_Inst_ID and i.Z_Side_Site_ID = k.Site_Inst_ID order by i.circ_path_inst_id";
		
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
			query.setParameter("site1Address", 	search.getAddress());
			query.setParameter("site1City", 	search.getCity());
		}
		if( (search.getCustomer() != null && !"".equals(search.getCustomer())) &&
				( search.getAddress2() != null && !"".equals(search.getAddress2()) && search.getCity2() != null && !"".equals(search.getCity2()) ) ) {
			query.setParameter("customer", 		search.getCustomer());
			query.setParameter("site2Address", 	search.getAddress2());
			query.setParameter("site2City", 	search.getCity2());
		}

		List<Circuit> modelList = new ArrayList<Circuit>();
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
		} else if(resutlList != null && resutlList.size() < maxResult) {
			Circuit circuit = null;
			for(Object[] o : resutlList) {
				circuit = new Circuit();
				circuit.setCircPathInstID(o[0] != null ? ((BigDecimal)o[0]).toString() : "");
				circuit.setCircuitID((String)o[1] != null ? (String)o[1] : "");
				circuit.setOrderNumber((String)o[2] != null ? (String)o[2] : "");
				circuit.setCustomer((String)o[3] != null ? (String)o[3] : "");
				circuit.setProductType(new Util().getProductType((o[4] != null) ? (String)o[4] : ""));
				circuit.setaSideSite(o[5] != null ? ((BigDecimal)o[5]).toString() : "");
				circuit.setzSideSite(o[6] != null ? ((BigDecimal)o[6]).toString() : "");
				modelList.add(circuit);
			}
		}
		if(!modelList.isEmpty()) {
			response.setResult(modelList);
		}

		return response;
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
		} else {
			response.setErrorCode(Response.CODE_NOTONE);
			response.setErrorMsg("More than one result found.");
			response.setStatus(Response.FAIL);
		}
		response.setResult(circuit);
		return response;
	}

}
