package com.colt.util;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UsageTracking {

	private Log log = LogFactory.getLog(UsageTracking.class);
	private long start = 0;
	private String operation;
	private String username;
	private String params;
	private long resultsFetched;

	public UsageTracking(String operation, String user, String param) {
		this.operation = operation;
		this.username = user;
		this.start = System.currentTimeMillis();
		this.params = param;
	}

	private String duration() {
		long end = System.currentTimeMillis();
		long diff = (end - start);
		return String.valueOf(diff);
	}

	public synchronized void write() {
		try {
			String logfile = SstConfig.getDefaultInstance().getProperty("usageTracking.logfile");
			File file = new File(logfile);
			FileWriter fileWriter = new FileWriter(file, true);
			fileWriter.write(this.createLine());
			fileWriter.close();
		} catch (Exception e) {
			log.error("[" + username + "] " + e, e);
		}
	}

	private String createLine() {
		Date date = new Date(start);
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss,SSS");
		String dateFormated = formatter.format(date);
		String result = "";
		result = dateFormated + "," + username + "," + operation + "," + params + "," + duration() + "," + resultsFetched + "\n";
		return result;
	}

	public long getResultsFetched() {
		return resultsFetched;
	}

	public void setResultsFetched(long resultsFetched) {
		this.resultsFetched = resultsFetched;
	}
}
