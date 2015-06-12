package com.colt.jmockit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.AgentUtil;

public class Util {

	private Log log = LogFactory.getLog(Util.class);

	public List<String> readFileTestOutPut(String fileName) {
		List<String> outputList = new ArrayList<String>();
		if(fileName != null && !"".equals(fileName)) {
			try {
				InputStream inputStreamFile = AgentUtil.class.getResourceAsStream("/conf/" + fileName);
				BufferedReader reader = new BufferedReader( new InputStreamReader(inputStreamFile));
				String line = null;
				while((line = reader.readLine()) != null) {
					outputList.add(line.trim());
				}
			} catch (Exception e) {
				log.error(e,e);
			}
		}
		return outputList;
	}

	public String readFileToString(String fileName) {
		String fileString = null;
		if(fileName != null && !"".equals(fileName)) {
			File file = new File(AgentUtil.class.getResource("/conf/" + fileName).getFile());
			if(file != null && file.isFile()) {
				try {
					fileString = FileUtils.readFileToString(file);
				} catch (Exception e) {
					log.error(e,e);
				}
			}
		}
		return fileString;
	}
}
