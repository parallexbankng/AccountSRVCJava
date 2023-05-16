package com.parallex.accountopening.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.parallex.accountopening.Constants;
import com.parallex.accountopening.response.ResponseHandler;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceCall {

	private static Logger log = LoggerFactory.getLogger(ServiceCall.class);
	
	private static TrustManager[] get_trust_mgr() {
		final TrustManager[] certs = { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// return null;
				return new X509Certificate[0];
			}

			@Override
			public void checkClientTrusted(final X509Certificate[] certs, final String t) {
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] certs, final String t) {
			}
		} };
		return certs;
	}
	
	public static String callPostingSvc1(String url, String jsonRequest) throws IOException {
		log.debug("url: " + url);
		log.debug("request: " + jsonRequest);

		String stringResp = "";

		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, jsonRequest);
		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", "application/json")
				.addHeader("client-id", Constants.CLIENT_ID)
				.addHeader("client-key", Constants.CLIENT_KEY).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		//} catch (IOException e) {
		//	e.printStackTrace();
		//	log.error("Error: " + e.fillInStackTrace());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error: " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}
	
	public static String callPostingSvc(String serviceURL, String jsonRequest) throws IOException {
		log.debug("Service URL >>>>> " + serviceURL);
		log.debug("Request >>>>> " + jsonRequest);
		String[] testArr = serviceURL.split("//");
		String URI = testArr[1];
		testArr = URI.split("/");
		String uri = testArr[0];
		String[] tstArray = uri.split(":");
		final String uri1 = tstArray[0];
		log.debug("Domain Name >>> " + uri1);
		
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession sslSession) {
			
				return hostname.equals(uri1);
			}
		});
		URL url = new URL(serviceURL);
		SSLContext sc;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder res = new StringBuilder();

		try {
			sc = SSLContext.getInstance("TLSv1.2");// TLSv1.2 TLSv1
			sc.init((KeyManager[]) null, (TrustManager[]) null, new SecureRandom());
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("client-id", Constants.CLIENT_ID);
			conn.setRequestProperty("client-key", Constants.CLIENT_KEY);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(jsonRequest);
			wr.flush();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String ex;
			while ((ex = rd.readLine()) != null) {
				res.append(ex);
			}
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("error encountered while processing service !!! "
					+ e.fillInStackTrace());
			log.error("exception detected and re-thrown! " + e);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			log.error("error encountered while processing service !!! "
					+ e.fillInStackTrace());
			log.error("exception detected and re-thrown! " + e);
			
		} catch (IOException var15) {
			log.error("error encountered while processing service !!! "
					+ var15.fillInStackTrace());
			log.error("exception detected and re-thrown! " + var15);
			throw var15;
		} finally {
			if (wr != null) {
				wr.close();
			}

			if (rd != null) {
				rd.close();
			}

		}

		log.debug("EXTRACTING RESPONSE...");
		log.debug("RESPONSE GOT >>> " + res.toString());
		return res.toString();
	}
	
	public static String callIdentityXService(String url, String jsonRequest) {
		log.debug("url: " + url);
		log.debug("request: " + jsonRequest);
		String stringResp = "";

		String[] testArr = url.split("//");
		String URI = testArr[1];
		testArr = URI.split("/");
		String uri = testArr[0];
		String[] tstArray = uri.split(":");
		String uri1 = tstArray[0];
		log.debug("Domain Name >>> " + uri1);

		SSLContext sc;
		final SSLSocketFactory sslSocketFactory;
		TrustManager[] trust_mgr;
		OkHttpClient client;
		try {
			sc = SSLContext.getInstance("TLSv1.2");
			trust_mgr = get_trust_mgr();
			sc.init(null, trust_mgr, new SecureRandom());
			sslSocketFactory = sc.getSocketFactory();
			client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true)
					.sslSocketFactory(sslSocketFactory, (X509TrustManager) trust_mgr[0])
					.hostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return hostname.equals(uri1);
						}
					}).build();

			client.callTimeoutMillis();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jsonRequest);
			Request request = new Request.Builder().url(url).method("POST", body)
					.addHeader("Content-Type", "application/json")
					.addHeader("Authorization", "Basic YWRtaW46M0ZIU0Z4dDI=").build();//YnZudmFsaWRhdG9yOlZ1MGFYRGtF test://cmV0YWlsLXRlc3Q6UGFyYWxsZXgxMDE=
			Response response = null;
			try {
				response = client.newCall(request).execute();
				stringResp = response.body().string();
			} catch (Exception e) {
				log.error("Error >>> " + e.fillInStackTrace());
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}

	public static String callWrapperService2(String auth, String url, String jsonRequest) {
		log.debug("url: " + url);
		log.debug("request: " + jsonRequest);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, jsonRequest);
		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer "+auth).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (Exception e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}
	
	public static String callWrapperService(String auth, String url, String jsonRequest) {
		log.debug("url: " + url);
		// log.debug("request: " + jsonRequest);
		String stringResp = "";

		String[] testArr = url.split("//");
		String URI = testArr[1];
		testArr = URI.split("/");
		String uri = testArr[0];
		String[] tstArray = uri.split(":");
		String uri1 = tstArray[0];
		log.debug("Domain Name >>> " + uri1);

		SSLContext sc;
		final SSLSocketFactory sslSocketFactory;
		TrustManager[] trust_mgr;
		OkHttpClient client;
		try {
			sc = SSLContext.getInstance("TLSv1.2");
			trust_mgr = get_trust_mgr();
			sc.init(null, trust_mgr, new SecureRandom());
			sslSocketFactory = sc.getSocketFactory();
			client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true)
					.sslSocketFactory(sslSocketFactory, (X509TrustManager) trust_mgr[0])
					.hostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return hostname.equals(uri1);
						}
					}).build();

			client.callTimeoutMillis();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jsonRequest);
			Request request = new Request.Builder().url(url).method("POST", body)
					.addHeader("Content-Type", "application/json")
					.addHeader("Authorization", "Bearer "+auth).build();
			Response response = null;
			try {
				response = client.newCall(request).execute();
				stringResp = response.body().string();
			} catch (Exception e) {
				log.error("Error >>> " + e.fillInStackTrace());
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// log.debug("Response: " + stringResp);
		return stringResp;
	}
	
	  public static String callWrapperService1(String url, String jsonRequest) {
	        log.debug("url: " + url);
	        log.debug("request: " + jsonRequest);
	        String stringResp= "";

	        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
	                .readTimeout(60, TimeUnit.SECONDS).build();
	        client.callTimeoutMillis();

	        MediaType mediaType = MediaType.parse("application/json");
	        RequestBody body = RequestBody.create(mediaType, jsonRequest);
	        Request request = new Request.Builder()
	                .url(url)
	                .method("POST", body)
	                .addHeader("Content-Type", "application/json")
	                //.addHeader("Authorization", "Basic QVBQVVNFUjpQcm9EcGFzc3dvcmQ=")
	                .build();
	        Response response = null;
	        try {
	            response = client.newCall(request).execute();
	            stringResp = response.body().string();
	        } catch (IOException e) {
	            log.error("Error >>> " + e.fillInStackTrace());
	        }
	        log.debug("Response: "+ stringResp);
	        return stringResp;
	    }
	
	  
	  public static String callAuthService(String url, String jsonRequest) {
			log.debug("url: " + url);
			log.debug("request: " + jsonRequest);
			String stringResp = "";

			String[] testArr = url.split("//");
			String URI = testArr[1];
			testArr = URI.split("/");
			String uri = testArr[0];
			String[] tstArray = uri.split(":");
			String uri1 = tstArray[0];
			log.debug("Domain Name >>> " + uri1);

			SSLContext sc;
			final SSLSocketFactory sslSocketFactory;
			TrustManager[] trust_mgr;
			OkHttpClient client;
			try {
				sc = SSLContext.getInstance("TLSv1.2");
				trust_mgr = get_trust_mgr();
				sc.init(null, trust_mgr, new SecureRandom());
				sslSocketFactory = sc.getSocketFactory();
				client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
						.readTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true)
						.sslSocketFactory(sslSocketFactory, (X509TrustManager) trust_mgr[0])
						.hostnameVerifier(new HostnameVerifier() {
							@Override
							public boolean verify(String hostname, SSLSession session) {
								return hostname.equals(uri1);
							}
						}).build();

				client.callTimeoutMillis();

				MediaType mediaType = MediaType.parse("application/json");
				RequestBody body = RequestBody.create(mediaType, jsonRequest);
				Request request = new Request.Builder().url(url).method("POST", body)
						.addHeader("Content-Type", "application/json").build();
				Response response = null;
				try {
					response = client.newCall(request).execute();
					stringResp = response.body().string();
				} catch (Exception e) {
					log.error("Error >>> " + e.fillInStackTrace());
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			log.debug("Response: " + stringResp);
			return stringResp;
		}  
	public static String callAuthService1(String url, String jsonRequest) {
		log.debug("url: " + url);
		log.debug("request: " + jsonRequest);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, jsonRequest);
		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", "application/json").build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (Exception e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}

	public static String callCardMgmt(String url, String jsonRequest) {
		log.debug("url: " + url);
		log.debug("request: " + jsonRequest);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, jsonRequest);
		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", "application/json").addHeader("Authorization", "Basic QVBQVVNFUjp0ZXN0")
				.addHeader("Applcode", "VVNTRA==").build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (IOException e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}
	
	public static String callCardMgmt1(String url, String rawRequest) {
		log.debug("url: " + url);
		log.debug("request: " + rawRequest);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();

		MediaType mediaType = MediaType.parse("text/plain");
		RequestBody body = RequestBody.create(mediaType, rawRequest);
		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", "text/plain").addHeader("Authorization", "Basic QVBQVVNFUjp0ZXN0")
				.addHeader("Applcode", "VVNTRA==").build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (IOException e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}

	
	
	public static String callSearchPepSrvc1(String url, String term) {
		log.debug("url: " + url);
		log.debug("Term: " + term);
		
		String stringResp = "";
		
		String[] testArr = url.split("//");
		String URI = testArr[1];
		testArr = URI.split("/");
		String uri = testArr[0];
		String[] tstArray = uri.split(":");
		String uri1 = tstArray[0];
		log.debug("Domain Name >>> " + uri1);

		SSLContext sc;
		final SSLSocketFactory sslSocketFactory;
		TrustManager[] trust_mgr;
		OkHttpClient client;
		try {
			sc = SSLContext.getInstance("TLSv1.2");
			trust_mgr = get_trust_mgr();
			sc.init(null, trust_mgr, new SecureRandom());
			sslSocketFactory = sc.getSocketFactory();
			client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true)
					.sslSocketFactory(sslSocketFactory, (X509TrustManager) trust_mgr[0])
					.hostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return hostname.equals(uri1);
						}
					}).build();
			client.callTimeoutMillis();
			
			
			
		
			Request request = new Request.Builder()
					.url(url + "peplistapi/api/PEPList/SearchPEP?term=" + term).method("GET", null)
					.build();
			Response response;
			try {
				response = client.newCall(request).execute();
				stringResp = response.body().string();
				log.debug("Response code: " + response.code());
				//if (response.code() == 200) {

					
				//}
			} catch (IOException e) {
				log.error("Error >>> " + e.fillInStackTrace());
				throw new ResponseHandler(e.fillInStackTrace().getMessage());
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		log.debug("Response: " + stringResp);
		return stringResp;
	}
	
	public static String callSearchPepSrvc(String url, String term) {
		
		log.debug("Term: " + term);
		
		String stringReq = "https://wisdomictl.com/peplistapi/api/PEPList/SearchPEP?term=\"" + term +"\"";
		log.debug("url: " + stringReq);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS)
				.readTimeout(40, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();
		
		Request request = new Request.Builder()
				  .url(stringReq)
				  .method("GET", null)
				  .build();

		Response response;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (IOException e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}
	
public static String callSearchWatchlistSrvc(String url, String term) {
		
		log.debug("Term: " + term);
		
		String stringReq = "https://wisdomictl.com/watchlistapi/api/Watchlist/SearchWatchlist?term="+term ;
		log.debug("url: " + stringReq);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS)
				.readTimeout(40, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();
		
		Request request = new Request.Builder()
				  .url(stringReq)
				  .method("GET", null)
				  .build();

		Response response;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (IOException e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}
	
	public static String callAccountLienEnquirySrvc(String url, String accountNo) {
		log.debug("url: " + url);
		log.debug("AccountNo: " + accountNo);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS)
				.readTimeout(40, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();

		Request request = new Request.Builder()
				.url(url + "/AccountLienEnquiry?AccountNo=" + accountNo + "&BankId=01").method("GET", null)
				.addHeader("client-id", Constants.CLIENT_ID).addHeader("client-key", Constants.CLIENT_KEY)
				.build();

		Response response;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (IOException e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}
	
	public static String callGetPossibeBanksSrvc(String url, String accountNo) {
		log.debug("url: " + url);
		log.debug("AccountNo: " + accountNo);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS)
				.readTimeout(40, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();

		Request request = new Request.Builder()
				.url(url + "/" + accountNo).method("GET", null)
				.build();

		Response response;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (IOException e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}
	


	public static String callNameEnquirySrvc(String url, String accountNo, String bankCode) {
		log.debug("url: " + url);
		log.debug("AccountNo: " + accountNo);
		log.debug("BankCode: " + bankCode);
		String stringResp = "";

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS)
				.readTimeout(40, TimeUnit.SECONDS).build();
		client.callTimeoutMillis();

		String url1 = url + "/account/nameinquire?accountNumber=" + accountNo + "&BankCode=" + bankCode + "&bankId=01";
		log.debug("url full: " + url1);
		Request request = new Request.Builder().url(url1).method("GET", null).addHeader("client-id", Constants.CLIENT_ID)
				.addHeader("client-key", Constants.CLIENT_KEY).build();

		Response response;
		try {
			response = client.newCall(request).execute();
			stringResp = response.body().string();
		} catch (IOException e) {
			log.error("Error >>> " + e.fillInStackTrace());
		}
		log.debug("Response: " + stringResp);
		return stringResp;
	}

}
