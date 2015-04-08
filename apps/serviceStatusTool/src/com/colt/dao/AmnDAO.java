package com.colt.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.colt.ws.biz.Circuit;

public class AmnDAO extends DAO {

	public AmnDAO(EntityManager em) {
		super(em);
	}

	public List<Circuit> retrieveCircuits() {

//		List<Circuit> modelList = new ArrayList<Circuit>();
//		String sql = "select unique i.circ_path_inst_id, i.circ_path_hum_id, i.status, i.order_num, i.type, i.customer_id, i.A_Side_Site_ID, i.Z_Side_Site_ID, " +
//				"j.SITE_HUM_ID as A_SITE_HUM_ID, j.City as A_City, k.SITE_HUM_ID as Z_SITE_HUM_ID, k.City as Z_City " +
//				"from AMN.ie_circ_path_inst i, AMN.ie_site_inst j, AMN.ie_site_inst k " +
//				"where i.circ_path_hum_id like 'Service%' and i.order_num like 'Order%' and i.customer_id like '%Customer%' and " + "i.status='Live' " +
//				"and ((j.SITE_HUM_ID like '%Site1 Address%' and j.City like '%Site1 City%') or (k.SITE_HUM_ID like '%Site1 Address%' and k.City like '%Site1 City%')) and " +
//				"((j.SITE_HUM_ID like '%Site2 Address%' and j.City like '%Site2 City%') or (k.SITE_HUM_ID like '%Site2 Address%' and k.City like '%Site2 City%')) and " +
//				"NOT REGEXP_LIKE (i.circ_path_hum_id, '-P|-AP') and i.A_Side_Site_ID = j.Site_Inst_ID and i.Z_Side_Site_ID = k.Site_Inst_ID order by i.circ_path_inst_id";
//		
//		Query query = em.createNativeQuery(sql);
//		List<Object[]> resutlList = query.getResultList();
//		if(resutlList != null && resutlList.size() > 0) {
//			Circuit circuit = null;
//			for(Object[] o : resutlList) {
//				circuit = new Circuit();
//				circuit.setCircuitID((String)o[0] != null ? (String)o[0] : "");
//				circuit.setOrderNumber((String)o[0] != null ? (String)o[0] : "");
//				circuit.setProductType((String)o[0] != null ? (String)o[0] : "");
//				modelList.add(circuit);
//			}
//		}

		List<Circuit> modelList = new ArrayList<Circuit>();
		Circuit circuit = new Circuit();
		circuit.setCircuitID("HAM/HAM/LE-111805");
		circuit.setOrderNumber("150201585");
		circuit.setProductType("LANLINK");
		modelList.add(circuit);
		
		circuit = new Circuit();
		circuit.setCircuitID("HAM/HAM/LE-111805");
		circuit.setOrderNumber("150201585");
		circuit.setProductType("LANLINK");
		modelList.add(circuit);
		
		circuit = new Circuit();
		circuit.setCircuitID("IPC04060761BRU9");
		circuit.setOrderNumber("150201585");
		circuit.setProductType("ACCESS IP MPLS");
		modelList.add(circuit);

		return modelList;
	}
}
