package com.colt.adapters;


public class FactoryAdapter {

	public final static String VENDOR_CISCO = "Cisco";
	private final String CISCO_IOS = "ios";
	private final String CISCO_XR = "xr";
	private final String CISCO_XE = "xe";
	public final static String VENDOR_JUNIPER = "Juniper";
	private final String JUNIPER_JUNOS = "junos";
	private final String JUNIPER_SCREENOS = "screenos";
	public final static String VENDOR_HUAWEI = "Huawei";
//	private final String HUAWEI_ = "";
//	private final String HUAWEI_ = "";
	

	public Adapter getAdapter(String vendor, String os) {
		Adapter adapter = null;
		if(VENDOR_CISCO.equalsIgnoreCase(vendor) && CISCO_IOS.equalsIgnoreCase(os)) {
			adapter = new CiscoIOSAdapter();
		} else if(VENDOR_CISCO.equalsIgnoreCase(vendor) && (CISCO_XR.equalsIgnoreCase(os) || CISCO_XE.equalsIgnoreCase(os))) {
			adapter = new CiscoXRAdapter();
		} else if(VENDOR_JUNIPER.equalsIgnoreCase(vendor) && (JUNIPER_JUNOS.equalsIgnoreCase(os) || JUNIPER_SCREENOS.equalsIgnoreCase(os))) {
			adapter = new JunosAdapter();
		} else if(VENDOR_HUAWEI.equalsIgnoreCase(vendor)) {
			adapter = new HuaweiAdapter();
		}
		return adapter;
	}
}
