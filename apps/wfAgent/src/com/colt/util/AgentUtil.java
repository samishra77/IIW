package com.colt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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

//	public static void main(String[] args) {
//		validateVendorModel("/home/daniel/workspace/sst/apps/wfAgent/src/com/colt/util/agentValidators.xml", "Cisco", "2901");
//	}

	public static String validateVendorModel(String xmlPath, String vendor, String model) {
		String resp = null;
		try {
			if(xmlPath != null && !"".equals(xmlPath) && vendor != null && !"".equals(vendor) && model != null && !"".equals(model)) {
				File file = new File(xmlPath);
				if(file != null && file.isFile()) {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(file);
					doc.getDocumentElement().normalize();
					XPathFactory factory = XPathFactory.newInstance();
					XPath xpath = factory.newXPath();

					String[] expression = null;
					if("Cisco".equalsIgnoreCase(vendor)) {
						expression = new String[] {"L3Devices/Cisco/IOSRouters/IOSDevice", "L3Devices/Cisco/IOSRouters/XEDevice", "L3Devices/Cisco/IOSRouters/XRDevice"};
					} else if("Juniper".equalsIgnoreCase(vendor)) {
						expression = new String[] {"L3Devices/Juniper/JunOSRouter"};
					} else if("Huawei".equalsIgnoreCase(vendor)) {
						resp = "huaweios";
					}

					Node node = null;
					if(expression != null && expression.length > 0) {
						for(String e : expression) {
							node = (Node) xpath.compile(e).evaluate(doc, XPathConstants.NODE);
							String fileName = node.getTextContent();
							String pathFile = AgentConfig.getDefaultInstance().getProperty("agentValidators.pathFile").trim();
							if(findOSByFile(pathFile, fileName, model)) {
								if("Device".contains(node.getNodeName())) {
									resp = node.getNodeName().replace("Device", "").toLowerCase();
								} else if("Router".contains(node.getNodeName())) {
									resp = node.getNodeName().replace("Router", "").toLowerCase();
								}
								resp = node.getNodeName();
								if ("IOSDevice".equals(resp)) {
									resp = "ios";
								} else if ("XEDevice".equals(resp) || "XRDevice".equals(resp)) {
									resp = "xr";
								} else if ("JunOSRouter".equals(resp)) {
									resp = "junos";
								}
								break;
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
	 * @param model
	 * @return
	 */
	private static boolean findOSByFile(String pathFile, String fileName, String model) {
		boolean found = false;
		if(pathFile != null && !"".equals(pathFile) && fileName != null && !"".equals(fileName) && model != null && !"".equals(model)) {
			try {
				File file = new File(pathFile, fileName);
				if(file != null && file.isFile()) {
					BufferedReader reader = new BufferedReader( new FileReader (file));
					String line = null;
					while((line = reader.readLine()) != null) {
						line = line.trim();
						if(model.equalsIgnoreCase(line)) {
							found = true;
							break;
						}
					}
				}
			} catch (Exception e) {
				log.error(e,e);
			}
		}
		return found;
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

