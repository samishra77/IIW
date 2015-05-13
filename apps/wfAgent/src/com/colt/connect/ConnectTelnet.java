package com.colt.connect;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.TelnetClient;

/**
 * @author Aricent
 * Class to connect through telnet
 */
public class ConnectTelnet extends TelnetDevice {
	protected Log log;
	protected TelnetClient telnet;
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
	public ConnectTelnet() {
		log = LogFactory.getLog(this.getClass());
		log.info("Using ConnectTelnet");
		telnet = new TelnetClient();
		telnet.setConnectTimeout(CONNECTTIMEOUT*1000);
	}

	@Override
	public void connect(String server, int _timeout) throws Exception {
		try {
			telnet.connect(server, 23);
			int auxtimeout = (_timeout > IDLETIMEOUT) ? _timeout:IDLETIMEOUT;
			telnet.setSoTimeout(auxtimeout*1000);

			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());
		} catch (Exception e) {
			log.error(server + ": " + e.getMessage(), e);
			throw e;
		}

	}

	@Override
	public String waitfor(String pattern) throws Exception {
		System.out.println("Patter passed is : " + pattern);
		if (pattern==null || "".equals(pattern)) {
			return null;
		}
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		int sleepCount = 0;
		//		final int maximumRunTime = 90000; // spends maximum 90 seconds reading input
		//		final int sleepInterval = 500;  // each sleep interval is 0.5 second
		//		final int sleepCountMax = 40;  // number os sleep intervals in order to reach 20 seconds, which is the "idle" timeout
		long startTime = System.currentTimeMillis();

		StringBuilder sb = new StringBuilder();
		char ch;

		while (true) {
			if (in.available() > 0) {
				sleepCount = 0;
				ch = (char)in.read();
				log.debug("char received: "+ch+"      (in.available():"+in.available());
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
				return sb.toString();
			}
		}
	}

	@Override
	public void write(String word) throws Exception {
		out.println(word);
		out.flush();
	}

	@Override
	public String sendCmd(String command, String nexttoken) throws Exception {
		write( command );
		return waitfor( nexttoken );
	}

	@Override
	public void disconnect() {
		try {
			telnet.disconnect();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws Exception
	 */
	private String readBuffer(String endTag) throws Exception {
		int k;
		int sleepCount = 0;
		//		final int maximumRunTime = 270000; // (REF#112 Increased to multiple of 3) spends maximum 90 seconds reading input
		//		final int sleepInterval = 500;  // each sleep interval is 0.5 second
		//		final int sleepCountMax = 180;  // (REF#112 Increased from 20 cycles to 180) number os sleep intervals in order to reach 20 seconds, which is the "idle" timeout

		char ch;
		StringBuilder sb = new StringBuilder();
		long startTime = System.currentTimeMillis();
		while (true) {
			if (in.available() > 0) {
				sleepCount = 0;
				ch = (char)in.read();
				log.debug(ch);
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
			//			if (System.currentTimeMillis()-startTime > waitForMaximumRunTime) {
			//				sb.append("\n[Time expired. It is taking too long to run. This is the output so far. Operation finished]");
			//				return sb.toString();
			//			}
			if (endTag!=null && !"".equals(endTag)) {
				if ( ( k = sb.indexOf(endTag)) >= 0 ) {
					return (sb.substring(0,k));
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String applyCommands(String commands) throws Exception {
		return applyCommands(commands, null);
	}

	@Override
	public String applyCommands(String commands, String endTag) throws Exception {
		String line;
		BufferedReader br = new BufferedReader(new StringReader(commands));

		while ((line = br.readLine()) != null) {
			this.write(line);
			Thread.sleep(70);
		}
		if (endTag!=null && !"".equals(endTag)) {
			this.write("\n"+endTag);
		}
		Thread.sleep(500);
		return this.readBuffer(endTag);

	}

	@Override
	public String getOutput() {
		return outstream.toString();
	}

	@Override
	public void prepareForCommands(String vendor, String devicetype, String suffix) throws Exception {
		String line;
		String[] columns;
		BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/conf/prepare-device."+vendor.toLowerCase()+"."+devicetype.toLowerCase()+"."+suffix)));

		while ((line = br.readLine()) != null) {
			if (!line.trim().equals("")) {
				columns = line.split(",");
				if (columns[0].trim().equals("waitfor")) {
					this.waitfor(columns[1].trim());
				}
				if (columns[0].trim().equals("write")) {
					this.write(columns[1].trim());
					//this.write("colt123");
				}
				if (columns[0].trim().equals("sendCmd")) {
					this.sendCmd(columns[1].trim(),columns[2].trim());
				}
			}
		}
	}

	@Override
	public String sendBREAK(String nexttoken) throws Exception {
		out.write((char)26);
		out.flush();
		return waitfor(nexttoken);

	}

	@Override
	public void setWaitForTimeDetails(int maxRunTime, int sleepInterval, int sleepCountMax) {
		waitForMaximumRunTime = maxRunTime;
		waitForSleepInterval = sleepInterval;
		waitForSleepCountMax = sleepCountMax;
	}

}