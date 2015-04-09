package com.colt.ws.biz;

public enum ProductType {

	HSS("HSS"),
	LANLINK("LANLINK"),
	IPVPN("IPVPN"),
	CPE_SOLUTIONS("CPE SOLUTIONS"),
	IP_ACCESS("IP ACCESS");
	private final String value;

	ProductType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ProductType fromValue(String v) {
		for (ProductType c: ProductType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
