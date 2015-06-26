package com.colt.connect;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @author Intelinet
 */

public class ConnectSSH  extends ConnectDevice {
	protected Log log;
	protected JSch jsch;
	protected SSHTools ssh;
	private String vendor = "";
	protected Channel channel=null;
	protected Session session=null;
	private String username;
	private String password;
	private String server=null;
	private int _timeout;
	protected InputStream in;
	protected PrintStream out;
	@SuppressWarnings("unused")
	private final int CONNECTTIMEOUT = 15; // *1000 = 15 seconds
	protected final int IDLETIMEOUT = 15; // default/mininum
	protected ByteArrayOutputStream outstream = new ByteArrayOutputStream();
	private int waitForMaximumRunTime = 270000; // spends maximum 270 seconds reading input
	private int waitForSleepInterval = 500;  // each sleep interval is 0.5 second
	private int waitForSleepCountMax = 180;  // number os sleep intervals in order to reach 20 seconds, which is the "idle" timeout

	/**
	 * Constructor for the TelnetDevice class
	 * @param
	 */
	public ConnectSSH() {
		log = LogFactory.getLog(this.getClass());
		log.info("Using ConnectSSH");
		jsch = new JSch();
	}

	public void connect(String server, int _timeout, String connectProtocol) throws Exception {
		try {
			this.server = server;
			this._timeout = _timeout;
		} catch (Exception e) {
			log.error(server + ": " + e.getMessage(), e);
			throw e;
		}

	}

	public String waitfor(String pattern) throws Exception {
		System.out.println("Waiting for :: " + pattern);
		if (pattern==null || "".equals(pattern)) {
			return null;
		}
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		int sleepCount = 0;
		long startTime = System.currentTimeMillis();

		StringBuilder sb = new StringBuilder();
		char ch;

		while (true) {
			if (in.available() > 0) {
				sleepCount = 0;
				ch = (char)in.read();
				//log.debug("char received: "+ch+"      (in.available():"+in.available());
				outstream.write(ch);
				sb.append( ch );
			}
			else {
				sleepCount++;
				if ( sleepCount>waitForSleepCountMax ) {
					throw new Exception("Timeout");
				}
				log.debug("waitfor() waiting for data... (sleepCount="+sleepCount+")");
				Thread.sleep(waitForSleepInterval);
			}
			if (System.currentTimeMillis()-startTime > waitForMaximumRunTime) {
				throw new Exception("Timeout");
			}
			m = p.matcher(sb.toString());
			if( m.find() ) {
				String result = sb.toString();
				log.debug(result);
				return result;
			}
		}
	}

	public void write(String word) throws Exception {
		out.println(word);
		out.flush();
	}

	public String sendCmd(String command, String nexttoken) throws Exception {
		write( command );
		return waitfor( nexttoken );

	}

	public void disconnect() {
		try{
			channel.disconnect();
			session.disconnect();
		}
		catch (Exception e) {
			System.out.println("Session already disconnected");
		}
	}

	/**
	 * @throws Exception
	 */
	private String readBuffer(String pattern) throws Exception {
		int k;
		int sleepCount = 0;
		char ch;
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		StringBuilder sb = new StringBuilder();
		long startTime = System.currentTimeMillis();
		while (true) {
			if (in.available() > 0) {
				sleepCount = 0;
				ch = (char)in.read();
				//log.debug(ch);
				outstream.write(ch);
				sb.append( ch );
			}
			else {
				sleepCount++;
				if ( sleepCount>waitForSleepCountMax ) {
					log.info("readBuffer() exceeded sleepCountMax");
					break;
				}
				log.debug("readBuffer() waiting for data... (sleepCount="+sleepCount+")");
				Thread.sleep(waitForSleepInterval);
			}
			m = p.matcher(sb.toString());
			if( m.find() ) {
				String result = sb.toString();
				log.debug(result);
				return result;
			}
		}
		String result = sb.toString();
		log.debug(result);
		return result;
	}

	public String applyCommands(String commands) throws Exception {
		return applyCommands(commands, null);
	}

	public String applyCommands(String commands, String endTag) throws Exception {
		if("cisco".equals(this.vendor) || "alu".equals(this.vendor)){
			endTag = "#";
		}
		if("huawei".equals(this.vendor)){
			endTag = ">";
		}
		write(commands);
		//Thread.sleep(1000);
		return waitfor(endTag);

	}

	public String getOutput() {
		return outstream.toString();
	}

