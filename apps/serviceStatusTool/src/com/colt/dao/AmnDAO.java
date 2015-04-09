package com.colt.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.colt.ws.biz.Circuit;
import com.colt.ws.biz.ProductType;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.Search;

public class AmnDAO extends DAO {

	private final int maxResult = 31;
	
	public AmnDAO(EntityManager em) {
		super(em);
	}

	public Response retrieveCircuits(Search search) {

		List<Circuit> modelList = new ArrayList<Circuit>();
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
			query.setParameter("service", search.getService()+"%");
		}
		if( search.getOrder() != null && !"".equals(search.getOrder()) ) {
			query.setParameter("order", search.getOrder()+"%");
		}
		if( (search.getCustomer() != null && !"".equals(search.getCustomer())) &&
					( search.getAddress() != null && !"".equals(search.getAddress()) && search.getCity() != null && !"".equals(search.getCity()) ) ) {
			query.setParameter("customer", 		"%"+search.getCustomer()+"%");
			query.setParameter("site1Address", 	"%"+search.getAddress()+"%");
			query.setParameter("site1City", 	"%"+search.getCity()+"%");
		}
		if( (search.getCustomer() != null && !"".equals(search.getCustomer())) &&
				( search.getAddress2() != null && !"".equals(search.getAddress2()) && search.getCity2() != null && !"".equals(search.getCity2()) ) ) {
			query.setParameter("customer", 		search.getCustomer());
			query.setParameter("site2Address", 	search.getAddress2());
			query.setParameter("site2City", 	search.getCity2());
		}

		query.setMaxResults(maxResult);
		List<Object[]> resutlList = query.getResultList();
		if(resutlList.isEmpty()) {

		} else if(resutlList.size() >= maxResult) {
			
		}
		if(resutlList != null && !resutlList.isEmpty()) {
			Circuit circuit = null;
			for(Object[] o : resutlList) {
				circuit = new Circuit();
				circuit.setCircuitID((String)o[1] != null ? (String)o[1] : "");
				circuit.setOrderNumber((String)o[2] != null ? (String)o[2] : "");
				circuit.setCustomer((String)o[3] != null ? (String)o[3] : "");
				circuit.setProductType(getProductType((o[4] != null) ? (String)o[4] : ""));
				circuit.setaSideSite((String)o[5] != null ? (String)o[5] : "");
				circuit.setzSideSite((String)o[6] != null ? (String)o[6] : "");
				modelList.add(circuit);
			}
		}

//		List<Circuit> modelList = new ArrayList<Circuit>();
//		Circuit circuit = new Circuit();
//		circuit.setCircuitID("HAM/HAM/LE-111805");
//		circuit.setOrderNumber("150201585");
//		circuit.setProductType("LANLINK");
//		modelList.add(circuit);
//		
//		circuit = new Circuit();
//		circuit.setCircuitID("HAM/HAM/LE-111805");
//		circuit.setOrderNumber("150201585");
//		circuit.setProductType("LANLINK");
//		modelList.add(circuit);
//		
//		circuit = new Circuit();
//		circuit.setCircuitID("IPC04060761BRU9");
//		circuit.setOrderNumber("150201585");
//		circuit.setProductType("ACCESS IP MPLS");
//		modelList.add(circuit);

		Response response = new Response();
		response.setResult(modelList);
		return response;
	}

	private String getProductType (String product) {
		if(product != null) {
			if(product.toUpperCase().contains("HIGH SPEED SERVICE")) {
				return ProductType.HSS.value();
			} else if(product.toUpperCase().contains("LANLINK") || product.toUpperCase().contains("ETHERNET PRIVATE")) {
				return ProductType.LANLINK.value();
			} else if(product.toUpperCase().contains("IPVPN")) {
				return ProductType.IPVPN.value();
			} else if(product.toUpperCase().contains("CPE SOLUTIONS")) {
				return ProductType.CPE_SOLUTIONS.value();
			} else if(product.toUpperCase().contains("IP ACCESS")) {
				return ProductType.IP_ACCESS.value();
			} else {
				return product;
			}
		}
		return "";
	}
}
