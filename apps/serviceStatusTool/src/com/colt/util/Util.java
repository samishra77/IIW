package com.colt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

}
