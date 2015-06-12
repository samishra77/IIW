package com.colt.jmockit.test;

import com.colt.apt.business.Device;
import com.colt.apt.business.DeviceAttribute;
import com.colt.apt.business.DeviceType;
import com.colt.apt.business.Equipment;
import com.colt.apt.business.Group;
import com.colt.apt.business.ServiceSearch;
import com.colt.apt.business.User;
import com.colt.apt.business.search.SearchCtrl;
import com.colt.apt.business.search.SearchResults;
import com.colt.common.aptcache.IDeviceDAO;

public class DeviceDAO implements IDeviceDAO {

	@Override
	public void delete(User arg0, String arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(User arg0, String arg1, String arg2) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasTrunkGroup(String arg0) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Device insert(User arg0, Device arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device insert(User arg0, Device arg1, String arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device insertIntoTrunk(User arg0, Device arg1, String arg2)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllASsEquipTypeInIdList(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllCPEsEquipTypeInIdList(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllDevices(User arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllDevicesBySiteAndCustomerId(User arg0,
			String arg1, String arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllDevicesBySiteId(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllDevicesInIdList(User arg0, String[] arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllDevicesInNameList(User arg0, String[] arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllPEsEquipTypeInIdList(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveAllWDMsEquipTypeInIdList(User arg0, String arg1,
			String arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveByDeviceTypeAndEquipTypeIDInCO(User arg0,
			String arg1, long arg2, String arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveByDeviceTypeAndEquipTypeIDInCOs(User arg0,
			String arg1, long arg2, String arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveCPEDevicesByCustomerOCN(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device retrieveDeviceById(User arg0, String arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeviceType retrieveDeviceTypeById(String arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveDevicesByAddress(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveDevicesByAddress(User arg0, String arg1, int arg2)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveDevicesByGroupIds(User arg0, String[] arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveDevicesByName(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveDevicesByName(User arg0, String arg1, int arg2)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveDevicesByType(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveERXDevices(User arg0, String arg1, Equipment[] arg2)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group[] retrieveGroupedDevicesByGroupIds(User arg0, String[] arg1,
			String[] arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveHubVpnDevicesByServiceId(User arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveIntermCPEsEquipTypeInIdList(User arg0, String arg1,
			boolean arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveVpnDevicesBySiteAndCustomerIdInAllServices(
			User arg0, String arg1, String arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device[] retrieveVpnDevicesBySiteAndServiceIdInService(User arg0,
			String arg1, String arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResults searchDevicesByAttributes(User arg0, SearchCtrl arg1,
			ServiceSearch[] arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResults searchDevicesBySyncStatus(User arg0, SearchCtrl arg1,
			Device arg2, String arg3, String[] arg4, String[] arg5,
			String[] arg6, DeviceAttribute[] arg7, String[] arg8)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResults searchDevicesBySyncStatusWithCO(User arg0,
			SearchCtrl arg1, Device arg2, String arg3, String[] arg4,
			String[] arg5, String[] arg6, String[] arg7,
			DeviceAttribute[] arg8, String[] arg9) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(User arg0, Device arg1) throws Exception {
		// TODO Auto-generated method stub

	}

}
