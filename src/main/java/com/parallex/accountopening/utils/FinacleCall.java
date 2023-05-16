package com.parallex.accountopening.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.parallex.accountopening.Constants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FinacleCall {

	private static final Logger logger = LoggerFactory.getLogger(FinacleCall.class);

	public static String processCall(String serviceURL, String soapreq) throws Exception {
		logger.debug("Service URL >>>>> " + serviceURL);
		logger.debug("Request to provider >>>>> " + soapreq);
		String[] testArr = serviceURL.split("//");
		String URI = testArr[1];
		testArr = URI.split("/");
		String uri = testArr[0];
		String[] tstArray = uri.split(":");
		final String uri1 = tstArray[0];
		logger.debug("Domain Name >>> " + uri1);
		
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession sslSession) {
			
				return hostname.equals(uri1);
			}
		});
		URL url = new URL(serviceURL);
		SSLContext sc = SSLContext.getInstance("TLSv1.2");// TLSv1.2 TLSv1
		sc.init((KeyManager[]) null, (TrustManager[]) null, new SecureRandom());
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setSSLSocketFactory(sc.getSocketFactory());
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-type", "application/xml");
		conn.setRequestProperty("SOAPAction", "");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder res = new StringBuilder();

		try {
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(soapreq);
			wr.flush();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String ex;
			while ((ex = rd.readLine()) != null) {
				res.append(ex);
			}
		} catch (Exception ex) {
			logger.error("error encountered while processing service !!! " + ex.fillInStackTrace());
			System.out.println("exception detected and re-thrown! " + ex);
			throw ex;
		} finally {
			if (wr != null) {
				wr.close();
			}

			if (rd != null) {
				rd.close();
			}

		}

		logger.debug("EXTRACTING RESPONSE...");
		logger.debug("RESPONSE GOT >>> " + res.toString());
		return res.toString();
	}
	
	public static String processEntrustCall1(String serviceURL, String soapreq) throws Exception {
		logger.debug("Service URL >>>>> " + serviceURL);
		logger.debug("Request to provider >>>>> " + soapreq);
		String[] testArr = serviceURL.split("//");
		String URI = testArr[1];
		testArr = URI.split("/");
		String uri = testArr[0];
		String[] tstArray = uri.split(":");
		final String uri1 = tstArray[0];
		logger.debug("Domain Name >>> " + uri1);
		
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession sslSession) {
			
				return hostname.equals(uri1);
			}
		});
		URL url = new URL(serviceURL);
		SSLContext sc = SSLContext.getInstance("TLSv1.2");// TLSv1.2 TLSv1
		sc.init((KeyManager[]) null, (TrustManager[]) null, new SecureRandom());
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setSSLSocketFactory(sc.getSocketFactory());
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-type", "application/json");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder res = new StringBuilder();

		try {
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(soapreq);
			wr.flush();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String ex;
			while ((ex = rd.readLine()) != null) {
				res.append(ex);
			}
		} catch (Exception ex) {
			logger.error("error encountered while processing service !!! " + ex.fillInStackTrace());
			System.out.println("exception detected and re-thrown! " + ex);
			throw ex;
		} finally {
			if (wr != null) {
				wr.close();
			}

			if (rd != null) {
				rd.close();
			}

		}

		logger.debug("EXTRACTING RESPONSE...");
		logger.debug("RESPONSE GOT >>> " + res.toString());
		return res.toString();
	}
	
	public static String processEntrustCall(String url, String emailId, String tokResponse) {
		logger.debug("url: " + url);
		String[] userEmailId = emailId.split("@");
		logger.debug("EmailId: " + userEmailId[0]);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS)
				.readTimeout(40, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();

		Request request = new Request.Builder()
				.url(url + "?UserId=" + userEmailId[0] + "&tokenResponse="+tokResponse).method("GET", null)
				.addHeader("client-id", Constants.CLIENT_ID).addHeader("client-key", Constants.CLIENT_KEY)
				.build();

		Response response;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (IOException e) {
			logger.error("Error >>> " + e.fillInStackTrace());
		}
		logger.debug("Response: " + stringResp);
		return stringResp;
	}
	
	

}
