package com.colt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.colt.adapters.l2.FactoryAdapter;

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

	public static boolean verifyItemInList(String[] listArray, String item) {
		boolean resp = false;
		if(listArray != null && listArray.length > 0 && item != null && !"".equals(item)) {
			for(String itemArray : listArray) {
				if(itemArray.equalsIgnoreCase(item)) {
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

	public static String validateVendorModel(InputStream inputStreamFile, String vendor, String model) {
		String resp = null;
		try {
			if(inputStreamFile != null && vendor != null && !"".equals(vendor) && model != null && !"".equals(model)) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = null;
				doc = dBuilder.parse(inputStreamFile);
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
				} else if("Accedian".equalsIgnoreCase(vendor)) {
					expression = new String[] {"L2Devices/AccedianDevice"};
				} else if("Overture".equalsIgnoreCase(vendor)) {
					expression = new String[] {"L2Devices/OvertureDevice"};
				} else if("Actelis".equalsIgnoreCase(vendor)) {
					expression = new String[] {"L2Devices/ActelisDevice"};
				}
				Node node = null;
				if(expression != null && expression.length > 0) {
					for(String e : expression) {
						node = (Node) xpath.compile(e).evaluate(doc, XPathConstants.NODE);
						String fileName = node.getTextContent();
						String pathFile = AgentConfig.getDefaultInstance().getProperty("agentValidators.pathFile").trim();
						if(findOSByFile(pathFile, fileName, model)) {
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
		} catch (Exception e) {
			log.error(e,e);
		}
		log.debug("XNG Model: " + model + " OS: " + (resp != null ? resp : " not found!"));
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
		if(fileName != null && !"".equals(fileName) && model != null && !"".equals(model)) {
			try {
				InputStream inputStreamFile = null;
				if(pathFile == null || "".equals(pathFile) || " ".equals(pathFile)) {
					inputStreamFile = AgentUtil.class.getResourceAsStream("/conf/" + fileName);
				} else {
					File file = new File(pathFile, fileName);
					if(file != null && file.isFile()) {
						inputStreamFile = new FileInputStream(file);
					}
				}
				BufferedReader reader = new BufferedReader( new InputStreamReader(inputStreamFile));
				String line = null;
				while((line = reader.readLine()) != null) {
					line = line.trim();
					if(model.equalsIgnoreCase(line)) {
						found = true;
						break;
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

	public static String calculateWanIp(String ip) {
		String partialAddress = "";
		try {
			if(ip != null && !"".equals(ip)) {
				int index = ip.lastIndexOf(".");
				if(index > -1) {
					String lastOctet = ip.substring(index+1, ip.length());
					partialAddress = ip.substring(0, ip.lastIndexOf(".")+1);
					int octet = 0;
					octet = Integer.valueOf(lastOctet);
					if(octet > 0) {
						octet = octet - 1;
					}
					partialAddress+= octet;
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return partialAddress;
	}

	public static List<String> splitByDelimiters(String data, String delimiter) {
		List<String> splited = new ArrayList<String>();
		if(data != null && !"".equals(data)) {
			StringTokenizer st = new StringTokenizer(data.trim(), delimiter);
			while(st.hasMoreTokens()) {
				splited.add(st.nextToken().trim());
			}
		}
		return splited;
	}

	public static String processCliInterfaceNameDescription(String interfaceNameDescription) {
		String resp = null;
		if(interfaceNameDescription != null && !"".equals(interfaceNameDescription)) {
			boolean foundDigit = false;
			int index = 0;
			for(int i = 0; i < interfaceNameDescription.length(); i++) {
				if(Character.isDigit(interfaceNameDescription.charAt(i))) {
					index = i;
					foundDigit = true;
					break;
				}
			}
			if(foundDigit) {
				resp = interfaceNameDescription.substring(index, interfaceNameDescription.length());
			}
		}
		return resp;
	}

	public static String[] retrieveAliasesByVendor(String vendor) {
		List<String> aliasListResp = new ArrayList<String>();
		try {
			if(vendor != null && !"".equals(vendor)) {
				aliasListResp.add(vendor);
				String aliases = null;
				if(FactoryAdapter.VENDOR_ACCEDIAN.equalsIgnoreCase(vendor)) {
					aliases = DeviceCommand.getDefaultInstance().getProperty("vendor.accedian.aliases").trim();
				} else if(FactoryAdapter.VENDOR_ACTELIS.equalsIgnoreCase(vendor)) {
					aliases = DeviceCommand.getDefaultInstance().getProperty("vendor.actelis.aliases").trim();
				} else if(FactoryAdapter.VENDOR_OVERTURE.equalsIgnoreCase(vendor)) {
					aliases = DeviceCommand.getDefaultInstance().getProperty("vendor.overture.aliases").trim();
				}
				List<String> aliasList = null;
				if(aliases != null && !"".equals(aliases)) {
					aliasList = AgentUtil.splitByDelimiters(aliases, ", ");
				}
				if(aliasList != null && !aliasList.isEmpty()) {
					aliasListResp.addAll(aliasList);
				}
			}
		} catch (IOException e) {
			log.error(e,e);
		}
		return aliasListResp.toArray(new String[aliasListResp.size()]);
	}

	public static String verifyLineInList(String[] listArray, String line) {
		String resp = null;
		if(listArray != null && listArray.length > 0 && line != null && !"".equals(line)) {
			for(String itemArray : listArray) {
				if(line.toUpperCase().contains(itemArray.toUpperCase())) {
					resp = itemArray;
					break;
				}
			}
		}
		return resp;
	}
}

