package com.colt.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.colt.util.SstConfig;
import com.colt.ws.biz.Response;

@RestController
public class Feedback {

	private Log log = LogFactory.getLog(SearchService.class);

	@RequestMapping(value = "/doFeedback", method = RequestMethod.POST, headers = "Accept=application/json")
	public synchronized Object doFeedback(@RequestBody String sugestion, @RequestParam String username) {
		log.info("[" + username + "] Entering method doFeedback()");
		Response response =  new Response();
		BufferedWriter b = null;
		try {
			String file = SstConfig.getDefaultInstance().getProperty("feedbackbutton.logfile");
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			b = new BufferedWriter(new FileWriter(file, true));
			b.write(sdf.format(d.getTime()) + " [" + username + "]" + "\n" + sugestion + "\n======================================================");
			b.newLine();
			b.flush();
			response.setStatus(Response.SUCCESS);
		} catch (Exception e) {
			log.error("[" + username + "] " + e, e);
			response = new Response();
			response.setStatus(Response.FAIL);
			response.setErrorCode(Response.CODE_UNKNOWN);
			response.setErrorMsg(e.getMessage());
		} finally { // always close the file
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					log.error(e, e);
					response = new Response();
					response.setStatus(Response.FAIL);
					response.setErrorCode(Response.CODE_UNKNOWN);
					response.setErrorMsg(e.getMessage());
				}
			}
		}
		log.info("Exit method doFeedback()");
		return response;
	}
}