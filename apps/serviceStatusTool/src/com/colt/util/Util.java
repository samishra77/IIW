package com.colt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.colt.ws.biz.ProductType;

public class Util {

	public String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}     
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public String getValue(String result,String startTag,String endTag) {
		if(result != null && result.contains(startTag)) {
			String a[] = result.split(startTag);	       
			String results[] = a[1].split(endTag);
			return results[0];
		} else {
			return null;
		}
	}

	public String getProductType (String product) {
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
