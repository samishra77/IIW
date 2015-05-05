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
	private String status;
	public static final String SUCCESS = "Success";
	public static final String ERROR = "Error";

	public UsageTracking(String operation, String user, String param) {
		this.operation = operation;
		this.username = user;
		this.start = System.currentTimeMillis();
		this.params = param;
		this.status = UsageTracking.SUCCESS;
	}

	private String duration() {
		long end = System.currentTimeMillis();
		long diff = (end - start) / 1000;
		return String.valueOf(diff);
	}

	public synchronized void write() {
		try {
			String header = "Date Time,User,Operation,Search Parameters,Response Time(Sec),No Of Records Fetched,Status\n";
			String logfile = SstConfig.getDefaultInstance().getProperty("usageTracking.logfile");
			File file = new File(logfile);
			boolean createHeader = false;
			if (!file.exists()) {
				createHeader = true;
			}
			FileWriter fileWriter = new FileWriter(file, true);
			if (createHeader) {
				fileWriter.write(header);
			}
			fileWriter.write(this.createLine());
			fileWriter.close();
		} catch (Exception e) {
			log.error("[" + username + "] " + e, e);
		}
	}

	private String createLine() {
		Date date = new Date(start);
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		String dateFormated = formatter.format(date);
		String result = "";
		result = dateFormated + "," + username + "," + operation + "," + params + "," + duration() + "," + resultsFetched + "," + status +"\n";
		return result;
	}

	public long getResultsFetched() {
		return resultsFetched;
	}

	public void setResultsFetched(long resultsFetched) {
		this.resultsFetched = resultsFetched;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
