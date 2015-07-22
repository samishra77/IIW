package com.colt.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.util.SstConfig;

public class ProxyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(ProxyServlet.class);


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = SstConfig.getDefaultInstance().getProperty("workflowagent.url.base") + request.getPathInfo();
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			Enumeration<String> enumerationOfHeaderNames = request.getHeaderNames();
			while (enumerationOfHeaderNames.hasMoreElements()) {
				String headerName = enumerationOfHeaderNames.nextElement();
				Enumeration<String> headers = request.getHeaders(headerName);
				while (headers.hasMoreElements()) {
					String headerValue = headers.nextElement();
					con.setRequestProperty(headerName, headerValue);
				}
			}

			con.setDoOutput(true);
			InputStream requestInput = request.getInputStream();
			BufferedReader requestIn = new BufferedReader(new InputStreamReader(requestInput));
			String inputLine = null;
			OutputStream conOutput = con.getOutputStream();
			while ((inputLine = requestIn.readLine()) != null) {
				conOutput.write(inputLine.getBytes());
			}
			requestIn.close();
			conOutput.flush();

			InputStream conInputStream = con.getInputStream();
			BufferedReader conIn = new BufferedReader(new InputStreamReader(conInputStream));
			inputLine = null;
			OutputStream reponseOutput = response.getOutputStream();
			while ((inputLine = conIn.readLine()) != null) {
				reponseOutput.write(inputLine.getBytes());
			}
			conIn.close();
			reponseOutput.flush();

		} catch (Exception e) {
			log.error(e, e);
		}
	}
}
