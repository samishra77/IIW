package com.colt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AgentUtil {

	private static Log log = LogFactory.getLog(AgentUtil.class);
	public final static String UP = "UP";
	public final static String DOWN = "DOWN";

	public static boolean verifyItemInList(String[] listArray, String item, StringBuffer xmlVendorFound) {
		boolean resp = false;
		if(listArray != null && listArray.length > 0 && item != null && !"".equals(item)) {
			for(String itemArray : listArray) {
				if(itemArray.equalsIgnoreCase(item)) {
					xmlVendorFound.append(itemArray);
					resp = true;
					break;
				}
			}
		}
		return resp;
	}

	public static void main(String[] args) {
		parseXML("/home/daniel/workspace/sst/apps/aopworkflow/src/com/colt/util/agentValidators.xml", "Cisco");
	}

	public static List<String> parseXML(String xmlPath, String value) {
		List<String> resp = null;
		try {
			if(xmlPath != null && !"".equals(xmlPath) && value != null && !"".equals(value)) {
				File file = new File(xmlPath);
				if(file != null && file.isFile()) {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(file);
					doc.getDocumentElement().normalize();
					String vendorsAttrib = doc.getDocumentElement().getAttribute("vendors");
					vendorsAttrib = vendorsAttrib.trim();
					if(vendorsAttrib != null && !"".equals(vendorsAttrib)) {
						String[] vendorsAttributes = vendorsAttrib.split(",");
						StringBuffer xmlVendorFound = new StringBuffer();
						if(vendorsAttributes != null && vendorsAttributes.length > 0 && verifyItemInList(vendorsAttributes, value, xmlVendorFound)) {
							if(xmlVendorFound.length() > 0) {
								NodeList nodeVendorList = doc.getElementsByTagName(xmlVendorFound.toString());
								List<String> modelFileListByVendor = find(nodeVendorList);
								if(!modelFileListByVendor.isEmpty()) {
									resp = new ArrayList<String>();
									for(String fileName : modelFileListByVendor) {
										String pathFile = AgentConfig.getDefaultInstance().getProperty("modelPath").trim();
										try {
											resp.addAll(retriveModelListByFile(pathFile, fileName));
										} catch (Exception e) {
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return resp;
	}

	/**
	 * 
	 * @param pathFile
	 * @param fileName
	 * @return model list of all Vendor files
	 */
	private static List<String> retriveModelListByFile(String pathFile, String fileName) {
		List<String> resp = null;
		if(pathFile != null && !"".equals(pathFile) && fileName != null && !"".equals(fileName)) {
			try {
				File file = new File(pathFile, fileName);
				if(file != null && file.isFile()) {
					resp = new ArrayList<String>();
					BufferedReader reader = new BufferedReader( new FileReader (file));
					String line = null;
					while((line = reader.readLine()) != null) {
						resp.add(line);
					}
				}
			} catch (Exception e) {
				log.error(e,e);
			}
		}
		return resp;
	}

	/**
	 * 
	 * @param nodeList
	 * @return file List by vendor
	 */
	private static List<String> find(NodeList nodeList) {
		List<String> resp = new ArrayList<String>();
		if(nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if(node.hasChildNodes()) {
					resp.addAll(find(node.getChildNodes()));
				} else {
					resp.add(node.getTextContent());
				}
			}
		}
		return resp;
	}

	public static List<String> runLocalCommand(String cmd) throws Exception {
		log.debug(cmd);
		List<String> output = null;
		if (cmd != null && !"".equals(cmd)) {
			output = new ArrayList<String>();
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				output.add(line);
			}
			p.waitFor();
		}
		if(output != null && !output.isEmpty()) {
			StringBuffer out = new StringBuffer();
			for(String line : output) {
				out.append(line);
				out.append("\n");
			}
			log.debug(out);
		}
		return output;
	}
}

