package com.colt.adapters.l2;



public class FactoryAdapter {

	public final static String VENDOR_ACCEDIAN = "Accedian";
	public final static String VENDOR_ACTELIS = "Actelis";
	public final static String VENDOR_OVERTURE = "Overture";
	public final static String VENDOR_ATRICA = "Atrica";
	public final static String VENDOR_ACCEDIAN_LABEL1 = "AMO-";
	public final static String VENDOR_ACCEDIAN_LABEL2 = "AMN-";
	public final static String VENDOR_ACCEDIAN_LABEL3 = "FDX";
	public final static String VENDOR_OVER_LABEL1 = "ISG-";
	public final static String VENDOR_OVER_LABEL2 = "Overture";
	
	public enum ACCEDIAN_LABELS {
		AMO,
		AMN,
		FDX
	};
	
	public enum OVERTURE_LABELS {
		ISG,
		Overture
	};

	public Adapter getAdapter(String vendor) {
		Adapter adapter = null;
		if(VENDOR_ACCEDIAN.equalsIgnoreCase(vendor)) {
			adapter = new AccedianAdapter();
		} else if(VENDOR_ACTELIS.equalsIgnoreCase(vendor)) {
			adapter = new ActelisAdapter();
		} else if(VENDOR_OVERTURE.equalsIgnoreCase(vendor)) {
			adapter = new OvertureAdapter();
		} else if(VENDOR_ATRICA.equalsIgnoreCase(vendor)) {
			adapter = new AspenAdapter();
		}
		return adapter;
	}
}