	public void prepareForCommands(String vendor, String os) throws Exception {
		String line;
		String[] columns;
		BufferedReader br = null;
		if (os != null) {
			br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase()+ "." + os)));
		} else {
			br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase())));
		}

		this.vendor = vendor.toLowerCase();
		boolean flag = false;
		int counter = 0;
		while ((line = br.readLine()) != null) {
			if (!line.trim().equals("")) {
				counter = counter + 1;
				columns = line.split(",");
				if (columns[0].trim().equals("write") && !columns[1].trim().startsWith("screen") && counter < 5) {
					if(flag)
					{
						System.out.println("Password  is : " + columns[1].trim());
						password = columns[1].trim();
					}
					else
					{
						System.out.println("Username is : " + columns[1].trim());
						username = columns[1].trim();
						flag = true;
					}
				}
			}
		}
		ssh = new SSHTools(server,username,password);

		session = ssh.getSshSession();
		session.connect();

		channel = session.openChannel("shell");
		channel.connect();

		in = channel.getInputStream();
		out = new PrintStream(channel.getOutputStream());

		BufferedReader brConf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device." + vendor.toLowerCase())));

		counter = 0;
		String lineConf;
		while ((lineConf = brConf.readLine()) != null) {
			if (!lineConf.trim().equals("")) {
				counter+=1;
				if(counter > 4) {
					columns = lineConf.split(",");
					if (columns[0].trim().equals("waitfor")) {
						this.waitfor(columns[1].trim());
					}
					if (columns[0].trim().equals("write")) {
						this.write(columns[1].trim());
					}
					if (columns[0].trim().equals("sendCmd")) {
						this.sendCmd(columns[1].trim(),columns[2].trim());
					}

				}
			}
		}
	}

	public void prepareForCommands(String vendor) throws Exception {
		prepareForCommands(vendor, null);
	}

	public String sendBREAK(String nexttoken) throws Exception {
		out.write((char)26);
		out.flush();
		return waitfor(nexttoken);

	}

	public void setWaitForTimeDetails(int maxRunTime, int sleepInterval, int sleepCountMax) {
		waitForMaximumRunTime = maxRunTime;
		waitForSleepInterval = sleepInterval;
		waitForSleepCountMax = sleepCountMax;
	}

//	public static void main(String args[]) {
//		String output = "";
//		try {
//			ConnectSSH telnetdev = new ConnectSSH();
//			telnetdev.connect("192.168.0.5", 1000);
//			telnetdev.prepareForCommands("juniper");
//			output = telnetdev.applyCommands("show system uptime", ">");
//			String[] array = output.split("\r\n");
//			if(array != null && array.length > 0) {
//				List<String> values = null;
//				for(String line : array) {
//					if(line.contains("up") && line.contains("day")) {
//						line = line.trim();
//						String[] lineArray = line.split(" ");
//						values = new ArrayList<String>();
//						for(String l : lineArray) {
//							if(!" ".equals(l) && !"".equals(l)) {
//								values.add(l.trim());
//							}
//						}
//						if(!values.isEmpty()) {
//							String day = "";
//							String hour = "";
//							String minute = "";
//							if(values.get(4) != null && values.get(4).contains(":") && values.get(4).contains(",")) {
//								String aux = values.get(4).replace(",", "");
//								String[] hourMinute = aux.split(":");
//								if(hourMinute != null && hourMinute.length > 1) {
//									int hourNumber = 0;
//									if(hourMinute[0] != null && !"".equals(hourMinute[0])) {
//										hourNumber = Integer.valueOf(hourMinute[0]);
//										if(hourNumber != 1) {
//											hour = hourNumber + " hours ";
//										} else {
//											hour = hourNumber + " hour ";
//										}
//									}
//									int minuteNumber = 0;
//									if(hourMinute[1] != null && !"".equals(hourMinute[1])) {
//										minuteNumber = Integer.valueOf(hourMinute[1]);
//										if(minuteNumber != 1) {
//											minute = minuteNumber + " minutes";
//										} else {
//											minute = minuteNumber + " minute";
//										}
//									}
//									
//								}
//							}
//							int dayNumber = 0;
//							if(values.get(2) != null) {
//								dayNumber = Integer.valueOf(values.get(2));
//								if(dayNumber != 1) {
//									day = dayNumber + " days ";
//								} else {
//									day = dayNumber + " day ";
//								}
//							}
//							
//							String result = day + hour + minute;
//							break;
//						}
//					}
//				}
//			}
//			
//			System.out.println("Command Response :: " + output);
//			telnetdev.disconnect();
//		}
//		catch (Exception e) {
//			System.out.println("Excepetion occured "+e.getMessage());
//		}
//	}

}

