package com.colt.util;

import java.util.Calendar;
import java.util.Date;

public class PamStatusCalculator {
	public static final Date IP_ACCESS_PAM_DATE = getDate(2014,5,30); //30-JUN-14
	public static enum SERVICE_TYPE { 
		IP_ACCESS {
			@Override
			public String toString() {
				return "IP ACCESS";
			}
		},
		VPN_CORPORATE_PLUS {
			@Override
			public String toString() {
				return "VPN CORPORATE PLUS";
			}
		},
		CPE_SOLUTIONS {
			@Override
			public String toString() {
				return "CPE SOLUTIONS";
			}
		},
		IPUNKNOWN {
			@Override
			public String toString() {
				return "IPUNKNOWN";
			}
		},
		OTHER{
			@Override
			public String toString() {
				return "OTHER";
			}
		},
		NOTSET {
			@Override
			public String toString() {
				return "NOTSET";
			}
		};

		public static SERVICE_TYPE fromString(String text) {
			if (text != null) {
				for (SERVICE_TYPE b : SERVICE_TYPE.values()) {
					if (text.equalsIgnoreCase(b.name())) {
						return b;
					}
				}
				return SERVICE_TYPE.OTHER;
			}
			return null;
		}

	};

	public static enum MANAGEMENT {
		MANAGED {
			@Override
			public String toString() {
				return "MANAGED";
			}
		},
		UNMANAGED {
			@Override
			public String toString() {
				return "UNMANAGED";
			}
		},
		NOTSET {
			@Override
			public String toString() {
				return "NOTSET";
			}
		};

		public static MANAGEMENT fromString(String text) {
			if (text != null) {
				for (MANAGEMENT b : MANAGEMENT.values()) {
					if (text.equalsIgnoreCase(b.name())) {
						return b;
					}
				}
				return MANAGEMENT.NOTSET;
			}
			return null;
		}
	};

	public static enum PAM_STATUS {
		ENABLED {
			@Override
			public String toString() {
				return "Enabled";
			}
		},
		DISABLED {
			@Override
			public String toString() {
				return "Disabled";
			}
		},	
		NOTSET {
			@Override
			public String toString() {
				return "NOTSET";
			}
		},
		UNKNOWN {
			@Override
			public String toString() {
				return "Unknown";
			}
		};

		public static PAM_STATUS fromString(String text) {
			if (text != null) {
				for (PAM_STATUS b : PAM_STATUS.values()) {
					if (text.equalsIgnoreCase(b.name())) {
						return b;
					}
				}
				return PAM_STATUS.UNKNOWN;
			}
			return null;
		}
	};

	public PamStatusCalculator() {}


	/** 
	 *	Calculates AMN SLM (PAM) status based on XNG data. Arguments can be null or empty strings. Arguments are assumed to be sourced from AMN Cache without conversion.
	 * @param category IE_CIRC_PATH_INST.TYPE
	 * @param serviceMenu IE_CIRC_PATH_INST.SERVICE_MENU
	 * @param productName IE_CIRC_PATH_INST.PRODUCTNAME
	 * @param  pam IE_CIRC_PATH_INST.MONITORING
	 * @param inService IE_CIRC_PATH_INST.IN_SERVICE
	 * @return perceived PAM status, one of the ENABLED, DISABLED or UNKNOWN
	 */
	public static String getPamEnabledSprint1(final String category, final String serviceMenu, final String productName, String pam, Date inService) {
		String[] arrServiceType = getServiceType(category, serviceMenu, productName);

		PAM_STATUS pamStatus = (pam == null || pam.trim().equals("")) ? PAM_STATUS.NOTSET : PAM_STATUS.fromString(pam.replaceAll(" ","_").toUpperCase());
		MANAGEMENT  management = MANAGEMENT.fromString(arrServiceType[1].replaceAll(" ","_").toUpperCase());
		SERVICE_TYPE serviceType = SERVICE_TYPE.fromString(arrServiceType[0].replaceAll(" ","_").toUpperCase());
		inService = inService == null ? new Date(0): inService;
		pam = pam == null ? "": pam;

		if (pam.equals("ENABLED")) {
			pamStatus = PAM_STATUS.ENABLED;
		}
		if (pam.equals("DISABLED")) {
			pamStatus = PAM_STATUS.DISABLED;
		}

		if (PAM_STATUS.NOTSET == pamStatus) {
			switch (serviceType) {
			case CPE_SOLUTIONS: case VPN_CORPORATE_PLUS:
				pamStatus = PAM_STATUS.ENABLED;
				break;
			case IP_ACCESS:
				if (MANAGEMENT.MANAGED == management && inService.after(IP_ACCESS_PAM_DATE)) {
					pamStatus = PAM_STATUS.ENABLED;
				} else if (MANAGEMENT.MANAGED == management && inService.before(IP_ACCESS_PAM_DATE)) {
					pamStatus = PAM_STATUS.UNKNOWN;
				} else if (MANAGEMENT.UNMANAGED == management) {
					pamStatus = PAM_STATUS.DISABLED;
				}
				break;
			default:
				pamStatus = PAM_STATUS.DISABLED;
			}
		}

		return pamStatus.toString();
	}

