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

import com.colt.util.AgentEncryption;
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
	private String vendor = null;
	private String os = null;
	protected Channel channel=null;
	protected Session session=null;
	private String username;
	private String password;
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

	public void connect(String server, int _timeout, String connectProtocol, String vendor, String os) throws Exception {
		try {
			log.debug("SSH connection to " + server);
			this.vendor = vendor.toLowerCase();
			this.os = os;
			this._timeout = _timeout;
			String line;
			String[] columns;
			BufferedReader br = null;
			if (os != null) {
				try {
					br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase()+ "." + os)));
				} catch(Exception e) {
					br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase())));
				}
			} else {
				br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase())));
			}

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
							password = AgentEncryption.decrypt(columns[1].trim());
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
		} catch (Exception e) {
			log.error(server + ": " + e.getMessage(), e);
			throw e;
		}
	}

	public String waitfor(String pattern) throws Exception {
		String[] retArray = waitforPattern(pattern);
		return retArray[0];
	}

	public String[] waitforPattern(String pattern) throws Exception {
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

		try {
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
					if (sleepCount == waitForSleepCountMax) {
						log.debug("waitfor() waiting for data... (sleepCount="+sleepCount+")");
					}
					Thread.sleep(waitForSleepInterval);
				}
				if (System.currentTimeMillis()-startTime > waitForMaximumRunTime) {
					throw new Exception("Timeout");
				}
				m = p.matcher(sb.toString());
				if( m.find() ) {
					String[] retArray = new String[2];
					String result = sb.toString();
					log.debug(result);
					retArray[0] = result;
					retArray[1] = m.group();
					return retArray;
				}
			}
		} catch(Exception e) {
			log.debug("Data before exception: " + sb.toString());
			throw e;
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
		int sleepCount = 0;
		char ch;
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		StringBuilder sb = new StringBuilder();
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
				if (sleepCount == waitForSleepCountMax) {
					log.debug("readBuffer() waiting for data... (sleepCount="+sleepCount+")");
				}
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
		write(commands);
		//Thread.sleep(1000);
		return waitfor(endTag);
	}

	public String getOutput() {
		return outstream.toString();
	}

	public void prepareForCommandsFullPrepare() throws Exception {
		String[] columns;
		BufferedReader brConf;
		if (os != null) {
			try {
				brConf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase()+ "." + os)));
			} catch(Exception e) {
				brConf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase())));
			}
		} else {
			brConf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase())));
		}
		int counter = 0;
		String lineConf;
		boolean isPassword = false;
		while ((lineConf = brConf.readLine()) != null) {
			if (!lineConf.trim().equals("")) {
				if(counter > 0) {
					columns = lineConf.split(",");
					if (columns[0].trim().equals("waitfor") && columns[1].trim().equals("word:") && counter < 3) {
						isPassword = true;
					} else if(isPassword) {
						password = AgentEncryption.decrypt(columns[1].trim());
					}
					if (columns[0].trim().equals("waitfor")) {
						this.waitfor(columns[1].trim());
					}
					if (columns[0].trim().equals("write")) {
						if(!isPassword) {
							this.write(columns[1].trim());
						} else {
							this.write(password);
							isPassword = false;
						}
					}
					if (columns[0].trim().equals("sendCmd")) {
						this.sendCmd(columns[1].trim(),columns[2].trim());
					}
				}
				counter++;
			}
		}
		brConf.close();
	}
	
	public void prepareForCommands() throws Exception {
		String[] columns;
		BufferedReader brConf;
		if (os != null) {
			try {
				brConf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase()+ "." + os)));
			} catch(Exception e) {
				brConf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase())));
			}
		} else {
			brConf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase())));
		}
		int counter = 0;
		String lineConf;
		while ((lineConf = brConf.readLine()) != null) {
			if (!lineConf.trim().equals("")) {
				counter++;
				if(counter > 4) {
					columns = lineConf.split(",");
					if (columns[0].trim().equals("waitfor")) {
						String[] waitArray = this.waitforPattern(columns[1].trim());
						//Some ERX devices don't recognize the ssh username. They read it like telnet, the user has to enter the username after the prompt is displayed.
						if ( waitArray[1] != null &&  (waitArray[1].equalsIgnoreCase("name:") || waitArray[1].equalsIgnoreCase("login:")) ) {
							prepareForCommandsFullPrepare();
							break;
						}
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
		brConf.close();
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

}