	/**
	 * Calculates service type and managmenet option for getPamEnabled function. 
	 * Management option is only calculated for IP Access service.
	 * @param category IE_CIRC_PATH_INST.TYPE
	 * @param serviceMenu IE_CIRC_PATH_INST.SERVICE_MENU
	 * @param productName IE_CIRC_PATH_INST.PRODUCTNAME
	 * @return Two element array with category at index 0 and management option at index 1.
	 */
	public static String[] getServiceType(String category, String serviceMenu, String productName) {
		String ret[] = { "", "" };

		category = category == null ? "" : category.toUpperCase();
		serviceMenu = serviceMenu == null ? "" : serviceMenu.toUpperCase();
		productName = productName == null ? "" : productName.toUpperCase();

		if (serviceMenu.contains("CPE SOLUTIONS") && category.equals("ACCESS IP MPLS")) ret[0] = SERVICE_TYPE.CPE_SOLUTIONS.toString();
		if (serviceMenu.contains("VPN CORPORATE PLUS") && category.equals("ACCESS IP MPLS")) ret[0] = SERVICE_TYPE.VPN_CORPORATE_PLUS.toString();
		if (!ret[0].isEmpty()) return ret;

		if (productName.contains("CPE SOLUTIONS") && category.equals("ACCESS IP MPLS")) ret[0] = SERVICE_TYPE.CPE_SOLUTIONS.toString();
		if (productName.contains("VPN CORPORATE PLUS") && category.equals("ACCESS IP MPLS")) ret[0] = SERVICE_TYPE.VPN_CORPORATE_PLUS.toString();
		if (!ret[0].isEmpty()) return ret;

		if (category.contains("ACCESS IP MPLS")) ret[0] = SERVICE_TYPE.IPUNKNOWN.toString();
		if (!ret[0].isEmpty()) return ret;
		// filtered out CPE Sol and VPN Corp+

		if (productName.contains("IP ACCESS") || serviceMenu.contains("IP ACCESS")) 
			ret[0] = SERVICE_TYPE.IP_ACCESS.toString();

		if (!ret[0].isEmpty()) {
			// this is IP ACCESS by productName
			if (productName.contains("MANAGED CPE") ) {
				// has details about management, could be managed or unamanged
				if (productName.contains("UNMANAGED CPE")) {
					ret[1] = MANAGEMENT.UNMANAGED.toString();
				} else {
					ret[1] = MANAGEMENT.MANAGED.toString();
				}
				return ret;
			} else {
				// does not contain info about managmenet
				ret[1] = MANAGEMENT.UNMANAGED.toString();
			}
			return ret;
		}

		// fallback to category to recognise possible IP access service
		if (category.equals("ACCESS IP")) {
			// only category exists
			ret[0] = SERVICE_TYPE.IPUNKNOWN.toString();
			return ret;
		}
		// filtered out possible IP ACCESS

		// any other service
		ret[0] = SERVICE_TYPE.OTHER.toString();
		return ret;
	}

	/**
	 * Utility function to overcome deprecated Date(int year, int month, int day)� constructor warnings.
	 * Creates date with last second of the day i.e. 23:59:59
	 * @param year Four digit year
	 * @param month Month index 0 based i.e. JAN=0, FEB=1, etc
	 * @param day Calendar day index 9 based i.e. First day in month=0
	 */
	public static Date getDate(int year, int month, int day) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, day);
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 59);

		return date.getTime();
	}
}