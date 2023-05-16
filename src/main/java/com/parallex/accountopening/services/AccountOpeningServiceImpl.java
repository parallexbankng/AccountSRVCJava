package com.parallex.accountopening.services;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.parallex.accountopening.Constants;
import com.parallex.accountopening.domain.AccountDetailsResponse;
import com.parallex.accountopening.domain.AccountLienInquiryResponse;
import com.parallex.accountopening.domain.AccountOpeningRequest;
import com.parallex.accountopening.domain.AccountParam;
import com.parallex.accountopening.domain.AccountReactivationRequest;
import com.parallex.accountopening.domain.AccountReactivationResponse;
import com.parallex.accountopening.domain.AccountRestrictionRequest;
import com.parallex.accountopening.domain.AccountRestrictionResponse;
import com.parallex.accountopening.domain.AcctDetails;
import com.parallex.accountopening.domain.DedupRequest;
import com.parallex.accountopening.domain.DedupResponse;
import com.parallex.accountopening.domain.Result;
import com.parallex.accountopening.domain.RiskRatingRequest;
import com.parallex.accountopening.domain.RiskRatingResponse;
import com.parallex.accountopening.domain.SalaryAccountFIResult;
import com.parallex.accountopening.domain.SalaryAccountResponse;
import com.parallex.accountopening.domain.UserDetails;
import com.parallex.accountopening.json.Request;
import com.parallex.accountopening.repositories.ResourceRepository;
import com.parallex.accountopening.utils.AppUtil;
import com.parallex.accountopening.utils.FinacleCall;
import com.parallex.accountopening.utils.ServiceCall;
import com.parallex.accountopening.xml.XMLParser;

@Service
public class AccountOpeningServiceImpl implements AccountOpeningService {

	private static Logger log = LoggerFactory.getLogger(AccountOpeningServiceImpl.class);

	@Value("${fi_url}")
	private String fiUrl;

	@Value("${sms_url}")
	private String smsUrl;

	@Value("${accountopening_url}")
	private String accountOpeningUrl;

	@Value("${savingacctcreation_url}")
	private String savingAcctCreationUrl;

	@Value("${updateaccountmandate_url}")
	private String updateAccountMandateUrl;

	@Value("${pep_search_url}")
	private String pepSearchUrl;

	@Value("${daon_user}")
	private String daonUser;

	@Autowired
	ResourceRepository resourceRepository;

	@Override
	public String dedup(DedupRequest req) throws Exception {
		JSONObject resObj = new JSONObject();

		try {

			String fiXML = XMLParser.formFinacleRequest(req);
			String soapReq = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA["
					+ fiXML + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			log.debug("Invoking Dedup WS");
			DedupResponse fiResponse = XMLParser.parseFinacleResponse(FinacleCall.processCall(fiUrl, soapReq));

			try {

				log.debug("Status Message >>> " + fiResponse.getStatusMessage());
				log.debug("CIF_ID >>> " + fiResponse.getCifId());

				if (fiResponse.getStatusFlag().equalsIgnoreCase("Y")
						&& fiResponse.getStatusMessage().equalsIgnoreCase("SUCCESS")) {
					if (fiResponse.getCifId() == "" || fiResponse.getCifId() == null
							|| fiResponse.getCifId().isEmpty()) {
						log.debug("Cifid is null");
						resObj.put("statusCode", "08");
						resObj.put("statusMsg", "Failed");
						resObj.put("cifId", fiResponse.getCifId());
						resObj.put("responseMessage", "Cif does not exist " + fiResponse.getResponseMessage());
						return resObj.toString();
					} else {
						log.debug("Dedup is successful");
						resObj.put("statusCode", "00");
						resObj.put("statusMsg", "Success");
						resObj.put("cifId", fiResponse.getCifId());
						resObj.put("responseMessage", "Cif exist");
						return resObj.toString();
					}

				} else {
					log.debug("Dedup not successful");
					resObj.put("statusCode", "08");
					resObj.put("statusMsg", "Failed");
					resObj.put("cifId", "");
					resObj.put("responseMessage", "Cif does not exist " + fiResponse.getResponseMessage());
					return resObj.toString();
				}

			} catch (Exception e) {

				e.printStackTrace();
				log.error("Error >>> " + e.fillInStackTrace());
				resObj.put("statusCode", "99");
				resObj.put("statusMsg", "System Error");

				return resObj.toString();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error calling FI: " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			//resObj.put("statusMsg", "Error calling FI");
			resObj.put("statusMsg", "An error occurred while processing the request.");
			// ResponseHandler.generateResponse("Error calling FI", HttpStatus.BAD_GATEWAY);
			return resObj.toString();
		}

	}

	@Override
	public String searchPep(String term) {
		JSONObject resObj = new JSONObject();
		String wName = "";
		String wMatch = "";
		try {
			log.debug("Invoking Pep search service now");
			String resp1 = ServiceCall.callSearchPepSrvc(pepSearchUrl, term);

			if (resp1.isEmpty() || resp1 == null) {
				log.debug("pep search service returned null");
				resObj.put("statusCode", "02");
				resObj.put("statusMsg", "Failed");
				return resObj.toString();

			} else {

				JSONArray jsonarray = new JSONArray(resp1);
				JSONArray ja = new JSONArray();

				if (jsonarray.length() == 0) {
					log.debug("watchlist search not successful");
					resObj.put("statusCode", "01");
					resObj.put("statusMsg", "Failed");
					return resObj.toString();
				} else {
					// Iterating the contents of the array
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject job = new JSONObject();
						JSONObject jsonobject = jsonarray.getJSONObject(i);
						if (jsonobject.has("WatchName") && jsonobject.getString("WatchName") != null) {
							job.put("WatchName", jsonobject.getString("WatchName"));
						} else {
							job.put("WatchName", "");
							wName = "X";
						}

						if (jsonobject.has("WatchNameMatch") && jsonobject.getBigDecimal("WatchNameMatch") != null) {
							job.put("WatchNameMatch",
									formatAmount(String.valueOf(jsonobject.getBigDecimal("WatchNameMatch"))));
						} else {
							job.put("WatchNameMatch", "");
							wMatch = "";
						}

						ja.put(job);
					}
				}

				if (!wName.equalsIgnoreCase("X")) {
					log.debug("watchlist search successful");

					resObj.put("statusCode", "00");
					resObj.put("statusMsg", "Successful");
					resObj.put("details", ja);
					return resObj.toString();

				} else {
					log.debug("watchlist search not successful");
					resObj.put("statusCode", "01");
					resObj.put("statusMsg", "Failed");
					return resObj.toString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			resObj.put("statusMsg", "System Error");

			return resObj.toString();

		}
	}

	@Override
	public String searchWatchlist(String term) {
		JSONObject resObj = new JSONObject();
		String wName = "";
		String wMatch = "";
		String entityName = "";
		String entityNMatch = "";
		try {

			log.debug("Invoking watchlist search service now");
			String resp1 = ServiceCall.callSearchWatchlistSrvc(pepSearchUrl, term);

			if (resp1.isEmpty() || resp1 == null) {
				log.debug("watchlist search service returned null");
				resObj.put("statusCode", "02");
				resObj.put("statusMsg", "Failed");
				return resObj.toString();

			} else {

				JSONArray jsonarray = new JSONArray(resp1);
				JSONArray ja = new JSONArray();

				// Iterating the contents of the array
				if (jsonarray.length() == 0) {
					log.debug("watchlist search not successful");
					resObj.put("statusCode", "01");
					resObj.put("statusMsg", "Failed");
					return resObj.toString();
				} else {
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject job = new JSONObject();
						JSONObject jsonobject = jsonarray.getJSONObject(i);

						if (jsonobject.has("Ind_Name") && jsonobject.getString("Ind_Name") != null) {
							job.put("Ind_Name", jsonobject.getString("Ind_Name"));
						} else {
							job.put("Ind_Name", "");
							wName = "X";
						}

						if (jsonobject.has("Ind_NameMatch") && jsonobject.getBigDecimal("Ind_NameMatch") != null) {
							job.put("Ind_NameMatch",
									formatAmount(String.valueOf(jsonobject.getBigDecimal("Ind_NameMatch"))));
						} else {
							job.put("Ind_NameMatch", "");
							wMatch = "";
						}

						if (jsonobject.has("EntityName") && !jsonobject.isNull("EntityName")) {
							job.put("EntityName", jsonobject.getString("EntityName"));
						} else {

							job.put("EntityName", "");
							entityName = "";
						}

						if (jsonobject.has("EntityNameMatch") && jsonobject.getBigDecimal("EntityNameMatch") != null) {
							job.put("EntityNameMatch", String.format("%.1f", jsonobject.getFloat("EntityNameMatch")));

						} else {
							job.put("EntityNameMatch", "0.0");
							entityNMatch = "";
						}
						ja.put(job);
					}
				}

				if (!wName.equalsIgnoreCase("X")) {
					log.debug("watchlist search successful");

					resObj.put("statusCode", "00");
					resObj.put("statusMsg", "Successful");
					resObj.put("details", ja);
					return resObj.toString();

				} else {
					log.debug("watchlist search not successful");
					resObj.put("statusCode", "01");
					resObj.put("statusMsg", "Failed");
					return resObj.toString();
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			resObj.put("statusMsg", "System Error");

			return resObj.toString();

		}

	}

	@Override
	public String riskRating(RiskRatingRequest req) throws Exception {
		JSONObject resObj = new JSONObject();

		try {

			String fiXML = XMLParser.formFinacleRiskRatingRequest(req);
			String soapReq = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA["
					+ fiXML + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			log.debug("Invoking Dedup WS");
			RiskRatingResponse fiResponse = XMLParser
					.parseFinacleRiskRatingResponse(FinacleCall.processCall(fiUrl, soapReq));

			try {

				log.debug("Status Message >>> " + fiResponse.getStatusMessage());
				log.debug("OUTPUT >>> " + fiResponse.getResponseMessage());

				if (fiResponse.getStatusMessage().equalsIgnoreCase("SUCCESS")) {
					if (fiResponse.getResponseMessage() == "" || fiResponse.getResponseMessage() == null
							|| fiResponse.getResponseMessage().isEmpty()) {
						log.debug("Risk Rating is null");
						resObj.put("statusCode", "08");
						resObj.put("statusMsg", "Failed");
						resObj.put("responseMessage", fiResponse.getStatusMessage());
						return resObj.toString();
					} else {
						log.debug("Risk Rating is successful");
						resObj.put("statusCode", "00");
						resObj.put("statusMsg", "Success");
						resObj.put("responseMessage", fiResponse.getResponseMessage());
						return resObj.toString();
					}

				} else {
					log.debug("Risk Rating not successful");
					resObj.put("statusCode", "08");
					resObj.put("statusMsg", "Failed");
					resObj.put("responseMessage", fiResponse.getStatusMessage());
					return resObj.toString();
				}

			} catch (Exception e) {

				e.printStackTrace();
				log.error("Error >>> " + e.fillInStackTrace());
				resObj.put("statusCode", "99");
				resObj.put("statusMsg", "System Error");

				return resObj.toString();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error calling FI: " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			resObj.put("statusMsg", "An error occurred while processing the request.");
			// ResponseHandler.generateResponse("Error calling FI", HttpStatus.BAD_GATEWAY);

		}
		return resObj.toString();
	}

	@Override
	public String accountCreation(String req) throws Exception {
		JSONObject resObj = new JSONObject();
		JSONObject reqObj = new JSONObject(req);
		String acctType = "";
		String schmCode = "";
		String schmType = "";
		String title = "";
		String sourceType = "";
		String referralCode = "";
		String cardPickupAddress = "";
		String minorInd = "";
		String sol_id = "";
		String accountNo = "";
		String cif = "";
		String cif1 = "";
		String cif_id = "";
		String mobilePhone1 = "";
		String mobilePhone2 = "";
		String phone2 = "";
		String telcoName = "";
		String dob1 = "";
		String issueDate = "";
		String txtMsg = "";
		String resp = "";
		String acctResponse = null;
		String savAcctResp = null;
		String errorDesc = "";
		String acctNum = null;

		String bvn = "";
		String lastName = "";
		String firstName = "";
		String middleName = "";
		String cust_phone_num = "";
		String accountCurrencyCode = "";
		String preferredNameonCard = "";
		String glSubHeadCode = "";
		String country = "";
		String city = "";
		String emailAddr = "";
		String response = null;
		// log.debug("msisdn >>> " + reqObj.getString("msisdn"));
		// log.debug("dob >>> " + reqObj.getString("dob"));
		String dob = reqObj.getString("dob").replace("/", "-");

		if (reqObj.has("source_id") && !reqObj.getString("source_id").toString().equalsIgnoreCase("")) {

			sourceType = reqObj.getString("source_id");

		} else {
			log.debug("Source Id field in null");
			resObj.put("statusCode", "01");
			resObj.put("statusMsg", "Failed");
			resObj.put("responseMessage", "Source Id Field is required");
			return resObj.toString();
		}

		if (reqObj.has("bvn") && !reqObj.getString("bvn").toString().equalsIgnoreCase("")) {

			bvn = reqObj.getString("bvn");

		} else {
			log.debug("BVN field in null");
			resObj.put("statusCode", "02");
			resObj.put("statusMsg", "Failed");
			resObj.put("responseMessage", "BVN Field is required");
			return resObj.toString();
		}

		if (reqObj.has("lastName") && !reqObj.getString("lastName").toString().equalsIgnoreCase("")) {

			lastName = reqObj.getString("lastName");
		} else {
			lastName = "";
		}

		if (reqObj.has("firstName") && !reqObj.getString("firstName").toString().equalsIgnoreCase("")) {

			firstName = reqObj.getString("firstName");
		} else {
			firstName = "";
		}

		if (reqObj.has("middleName") && !reqObj.getString("middleName").toString().equalsIgnoreCase("")) {

			middleName = reqObj.getString("middleName");
		} else {
			middleName = ".";
		}

		if (reqObj.has("acctType") && !reqObj.getString("acctType").toString().equalsIgnoreCase("")) {

			acctType = reqObj.getString("acctType");
		} else {
			acctType = "";
		}

		if (reqObj.has("schmCode") && !reqObj.getString("schmCode").toString().equalsIgnoreCase("")) {

			schmCode = reqObj.getString("schmCode");
		} else {
			schmCode = "";
		}

		if (reqObj.has("glSubHeadCode") && !reqObj.getString("glSubHeadCode").toString().equalsIgnoreCase("")) {

			glSubHeadCode = reqObj.getString("glSubHeadCode");
		} else {
			glSubHeadCode = "";
		}

		if (reqObj.has("accountCurrencyCode") && !reqObj.getString("accountCurrencyCode").toString().equalsIgnoreCase("")) {

			accountCurrencyCode = reqObj.getString("accountCurrencyCode");
		} else {
			accountCurrencyCode = "";
		}

		if (reqObj.has("referral_code") && !reqObj.getString("referral_code").toString().equalsIgnoreCase("")) {

			referralCode = reqObj.getString("referral_code");
		} else {
			referralCode = "";
		}

		if (reqObj.has("cardPickupAddress") && !reqObj.getString("cardPickupAddress").toString().equalsIgnoreCase("")) {

			cardPickupAddress = reqObj.getString("cardPickupAddress");
		} else {
			cardPickupAddress = "";
		}

		if (reqObj.has("preferredNameonCard") && !reqObj.getString("preferredNameonCard").toString().equalsIgnoreCase("")) {

			preferredNameonCard = reqObj.getString("preferredNameonCard");
		} else {
			preferredNameonCard = "";
		}

		if (reqObj.has("minorInd") && !reqObj.getString("minorInd").toString().equalsIgnoreCase("")) {

			minorInd = reqObj.getString("minorInd"); // check the customer is a minor or not (Y/N)
		} else {
			minorInd = "";
		}

		log.debug("Request to Validate OTP before Account opening");
		JSONObject bvnRequest = new JSONObject();
		bvnRequest.put("bvn", bvn);
		bvnRequest.put("otp", reqObj.getString("otp"));
		response = ServiceCall.callAuthService(Constants.AUTHENTICATE_OTP_URL, bvnRequest.toString());

		JSONObject otpResp = new JSONObject(response);
		if (response != null && otpResp.getString("responseCode").toString().equalsIgnoreCase("00")) {

		} else {
			log.debug("OTP Validation failed");
			resObj.put("statusCode", "25");
			resObj.put("statusMsg", "Failed");

			return resObj.toString();
		}

		String sessionId = GenerateRequestid();

		Integer count = resourceRepository.getSchemeCodeNo(sourceType, schmCode, glSubHeadCode, accountCurrencyCode);
		if (count > 0) {
			log.debug("country/channel account opening config details...");
			if (count > 1) {
				log.debug("Cannot fetch country/channel account opening config details");
				resObj.put("statusCode", "06");
				resObj.put("statusMsg", "Failed");
				resObj.put("responseMessage",
						"There are multiple country/channel account opening config details. Kindly specify the scheme code");
				return resObj.toString();
			} else {
				AccountParam accountParam = resourceRepository.getAccountOpeningDefValues(sourceType, schmCode,
						glSubHeadCode, accountCurrencyCode);

				if (reqObj.has("solId") && !reqObj.getString("solId").toString().equalsIgnoreCase("")) {

					sol_id = reqObj.getString("solId").toString();
				} else {
					sol_id = accountParam.getSolId();
				}

				if (schmCode.equalsIgnoreCase("")) {

					schmCode = accountParam.getSchmCode();
				}

				if (glSubHeadCode.equalsIgnoreCase("")) {

					glSubHeadCode = accountParam.getGlSubHeadCode();
				}

				if (acctType.equalsIgnoreCase("")) {

					acctType = accountParam.getAcctType();
				}

				if (accountCurrencyCode.equalsIgnoreCase("")) {

					accountCurrencyCode = accountParam.getCrncyCode();
				}

				if (reqObj.has("schmType") && !reqObj.getString("schmType").toString().equalsIgnoreCase("")) {

					schmType = reqObj.getString("schmType").toString();
				} else {
					schmType = accountParam.getSchmCodeDesc();
				}

				if (reqObj.has("title") && !reqObj.getString("title").toString().equalsIgnoreCase("")) {

					title = reqObj.getString("title").toString();
				} else {
					title = accountParam.getTitle();
				}

				if (reqObj.has("country") && !reqObj.getString("country").toString().equalsIgnoreCase("")) {

					country = reqObj.getString("country").toString();
				} else {
					country = accountParam.getCountry();
				}

				if (reqObj.has("city") && !reqObj.getString("city").toString().equalsIgnoreCase("")) {

					city = reqObj.getString("city").toString();
				} else {
					city = accountParam.getCity();
				}

			}

		} else {
			log.debug("Cannot fetch country/channel account opening config details");
			resObj.put("statusCode", "05");
			resObj.put("statusMsg", "Failed");
			resObj.put("responseMessage", "Cannot fetch country/channel account opening config details");
			return resObj.toString();
		}

		try {

			String auth = getLoginDetails(Constants.USERNAME, Constants.PASSWORD);

			if (auth.isEmpty() || auth == "" || auth == null) {
				log.debug("Unable to generate token");
				resObj.put("statusCode", "03");
				resObj.put("statusMsg", "Failed");
				resObj.put("responseMessage", "No Authorization Header");

				return resObj.toString();
			}

			log.debug("Request to BVN Validation WS");
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("bvn", bvn);
			jsonRequest.put("channel", "1");

			String resp2 = ServiceCall.callWrapperService(auth, Constants.BVN_VALIDATION_URL, jsonRequest.toString());

			if (resp2.isEmpty() || resp2 == null) {
				log.debug("BVN Validation WS returned null");

				resObj.put("statusCode", "07");
				resObj.put("statusMsg", "Failed");
				resObj.put("responseMessage", "BVN Validation Failed");

				return resObj.toString();

			} else {

				JSONObject obj5 = new JSONObject(resp2);

				if (obj5.getString("responseCode").equalsIgnoreCase("00")) {

					if (obj5.has("dateOfBirth"))
						if (!obj5.isNull("dateOfBirth")) {
							if (obj5.has("phoneNumber1") && !obj5.getString("phoneNumber1").toString().equalsIgnoreCase("")) {
								String mob1 = obj5.getString("phoneNumber1");

								mobilePhone1 = "234" + mob1.substring(mob1.length() - 10);
								log.debug("mobilePhone1 >>> " + mobilePhone1);
							} else {
								if (obj5.has("phoneNumber2") && obj5.getString("phoneNumber2").toString().equalsIgnoreCase("")) {
									log.debug("mobilePhone2 is empty ");
									mobilePhone1 = "";

								} else {
									log.debug("mobilePhone2 is not empty ");
									String mob2 = obj5.getString("phoneNumber2").toString();
									mobilePhone1 = "234" + mob2.substring(mob2.length() - 10);
									log.debug("mobilePhone1 >>> " + mobilePhone1);
								}
							}

							String phoneNo = "+234" + "(0)" + mobilePhone1.substring(mobilePhone1.length() - 10);

							log.debug("Request to Dedup WS");

							JSONObject jRequest = new JSONObject();
							jRequest.put("acctType", acctType);
							jRequest.put("dob", dob);
							jRequest.put("firstName", firstName);
							jRequest.put("lastName", lastName);
							jRequest.put("middleName", middleName);
							jRequest.put("phoneNumber", phoneNo);
							jRequest.put("bvn", bvn);

							String resp1 = ServiceCall.callAuthService(Constants.DEDUP_URL, jRequest.toString());
							if (resp1.isEmpty() || resp1 == null) {
								log.debug("DEDUP WS returned null");

								resObj.put("statusCode", "09");
								resObj.put("statusMsg", "Failed");
								resObj.put("responseMessage", "Dedup returned null");

								return resObj.toString();

							}

							JSONObject obj = new JSONObject(resp1);

							if (obj.getString("statusCode").toString().equalsIgnoreCase("00")
									&& obj.getString("statusMsg").toString().equalsIgnoreCase("Success")) {
								cif_id = obj.getString("cifId");
								log.debug("Account Opening Cif ID: " + cif_id);

							} else {

								if (obj.getString("statusCode").equalsIgnoreCase("99")) {
									resObj.put("statusCode", obj.getString("statusCode").toString());
									resObj.put("statusMsg", "Failed");
									resObj.put("responseMessage", obj.getString("statusMsg").toString());

									return resObj.toString();
								} else {
									cif_id = "";
								}

							}

							if (!cif_id.equalsIgnoreCase("")) {

								acctNum = resourceRepository.getAccountbyCifid(schmCode, cif_id, accountCurrencyCode);

								if (acctNum != null) {
									log.debug("Customer already opened account");
									resObj.put("statusCode", "42");
									resObj.put("statusMsg", "Failed");
									resObj.put("acctNum", acctNum);
									resObj.put("responseMessage",
											"Customer already opened account with the same scheme code and currency");

									return resObj.toString();
								} else {
									log.debug("Customer does not exists on finacle");
								}
							} // else {

							/*
							 * if (minorInd.equalsIgnoreCase("Y")) { String minRelnCifId = "";// this will
							 * be parent cifid String minReln = "Guardian"; String minRelnGDCode = "FATHER";
							 * }
							 */
						} else {
							log.debug("Date Birth field is null");

							resObj.put("statusCode", "08");
							resObj.put("statusMsg", "Failed");
							resObj.put("responseMessage", "Date Birth field is null");
							return resObj.toString();
						}

					JSONObject resBVNObj = new JSONObject();
					resBVNObj.put("lastName", obj5.getString("lastName"));
					resBVNObj.put("firstName", obj5.getString("firstName"));
					resBVNObj.put("middleName", obj5.getString("middleName"));
					resBVNObj.put("streetNo", obj5.getString("residentialAddress"));

					if (!obj5.isNull("title")) {
						title = obj5.getString("title").toString().toUpperCase();
					} else {
						
					}

					if (obj5.getString("gender").toString().toUpperCase() == "Female".toUpperCase()) {
						resBVNObj.put("gender", "F");
						resBVNObj.put("title", "MRS");
					} else {
						resBVNObj.put("gender", "M");
						resBVNObj.put("title", "MR");
					}

					if (obj5.isNull("email") || obj5.getString("email").equalsIgnoreCase("")) {
						emailAddr = "noemail@parallexbank.com";
					} else {
						emailAddr = obj5.getString("email").toString();
					}

					resBVNObj.put("phonNumber1", "234");
					resBVNObj.put("phonNumber2", "0");
					resBVNObj.put("phonNumber3", mobilePhone1.substring(mobilePhone1.length() - 10));
					resBVNObj.put("emailAddress", emailAddr);
					resBVNObj.put("loclGovt", obj5.getString("lgaOfResidence"));
					resBVNObj.put("loclOrigin", obj5.getString("lgaOfOrigin"));
					resBVNObj.put("maritalStatus", obj5.getString("maritalStatus"));
					resBVNObj.put("nationality", obj5.getString("nationality"));
					// resBVNObj.put("residentialAddress", obj.getString("residentialAddress"));
					resBVNObj.put("stateOfOrigin", obj5.getString("stateOfOrigin"));
					resBVNObj.put("stateOfResidence", obj5.getString("stateOfResidence"));
					resBVNObj.put("typOfId", "IDPRF");
					resBVNObj.put("issueAuthority", "FGN");
					resBVNObj.put("registrationNumber", obj5.getString("nin"));
					resBVNObj.put("base64Image", obj5.getString("base64Image").replace("\"", ""));

					// converting dob from 30-Jan-1949 to 30-01-1949
					dob1 = AppUtil.getValueDateTime1(obj5.getString("dateOfBirth"));
					// issueDate = AppUtil.getValueDateTime1(obj.getString("registrationDate"));
					log.debug("dob1 >>> " + dob1);
					// log.debug("issueDate >>> " + issueDate);
					resBVNObj.put("dob", dob1);

					if (!cif_id.equalsIgnoreCase("")) {
						// Invoke Savings Account Creation WS
						savAcctResp = savingsAccountCreation(cif_id, bvn,
								referralCode, schmCode, schmType);

						if (savAcctResp != null) {

							JSONObject obj1 = new JSONObject(savAcctResp);

							log.debug("Savings Account Creation ResponseCode: " + obj1.getString("responseCode").toString());

							if (obj1.has("cif"))
								if (!obj1.isNull("cif")) {
									cif1 = obj1.getString("cif");

								} else {
									cif1 = "";
								}

							if (obj1.has("accountNumber"))
								if (!obj1.isNull("accountNumber")) {
									accountNo = obj1.getString("accountNumber");
								} else {
									accountNo = "";
								}

							if (obj1.has("errorDetail"))
								if (!obj1.isNull("errorDetail")) {
									JSONArray arr = obj1.getJSONArray("errorDetail");
									errorDesc = arr.getJSONObject(0).getString("errorDesc");
								} else {
									errorDesc = "";
								}

							// && obj1.getString("responseMessage").equalsIgnoreCase("SUCCESS")
							if (obj1.getString("responseCode").toString().equalsIgnoreCase("00")) {
								log.debug("Savings Account creation successful");

								// INSERT RECORD INTO ACCT OPENING LOG
								resourceRepository.insertAcctOpeningLog(sourceType, sessionId, accountNo, cif1,
										mobilePhone1, bvn, acctType, Constants.BANK_ID,
										resBVNObj.getString("streetNo"), resBVNObj.getString("emailAddress"),
										schmCode, "00", cardPickupAddress, preferredNameonCard);

								if (!bvn.isEmpty()) {
									txtMsg = "Welcome to Parallex. Your Account has been opened. Your account number is: "
											+ accountNo
											+ ". Please dial our shortcode to continue enjoying parallex bank service on the USSD platform";
								} else {
									txtMsg = "Welcome to Parallex. Your Account has been opened. Your account number is: "
											+ accountNo
											+ ". Please visit the nearest Parallex Branch to update your details.";
								}

								// Invoke sms service here..............
								sendSms(txtMsg, mobilePhone1);
								// String smsResp = "00";

								JSONObject jsonReq = new JSONObject();
								// Invoke Update Account Mandate WS
								log.debug("Invoking Update Account Mandate WS");
								jsonReq.put("accountNumber", accountNo);
								jsonReq.put("base64Picture", resBVNObj.getString("base64Image"));

								String mandateResp = ServiceCall.callPostingSvc(updateAccountMandateUrl,
										jsonReq.toString());
								// String mandateResp = "{\r\n \"requestId\":
								// \"a678bcdf-bb31-4c26-bdf6-5b4b84fe1df8\",\r\n \"responseCode\":
								// \"00\",\r\n \"responseDescription\": \"Success\",\r\n
								// \"responseDetails\": null\r\n}";

								if (mandateResp != null) {
									JSONObject obj6 = new JSONObject(mandateResp);
									log.debug("Update Account Mandate responseCode: "
											+ obj6.getString("responseCode"));
									if (obj6.getString("responseCode").equalsIgnoreCase("00")) {
										log.debug("Update Account Mandate successful");
										resourceRepository.updateAcctOpeningLog(sessionId, "Y");
										resObj.put("statusCode", "00");
										resObj.put("statusMsg", "Success");
										resObj.put("acctnum", accountNo);
										resObj.put("cifid", cif);
										return resObj.toString();
									} else {
										log.debug("Update Account Mandate not successful");

										resourceRepository.updateAcctOpeningLog(sessionId, "Y");

										resObj.put("statusCode", "00");
										resObj.put("statusMsg", "Success");
										resObj.put("acctnum", accountNo);
										resObj.put("cifid", cif);
										resObj.put("responseMessage",
												"Acount Opening Successful but Mandate upload failed");
										return resObj.toString();
									}

								} else {
									log.debug("Update Account Mandate return null");
									resourceRepository.updateAcctOpeningLog(sessionId, "N");
									resObj.put("statusCode", "11");
									resObj.put("statusMsg", "Failed");
									resObj.put("responseMessage", "Acount Opening Failed");
									return resObj.toString();
								}

							} else {
								log.debug("Savings Account creation failed");
								resObj.put("statusCode", "11");
								resObj.put("statusMsg", "Failed");
								resObj.put("responseMessage", "Acount Opening Failed");
								return resObj.toString();

							}

						} else {
							log.debug("savings account creation returned null");
							// invoke retry mechanism in spring boot
							log.debug("Savings Account creation failed");
							resObj.put("statusCode", "11");
							resObj.put("statusMsg", "Failed");
							resObj.put("responseMessage", "Acount Opening Failed");
							return resObj.toString();

						}

					} else {

						// Invoke Retail Account Creation
						acctResponse = retailAccountOpening(title, accountCurrencyCode, resBVNObj.getString("streetNo"),
								resBVNObj.getString("firstName"), resBVNObj.getString("lastName"),
								resBVNObj.getString("middleName"), resBVNObj.getString("gender"),
								resBVNObj.getString("dob"), resBVNObj.getString("phonNumber1"),
								resBVNObj.getString("phonNumber2"), resBVNObj.getString("phonNumber3"),
								resBVNObj.getString("emailAddress"), sol_id, resBVNObj.getString("nationality"),
								resBVNObj.getString("maritalStatus"), city);

						if (acctResponse != null) {
							JSONObject obj2 = new JSONObject(acctResponse);

							log.debug("responseCode: " + obj2.getString("ResponseCode"));

							if (obj2.has("CIF_id"))
								if (!obj2.isNull("CIF_id")) {
									cif = obj2.getString("CIF_id");
									// qp.setCifId(cif);
								} else {
									cif = "";
								}

							if (obj2.has("ErrorDetail"))
								if (!obj2.isNull("ErrorDetail")) {
									JSONArray arr = obj2.getJSONArray("ErrorDetail");
									errorDesc = arr.getJSONObject(0).getString("ErrorDesc");
								} else {
									errorDesc = "";
								}

							if (obj2.getString("ResponseCode").equalsIgnoreCase("00")
									&& obj2.getString("ResponseMessage").equalsIgnoreCase("SUCCESS")) {
								log.debug("Retail customer creation successful");
								// log.debug("Invoke Savings Account Creation WS");
								// Invoke Savings Account Creation WS
								savAcctResp = savingsAccountCreation(cif, bvn, referralCode, schmCode, schmType);

								if (savAcctResp != null) {

									JSONObject obj1 = new JSONObject(savAcctResp);

									log.debug(
											"Savings Account Creation ResponseCode: " + obj1.getString("responseCode"));

									if (obj1.has("cif"))
										if (!obj1.isNull("cif")) {
											cif1 = obj1.getString("cif");

										} else {
											cif1 = "";
										}

									if (obj1.has("accountNumber"))
										if (!obj1.isNull("accountNumber")) {
											accountNo = obj1.getString("accountNumber");
											// qp.setAccountNo(accountNo);
										} else {
											accountNo = "";
										}

									if (obj1.has("errorDetail"))
										if (!obj1.isNull("errorDetail")) {
											JSONArray arr = obj1.getJSONArray("errorDetail");
											errorDesc = arr.getJSONObject(0).getString("errorDesc");
										} else {
											errorDesc = "";
										}

									// && obj1.getString("responseMessage").equalsIgnoreCase("SUCCESS")
									if (obj1.getString("responseCode").equalsIgnoreCase("00")) {
										log.debug("Savings Account creation successful");
										// INSERT RECORD INTO ACCT OPENING LOG
										resourceRepository.insertAcctOpeningLog(sourceType, sessionId, accountNo, cif,
												mobilePhone1, bvn, acctType, Constants.BANK_ID,
												resBVNObj.getString("streetNo"), resBVNObj.getString("emailAddress"),
												schmCode, "00", cardPickupAddress, preferredNameonCard);

										if (!reqObj.getString("bvn").isEmpty()) {
											txtMsg = "Welcome to Parallex. Your Account has been opened. Your account number is: "
													+ accountNo
													+ ". Please dial our shortcode to continue enjoying parallex bank service on the USSD platform";
										} else {
											txtMsg = "Welcome to Parallex. Your Account has been opened. Your account number is: "
													+ accountNo
													+ ". Please visit the nearest Parallex Branch to update your details.";
										}

										// Invoke sms service here..............
										sendSms(txtMsg, mobilePhone1);
										// String smsResp = "00";

										JSONObject jsonReq = new JSONObject();
										// Invoke Update Account Mandate WS
										log.debug("Invoking Update Account Mandate WS");
										jsonReq.put("accountNumber", accountNo);
										jsonReq.put("base64Picture", resBVNObj.getString("base64Image"));

										String mandateResp = ServiceCall.callPostingSvc(updateAccountMandateUrl,
												jsonReq.toString());
										// String mandateResp = "{\r\n \"requestId\":
										// \"a678bcdf-bb31-4c26-bdf6-5b4b84fe1df8\",\r\n \"responseCode\":
										// \"00\",\r\n \"responseDescription\": \"Success\",\r\n
										// \"responseDetails\": null\r\n}";

										if (mandateResp != null) {
											JSONObject obj6 = new JSONObject(mandateResp);
											log.debug("Update Account Mandate responseCode: "
													+ obj6.getString("responseCode"));
											if (obj6.getString("responseCode").equalsIgnoreCase("00")) {
												log.debug("Update Account Mandate successful");
												resourceRepository.updateAcctOpeningLog(sessionId, "Y");
												resObj.put("statusCode", "00");
												resObj.put("statusMsg", "Success");
												resObj.put("acctnum", accountNo);
												resObj.put("cifid", cif);

											} else {
												log.debug("Update Account Mandate not successful");

												resourceRepository.updateAcctOpeningLog(sessionId, "Y");

												resObj.put("statusCode", "00");
												resObj.put("statusMsg", "Success");
												resObj.put("acctnum", accountNo);
												resObj.put("cifid", cif);
												resObj.put("responseMessage",
														"Acount Opening Successful but Mandate upload failed");
												return resObj.toString();
											}

										} else {
											log.debug("Update Account Mandate return null");
											resourceRepository.updateAcctOpeningLog(sessionId, "N");
											resObj.put("statusCode", "11");
											resObj.put("statusMsg", "Failed");
											resObj.put("responseMessage", "Acount Opening Failed");
											return resObj.toString();
										}
									} else {
										log.debug("Savings Account creation failed");
										resObj.put("statusCode", "11");
										resObj.put("statusMsg", "Failed");
										resObj.put("responseMessage", "Acount Opening Failed");
										return resObj.toString();
									}
								} else {
									log.debug("Savings Account creation failed");
									resObj.put("statusCode", "11");
									resObj.put("statusMsg", "Failed");
									resObj.put("responseMessage", "Acount Opening Failed");
									return resObj.toString();
								}

							} else {
								// Retail customer creation
								log.debug("Retail customer creation failed");
								resObj.put("statusCode", "11");
								resObj.put("statusMsg", "Failed");
								resObj.put("responseMessage", "Acount Opening Failed");
								return resObj.toString();
							}

						} else {
							// respo acctResponse
							log.debug("Retail Account creation failed");
							resObj.put("statusCode", "11");
							resObj.put("statusMsg", "Failed");
							resObj.put("responseMessage", "Acount Opening Failed");
							return resObj.toString();
						}
					}

				} else {
					resObj.put("statusCode", "07");
					resObj.put("statusMsg", "Failed");
					resObj.put("responseMessage", "BVN Validation Failed");
					return resObj.toString();// mobilephone
				}
			} // bvn validation

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			resObj.put("statusMsg", "System Error");

			return resObj.toString();

		}

		return resObj.toString();
	}

	@Override
	public String accountCreation1(String req) throws Exception {
		JSONObject resObj = new JSONObject();
		JSONObject reqObj = new JSONObject(req);
		String acctType = "";
		String schmCode = "";
		String schmType = "";
		String title = "";
		String sourceType = "";
		String referralCode = "";
		String cardPickupAddress = "";
		String minorInd = "";
		String sol_id = "";
		String accountNo = "";
		String cif = "";
		String cif1 = "";
		String cif_id = "";
		String mobilePhone1 = "";
		String mobilePhone2 = "";
		String phone2 = "";
		String telcoName = "";
		String dob1 = "";
		String issueDate = "";
		String txtMsg = "";
		String resp = "";
		String acctResponse = null;
		String savAcctResp = null;
		String errorDesc = "";
		String acctNum = null;

		String bvn = "";
		String lastName = "";
		String firstName = "";
		String middleName = "";
		String cust_phone_num = "";
		String accountCurrencyCode = "";
		String preferredNameonCard = "";
		String glSubHeadCode = "";
		String country = "";
		String city = "";
		String emailAddr = "";
		String response = null;
		// log.debug("msisdn >>> " + reqObj.getString("msisdn"));
		// log.debug("dob >>> " + reqObj.getString("dob"));
		String dob = reqObj.getString("dob").replace("/", "-");

		if (reqObj.has("source_id") && !reqObj.getString("source_id").equalsIgnoreCase("")) {

			sourceType = reqObj.getString("source_id");

		} else {
			log.debug("Source Id field in null");
			resObj.put("statusCode", "01");
			resObj.put("statusMsg", "Failed");
			resObj.put("responseMessage", "Source Id Field is required");
			return resObj.toString();
		}

		if (reqObj.has("bvn") && !reqObj.getString("bvn").equalsIgnoreCase("")) {

			bvn = reqObj.getString("bvn");

		} else {
			log.debug("BVN field in null");
			resObj.put("statusCode", "02");
			resObj.put("statusMsg", "Failed");
			resObj.put("responseMessage", "BVN Field is required");
			return resObj.toString();
		}

		if (reqObj.has("phoneNumber") && !reqObj.getString("phoneNumber").equalsIgnoreCase("")) {

			cust_phone_num = reqObj.getString("phoneNumber");

		} else {
			log.debug("Phone Number field in null");
			resObj.put("statusCode", "04");
			resObj.put("statusMsg", "Failed");
			resObj.put("responseMessage", "Phone Number Field is required");
			return resObj.toString();
		}

		if (reqObj.has("lastName") && !reqObj.getString("lastName").equalsIgnoreCase("")) {

			lastName = reqObj.getString("lastName");
		} else {
			lastName = "";
		}

		if (reqObj.has("firstName") && !reqObj.getString("firstName").equalsIgnoreCase("")) {

			firstName = reqObj.getString("firstName");
		} else {
			firstName = "";
		}

		if (reqObj.has("middleName") && !reqObj.getString("middleName").equalsIgnoreCase("")) {

			middleName = reqObj.getString("middleName");
		} else {
			middleName = "";
		}

		if (reqObj.has("acctType") && !reqObj.getString("acctType").equalsIgnoreCase("")) {

			acctType = reqObj.getString("acctType");
		} else {
			acctType = "";
		}

		if (reqObj.has("schmCode") && !reqObj.getString("schmCode").equalsIgnoreCase("")) {

			schmCode = reqObj.getString("schmCode");
		} else {
			schmCode = "";
		}

		if (reqObj.has("glSubHeadCode") && !reqObj.getString("glSubHeadCode").equalsIgnoreCase("")) {

			glSubHeadCode = reqObj.getString("glSubHeadCode");
		} else {
			glSubHeadCode = "";
		}

		if (reqObj.has("accountCurrencyCode") && !reqObj.getString("accountCurrencyCode").equalsIgnoreCase("")) {

			accountCurrencyCode = reqObj.getString("accountCurrencyCode");
		} else {
			accountCurrencyCode = "";
		}

		if (reqObj.has("referral_code") && !reqObj.getString("referral_code").equalsIgnoreCase("")) {

			referralCode = reqObj.getString("referral_code");
		} else {
			referralCode = "";
		}

		if (reqObj.has("cardPickupAddress") && !reqObj.getString("cardPickupAddress").equalsIgnoreCase("")) {

			cardPickupAddress = reqObj.getString("cardPickupAddress");
		} else {
			cardPickupAddress = "";
		}

		if (reqObj.has("preferredNameonCard") && !reqObj.getString("preferredNameonCard").equalsIgnoreCase("")) {

			preferredNameonCard = reqObj.getString("preferredNameonCard");
		} else {
			preferredNameonCard = "";
		}

		if (reqObj.has("minorInd") && !reqObj.getString("minorInd").equalsIgnoreCase("")) {

			minorInd = reqObj.getString("minorInd"); // check the customer is a minor or not (Y/N)
		} else {
			minorInd = "";
		}

		String sessionId = GenerateRequestid();

		Integer count = resourceRepository.getSchemeCodeNo(sourceType, schmCode, glSubHeadCode, accountCurrencyCode);
		if (count > 0) {
			log.debug("country/channel account opening config details...");
			if (count > 1) {
				log.debug("Cannot fetch country/channel account opening config details");
				resObj.put("statusCode", "06");
				resObj.put("statusMsg", "Failed");
				resObj.put("responseMessage",
						"There are multiple country/channel account opening config details. Kindly specify the scheme code");
				return resObj.toString();
			} else {
				AccountParam accountParam = resourceRepository.getAccountOpeningDefValues(sourceType, schmCode,
						glSubHeadCode, accountCurrencyCode);

				if (reqObj.has("solId") && !reqObj.getString("solId").equalsIgnoreCase("")) {

					sol_id = reqObj.getString("solId");
				} else {
					sol_id = accountParam.getSolId();
				}

				if (schmCode.equalsIgnoreCase("")) {

					schmCode = accountParam.getSchmCode();
				}

				if (glSubHeadCode.equalsIgnoreCase("")) {

					glSubHeadCode = accountParam.getGlSubHeadCode();
				}

				if (acctType.equalsIgnoreCase("")) {

					acctType = accountParam.getAcctType();
				}

				if (accountCurrencyCode.equalsIgnoreCase("")) {

					accountCurrencyCode = accountParam.getCrncyCode();
				}

				if (reqObj.has("schmType") && !reqObj.getString("schmType").equalsIgnoreCase("")) {

					schmType = reqObj.getString("schmType");
				} else {
					schmType = accountParam.getSchmCodeDesc();
				}

				if (reqObj.has("title") && !reqObj.getString("title").equalsIgnoreCase("")) {

					title = reqObj.getString("title");
				} else {
					title = accountParam.getTitle();
				}

				if (reqObj.has("country") && !reqObj.getString("country").equalsIgnoreCase("")) {

					country = reqObj.getString("country");
				} else {
					country = accountParam.getCountry();
				}

				if (reqObj.has("city") && !reqObj.getString("city").equalsIgnoreCase("")) {

					city = reqObj.getString("city");
				} else {
					city = accountParam.getCity();
				}

			}

		} else {
			log.debug("Cannot fetch country/channel account opening config details");
			resObj.put("statusCode", "05");
			resObj.put("statusMsg", "Failed");
			resObj.put("responseMessage", "Cannot fetch country/channel account opening config details");
			return resObj.toString();
		}

		String phoneNo = "+234" + "(0)" + cust_phone_num.substring(cust_phone_num.length() - 10);

		try {

			try {

				log.debug("Request to Dedup WS");

				JSONObject jRequest = new JSONObject();
				jRequest.put("acctType", acctType);
				jRequest.put("dob", dob);
				jRequest.put("firstName", firstName);
				jRequest.put("lastName", lastName);
				jRequest.put("middleName", middleName);
				jRequest.put("phoneNumber", phoneNo);
				jRequest.put("bvn", bvn);

				String resp1 = ServiceCall.callAuthService(Constants.DEDUP_URL, jRequest.toString());
				if (resp1.isEmpty() || resp1 == null) {
					log.debug("DEDUP WS returned null");

					resObj.put("statusCode", "09");
					resObj.put("statusMsg", "Failed");
					resObj.put("responseMessage", "Dedup returned null");

					return resObj.toString();

				}

				JSONObject obj = new JSONObject(resp1);

				if (obj.getString("statusCode").equalsIgnoreCase("00")
						&& obj.getString("statusMsg").equalsIgnoreCase("Success")) {
					cif_id = obj.getString("cifId");
					log.debug("Account Opening Cif ID: " + cif_id);

				} else {

					if (obj.getString("statusCode").equalsIgnoreCase("99")) {
						resObj.put("statusCode", obj.getString("statusCode"));
						resObj.put("statusMsg", "Failed");
						resObj.put("responseMessage", obj.getString("statusMsg"));

						return resObj.toString();
					} else {
						cif_id = "";
					}

				}

				if (!cif_id.equalsIgnoreCase("")) {

					acctNum = resourceRepository.getAccountbyCifid(schmCode, cif_id, accountCurrencyCode);

					if (!acctNum.equalsIgnoreCase("") || acctNum != null) {
						// && minorInd.equalsIgnoreCase("N")) {
						// if ((acctNum != null || acctNum != "" || !acctNum.isEmpty()) &&
						// minorInd.equalsIgnoreCase("N")) {
						// if ((!acctNum.equalsIgnoreCase("") || acctNum != null) &&
						// minorInd.equalsIgnoreCase("N")) {
						log.debug("Customer already opened account");
						resObj.put("statusCode", "42");
						resObj.put("statusMsg", "Failed");
						resObj.put("acctNum", acctNum);
						resObj.put("responseMessage",
								"Customer already opened account with the same scheme code and currency");

						return resObj.toString();
					}
				} // else {

				if (minorInd.equalsIgnoreCase("Y")) {
					String minRelnCifId = "";// this will be parent cifid
					String minReln = "Guardian";
					String minRelnGDCode = "FATHER";
				}

				String auth = getLoginDetails(Constants.USERNAME, Constants.PASSWORD);

				if (auth.isEmpty() || auth == "" || auth == null) {
					log.debug("Unable to generate token");
					resObj.put("statusCode", "03");
					resObj.put("statusMsg", "Failed");
					resObj.put("responseMessage", "No Authorization Header");

					return resObj.toString();
				}

				log.debug("Request to BVN Validation WS");
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("bvn", bvn);
				jsonRequest.put("channel", "1");

				String resp2 = ServiceCall.callWrapperService(auth, Constants.BVN_VALIDATION_URL,
						jsonRequest.toString());
				// String resp2 = "{\r\n \"responseCode\": \"00\",\r\n \"bvn\":
				// \"22395491913\",\r\n \"firstName\": \"ENEMOR\",\r\n \"middleName\": \"\",\r\n
				// \"lastName\": \"JOSEPHINE\",\r\n \"dateOfBirth\": \"20-Jan-1981\",\r\n
				// \"phoneNumber1\": \"08139181252\",\r\n \"registrationDate\": \"\",\r\n
				// \"enrollmentBank\": \"011\",\r\n \"enrollmentBranch\": \"LAPAL HOUSE\",\r\n
				// \"email\": \"josephine@yahoo.com\",\r\n \"gender\": \"Female\",\r\n
				// \"phoneNumber2\": \"\",\r\n \"levelOfAccount\": \"Level 1 - Low Level
				// Accounts\",\r\n \"lgaOfOrigin\": \"Ifelodun\",\r\n \"lgaOfResidence\":
				// \"Lekki\",\r\n \"maritalStatus\": \"Married\",\r\n \"nin\":
				// \"74336987786\",\r\n \"nameOnCard\": \"John John John\",\r\n \"nationality\":
				// \"Nigeria\",\r\n \"residentialAddress\": \"371, CHEVRON DRIVE ROAD, LEKKI,
				// LAGOS.\",\r\n \"stateOfOrigin\": \"Abia\",\r\n \"stateOfResidence\": \"Lagos
				// State\",\r\n \"title\": null,\r\n \"watchListed\": \"NO\",\r\n
				// \"base64Image\":
				// \"/9j/4AAQSkZJRgABAgAAAQABAAD/sdsdzsfdfffffsrtgzerdHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQ\"\r\n}";
				if (resp2.isEmpty() || resp2 == null) {
					log.debug("BVN Validation WS returned null");

					resObj.put("statusCode", "07");
					resObj.put("statusMsg", "Failed");
					resObj.put("responseMessage", "BVN Validation Failed");

					return resObj.toString();

				} else {

					JSONObject obj5 = new JSONObject(resp2);

					if (obj5.getString("responseCode").equalsIgnoreCase("00")) {

						if (obj5.has("dateOfBirth"))
							if (!obj5.isNull("dateOfBirth")) {

								String mob1 = obj5.getString("phoneNumber1");

								mobilePhone1 = "234" + mob1.substring(mob1.length() - 10);
								log.debug("mobilePhone1 >>> " + mobilePhone1);

								if (obj5.has("phoneNumber2") && obj5.getString("phoneNumber2").equalsIgnoreCase("")) {
									log.debug("mobilePhone2 is empty ");
									mobilePhone2 = "";

								} else {
									log.debug("mobilePhone2 is not empty ");
									String mob2 = obj5.getString("phoneNumber2");
									mobilePhone2 = "234" + mob2.substring(mob2.length() - 10);
								}

							} else {
								log.debug("Date Birth field is null");

								resObj.put("statusCode", "08");
								resObj.put("statusMsg", "Failed");
								resObj.put("responseMessage", "Date Birth field is null");
								return resObj.toString();
							}

						if (mobilePhone1.equalsIgnoreCase(cust_phone_num)
								|| mobilePhone2.equalsIgnoreCase(cust_phone_num)) {
							JSONObject resBVNObj = new JSONObject();
							resBVNObj.put("lastName", obj5.getString("lastName"));
							resBVNObj.put("firstName", obj5.getString("firstName"));
							resBVNObj.put("middleName", obj5.getString("middleName"));
							resBVNObj.put("streetNo", obj5.getString("residentialAddress"));

							if (!obj5.isNull("title")) {
								title = obj5.getString("title").toUpperCase();
							} else {

							}

							if (obj5.getString("gender").toString().toUpperCase().equals("FEMALE")) {
								resBVNObj.put("gender", "F");
								resBVNObj.put("title", "MRS");
							} else {
								resBVNObj.put("gender", "M");
								resBVNObj.put("title", "MR");
							}

							if (obj5.isNull("email") || obj5.getString("email").equalsIgnoreCase("")) {
								emailAddr = "noemail@parallexbank.com";
							} else {
								emailAddr = obj5.getString("email");
							}

							resBVNObj.put("phonNumber1", "234");
							resBVNObj.put("phonNumber2", "0");
							resBVNObj.put("phonNumber3", cust_phone_num.substring(cust_phone_num.length() - 10));
							resBVNObj.put("emailAddress", emailAddr);
							resBVNObj.put("loclGovt", obj5.getString("lgaOfResidence"));
							resBVNObj.put("loclOrigin", obj5.getString("lgaOfOrigin"));
							resBVNObj.put("maritalStatus", obj5.getString("maritalStatus"));
							resBVNObj.put("nationality", obj5.getString("nationality"));
							// resBVNObj.put("residentialAddress", obj.getString("residentialAddress"));
							resBVNObj.put("stateOfOrigin", obj5.getString("stateOfOrigin"));
							resBVNObj.put("stateOfResidence", obj5.getString("stateOfResidence"));
							resBVNObj.put("typOfId", "IDPRF");
							resBVNObj.put("issueAuthority", "FGN");
							resBVNObj.put("registrationNumber", obj5.getString("nin"));
							resBVNObj.put("base64Image", obj5.getString("base64Image").replace("\"", ""));

							// converting dob from 30-Jan-1949 to 30-01-1949
							dob1 = AppUtil.getValueDateTime1(obj5.getString("dateOfBirth"));
							// issueDate = AppUtil.getValueDateTime1(obj.getString("registrationDate"));
							log.debug("dob1 >>> " + dob1);
							// log.debug("issueDate >>> " + issueDate);
							resBVNObj.put("dob", dob1);

							// Invoke Retail Account Creation
							acctResponse = retailAccountOpening(title, accountCurrencyCode,
									resBVNObj.getString("streetNo"), resBVNObj.getString("firstName"),
									resBVNObj.getString("lastName"), resBVNObj.getString("middleName"),
									resBVNObj.getString("gender"), resBVNObj.getString("dob"),
									resBVNObj.getString("phonNumber1"), resBVNObj.getString("phonNumber2"),
									resBVNObj.getString("phonNumber3"), resBVNObj.getString("emailAddress"), sol_id,
									resBVNObj.getString("nationality"), resBVNObj.getString("maritalStatus"), city);

							if (acctResponse != null) {
								JSONObject obj2 = new JSONObject(acctResponse);

								log.debug("responseCode: " + obj2.getString("ResponseCode"));

								if (obj2.has("CIF_id"))
									if (!obj2.isNull("CIF_id")) {
										cif = obj2.getString("CIF_id");
										// qp.setCifId(cif);
									} else {
										cif = "";
									}

								if (obj2.has("ErrorDetail"))
									if (!obj2.isNull("ErrorDetail")) {
										JSONArray arr = obj2.getJSONArray("ErrorDetail");
										errorDesc = arr.getJSONObject(0).getString("ErrorDesc");
									} else {
										errorDesc = "";
									}

								if (obj2.getString("ResponseCode").equalsIgnoreCase("00")
										&& obj2.getString("ResponseMessage").equalsIgnoreCase("SUCCESS")) {
									log.debug("Retail customer creation successful");
									// log.debug("Invoke Savings Account Creation WS");
									// Invoke Savings Account Creation WS
									savAcctResp = savingsAccountCreation(cif, bvn, referralCode, schmCode, schmType);

									if (savAcctResp != null) {

										JSONObject obj1 = new JSONObject(savAcctResp);

										log.debug("Savings Account Creation ResponseCode: "
												+ obj1.getString("responseCode"));

										if (obj1.has("cif"))
											if (!obj1.isNull("cif")) {
												cif1 = obj1.getString("cif");

											} else {
												cif1 = "";
											}

										if (obj1.has("accountNumber"))
											if (!obj1.isNull("accountNumber")) {
												accountNo = obj1.getString("accountNumber");
												// qp.setAccountNo(accountNo);
											} else {
												accountNo = "";
											}

										if (obj1.has("errorDetail"))
											if (!obj1.isNull("errorDetail")) {
												JSONArray arr = obj1.getJSONArray("errorDetail");
												errorDesc = arr.getJSONObject(0).getString("errorDesc");
											} else {
												errorDesc = "";
											}

										// && obj1.getString("responseMessage").equalsIgnoreCase("SUCCESS")
										if (obj1.getString("responseCode").equalsIgnoreCase("00")) {
											log.debug("Savings Account creation successful");
											// INSERT RECORD INTO ACCT OPENING LOG
											resourceRepository.insertAcctOpeningLog(sourceType, sessionId, accountNo,
													cif, cust_phone_num, bvn, acctType, Constants.BANK_ID,
													resBVNObj.getString("streetNo"),
													resBVNObj.getString("emailAddress"), schmCode, "00",
													cardPickupAddress, preferredNameonCard);

											if (!reqObj.getString("bvn").isEmpty()) {
												txtMsg = "Welcome to Parallex. Your Account has been opened. Your account number is: "
														+ accountNo
														+ ". Please dial our shortcode to continue enjoying parallex bank service on the USSD platform";
											} else {
												txtMsg = "Welcome to Parallex. Your Account has been opened. Your account number is: "
														+ accountNo
														+ ". Please visit the nearest Parallex Branch to update your details.";
											}

											// Invoke sms service here..............
											sendSms(txtMsg, cust_phone_num);
											// String smsResp = "00";

											JSONObject jsonReq = new JSONObject();
											// Invoke Update Account Mandate WS
											log.debug("Invoking Update Account Mandate WS");
											jsonReq.put("accountNumber", accountNo);
											jsonReq.put("base64Picture", resBVNObj.getString("base64Image"));

											String mandateResp = ServiceCall.callPostingSvc(updateAccountMandateUrl,
													jsonReq.toString());
											// String mandateResp = "{\r\n \"requestId\":
											// \"a678bcdf-bb31-4c26-bdf6-5b4b84fe1df8\",\r\n \"responseCode\":
											// \"00\",\r\n \"responseDescription\": \"Success\",\r\n
											// \"responseDetails\": null\r\n}";

											if (mandateResp != null) {
												JSONObject obj6 = new JSONObject(mandateResp);
												log.debug("Update Account Mandate responseCode: "
														+ obj6.getString("responseCode"));
												if (obj6.getString("responseCode").equalsIgnoreCase("00")) {
													log.debug("Update Account Mandate successful");
													resourceRepository.updateAcctOpeningLog(sessionId, "Y");
													resObj.put("statusCode", "00");
													resObj.put("statusMsg", "Success");
													resObj.put("acctnum", accountNo);
													resObj.put("cifid", cif);

												} else {
													log.debug("Update Account Mandate not successful");

													resourceRepository.updateAcctOpeningLog(sessionId, "Y");

													resObj.put("statusCode", "00");
													resObj.put("statusMsg", "Success");
													resObj.put("acctnum", accountNo);
													resObj.put("cifid", cif);
													resObj.put("responseMessage",
															"Acount Opening Successful but Mandate upload failed");
													return resObj.toString();
												}

											} else {
												log.debug("Update Account Mandate return null");
												resourceRepository.updateAcctOpeningLog(sessionId, "N");
												resObj.put("statusCode", "11");
												resObj.put("statusMsg", "Failed");
												resObj.put("responseMessage", "Acount Opening Failed");
												return resObj.toString();
											}
										} else {
											log.debug("Savings Account creation failed");
											resObj.put("statusCode", "11");
											resObj.put("statusMsg", "Failed");
											resObj.put("responseMessage", "Acount Opening Failed");
											return resObj.toString();
										}
									} else {
										log.debug("Savings Account creation failed");
										resObj.put("statusCode", "11");
										resObj.put("statusMsg", "Failed");
										resObj.put("responseMessage", "Acount Opening Failed");
										return resObj.toString();
									}

								} else {
									// Retail customer creation
									log.debug("Retail customer creation failed");
									resObj.put("statusCode", "11");
									resObj.put("statusMsg", "Failed");
									resObj.put("responseMessage", "Acount Opening Failed");
									return resObj.toString();
								}

							} else {
								// respo acctResponse
								log.debug("Retail Account creation failed");
								resObj.put("statusCode", "11");
								resObj.put("statusMsg", "Failed");
								resObj.put("responseMessage", "Acount Opening Failed");
								return resObj.toString();
							}
						} else {
							resObj.put("statusCode", "10");
							resObj.put("statusMsg", "Failed");
							resObj.put("responseMessage", "BVN Validation Failed. Phone does not Match BVN data");
							return resObj.toString();
						}
					} else {
						resObj.put("statusCode", "07");
						resObj.put("statusMsg", "Failed");
						resObj.put("responseMessage", "BVN Validation Failed");
						return resObj.toString();// mobilephone
					}
				} // bvn validation

			} catch (Exception e) {

				e.printStackTrace();
				log.error("Error >>> " + e.fillInStackTrace());
				resObj.put("statusCode", "99");
				resObj.put("statusMsg", "System Error");

				return resObj.toString();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error calling FI: " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			resObj.put("statusMsg", "An error occurred while processing the request.");
			// ResponseHandler.generateResponse("Error calling FI", HttpStatus.BAD_GATEWAY);

		}
		return resObj.toString();
	}

	@Override
	public String validationBVN(String req) throws Exception {

		JSONObject bvnObj = new JSONObject();
		JSONObject reqObj = new JSONObject(req);
		String bvn = reqObj.getString("bvn");
		String response = null;

		log.debug("bvn >>> " + bvn);

		try {
			if (bvn == "" || bvn.isEmpty() || bvn == null) {
				log.debug("BVN field is empty");
				bvnObj.put("statusCode", "02");
				bvnObj.put("statusMsg", "BVN field is required");

				return bvnObj.toString();
			} else {

				String auth = getLoginDetails(Constants.USERNAME, Constants.PASSWORD);

				if (auth.isEmpty() || auth == "" || auth == null) {
					log.debug("Unable to generate token");
					bvnObj.put("statusCode", "01");
					bvnObj.put("statusMsg", "No Authorization Header");

					return bvnObj.toString();
				}

				log.debug("Request to BVN Validation WS");
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("bvn", bvn);
				jsonRequest.put("channel", "1");

				String resp1 = ServiceCall.callWrapperService(auth, Constants.BVN_VALIDATION_URL,
						jsonRequest.toString());
				if (resp1.isEmpty() || resp1 == null) {
					log.debug("BVN Validation WS returned null");
					bvnObj.put("statusCode", "03");
					bvnObj.put("statusMsg", "Failed");
					return bvnObj.toString();

				} else {

					JSONObject obj = new JSONObject(resp1);
					String mob1 = "";
					String mob2 = "";
					String mobilePhone2 = "";
					String mobilePhone = "";

					if (obj.getString("responseCode").equalsIgnoreCase("00")) {
						log.debug("BVN validation successful");

						if (obj.has("phoneNumber1") && !obj.getString("phoneNumber1").equalsIgnoreCase("")) {
							mob1 = obj.getString("phoneNumber1");
							mobilePhone = "234" + mob1.substring(mob1.length() - 10);
							log.debug("mobilePhone >>> " + mobilePhone);
						} else {

							log.debug("phoneNumber1 is empty ");
							if (obj.has("phoneNumber2") && !obj.getString("phoneNumber2").equalsIgnoreCase("")) {
								log.debug("phoneNumber2 is not empty ");
								mob2 = obj.getString("phoneNumber2");
								mobilePhone = "234" + mob2.substring(mob2.length() - 10);
								log.debug("mobilePhone >>> " + mobilePhone);
							} else {
								log.debug("phoneNumber2 is empty ");
								mobilePhone = "";
								log.debug("BVN Validation WS returned null");
								bvnObj.put("statusCode", "04");
								bvnObj.put("statusMsg", "Failed");
								return bvnObj.toString();

							}

						}

						log.debug("Request to Validation BVN and Generate OTP WS");
						JSONObject bvnRequest = new JSONObject();
						bvnRequest.put("bvn", bvn);
						response = ServiceCall.callAuthService(Constants.VALIDATE_BVN_URL, bvnRequest.toString());

						JSONObject obj1 = new JSONObject(response);
						if (response != null && obj1.getString("responseCode").equalsIgnoreCase("00")) {
							if (obj.has("email") && !obj.getString("email").equalsIgnoreCase("")) {
								String emailAddr[] = obj.getString("email").split("@");
								bvnObj.put("emailAddress", emailAddr[0].substring(0, 2) + "******@" + emailAddr[1]);
							} else {
								bvnObj.put("emailAddress", "");
							}

							bvnObj.put("statusCode", "00");
							bvnObj.put("statusMsg", "Success");
							bvnObj.put("bvn", obj.getString("bvn"));
							bvnObj.put("phoneNo", mobilePhone.substring(0, 4) + "******"
									+ mobilePhone.substring(mobilePhone.length() - 3));

							return bvnObj.toString();
						} else {
							log.debug("BVN validation successful but OTP generation not successful");
							bvnObj.put("statusCode", "05");
							bvnObj.put("statusMsg", "Failed");

							return bvnObj.toString();
						}

					} else {
						log.debug("BVN validation not successful");
						bvnObj.put("statusCode", "06");
						bvnObj.put("statusMsg", "Failed");

						return bvnObj.toString();
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			bvnObj.put("statusCode", "99");
			bvnObj.put("statusMsg", "System Malfunction");

			return bvnObj.toString();

		}

	}

	@Override
	public String bvnValidation(String req) throws Exception {

		JSONObject bvnObj = new JSONObject();
		JSONObject reqObj = new JSONObject(req);
		String bvn = reqObj.getString("bvn");

		log.debug("bvn >>> " + bvn);

		try {
			if (bvn == "" || bvn.isEmpty() || bvn == null) {
				log.debug("BVN field is empty");
				bvnObj.put("statusCode", "02");
				bvnObj.put("statusMsg", "BVN field is required");

				return bvnObj.toString();
			} else {

				String auth = getLoginDetails(Constants.USERNAME, Constants.PASSWORD);

				if (auth.isEmpty() || auth == "" || auth == null) {
					log.debug("Unable to generate token");
					bvnObj.put("statusCode", "01");
					bvnObj.put("statusMsg", "No Authorization Header");

					return bvnObj.toString();
				}

				log.debug("Request to BVN Validation WS");
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("bvn", bvn);
				jsonRequest.put("channel", "1");

				String resp1 = ServiceCall.callWrapperService(auth, Constants.BVN_VALIDATION_URL,
						jsonRequest.toString());
				if (resp1.isEmpty() || resp1 == null) {
					log.debug("BVN Validation WS returned null");
					bvnObj.put("statusCode", "03");
					bvnObj.put("statusMsg", "Failed");
					return bvnObj.toString();

				} else {

					JSONObject obj = new JSONObject(resp1);

					if (obj.getString("responseCode").equalsIgnoreCase("00")) {
						log.debug("BVN validation successful");

						bvnObj.put("statusCode", "00");
						bvnObj.put("statusMsg", "Success");
						bvnObj.put("bvn", obj.getString("bvn"));
						
						if (!obj.isNull("title")) {
							bvnObj.put("title", obj.getString("title").toString().toUpperCase());
							//title = obj5.getString("title").toUpperCase();
						} else {

						}

						String genderFromApi = obj.getString("gender").toString();
						log.debug("Gender from BVN API >>> "+genderFromApi.toUpperCase());
						if (genderFromApi.toUpperCase().equals("FEMALE")) {
//							bvnObj.put("gender", "F");
							bvnObj.put("title", "MRS");
						} else {
//							bvnObj.put("gender", "M");
							bvnObj.put("title", "MR");
						}
						
//						bvnObj.put("title", obj.getString("title"));
						bvnObj.put("lastName", obj.getString("lastName"));
						bvnObj.put("firstName", obj.getString("firstName"));
						bvnObj.put("middleName", obj.getString("middleName"));

						return bvnObj.toString();
					} else {
						log.debug("BVN validation not successful");
						bvnObj.put("statusCode", "03");
						bvnObj.put("statusMsg", "Failed");

						return bvnObj.toString();
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			bvnObj.put("statusCode", "99");
			bvnObj.put("statusMsg", "System Malfunction");

			return bvnObj.toString();

		}

	}

	@Override
	public String bvnValidation1(String req) throws Exception {

		JSONObject bvnObj = new JSONObject();
		JSONObject reqObj = new JSONObject(req);
		String bvn = "";
		String phoneNo = "";
		String dob = "";
		String dob1 = "";
		String mobilePhone1 = "";
		String mobilePhone2 = "";
		String phoneNumber = "";
		String image = "";
		String bvnImage = "";
		boolean usable = false;
		String evaluationResult = "";

		log.debug("bvn >>> " + bvn);

		try {

			if (reqObj.has("bvn") && !reqObj.getString("bvn").equalsIgnoreCase("")) {

				bvn = reqObj.getString("bvn");
			} else {
				log.debug("BVN field is empty");
				bvnObj.put("statusCode", "01");
				bvnObj.put("statusMsg", "Failed");
				bvnObj.put("responseMsg", "BVN field is required");
				bvnObj.put("Match", Boolean.valueOf(false));

				return bvnObj.toString();
			}

			if (reqObj.has("phoneNo") && !reqObj.getString("phoneNo").equalsIgnoreCase("")) {

				phoneNo = reqObj.getString("phoneNo");
				phoneNumber = "234" + phoneNo.substring(phoneNo.length() - 10);
			} else {
				log.debug("PhoneNo field is empty");
				bvnObj.put("statusCode", "02");
				bvnObj.put("statusMsg", "Failed");
				bvnObj.put("responseMsg", "PhoneNo field is required");
				bvnObj.put("Match", Boolean.valueOf(false));

				return bvnObj.toString();
			}

			if (reqObj.has("dob") && !reqObj.getString("dob").equalsIgnoreCase("")) {

				dob = reqObj.getString("dob");
			} else {
				log.debug("DOB field is empty");
				bvnObj.put("statusCode", "03");
				bvnObj.put("statusMsg", "Failed");
				bvnObj.put("responseMsg", "DOB field is required");
				bvnObj.put("Match", Boolean.valueOf(false));

				return bvnObj.toString();
			}

			if (reqObj.has("image") && !reqObj.getString("image").equalsIgnoreCase("")) {

				image = reqObj.getString("image");
			} else {
				image = "";
			}

			String auth = getLoginDetails(Constants.USERNAME, Constants.PASSWORD);

			if (auth.isEmpty() || auth == "" || auth == null) {
				log.debug("Unable to generate token");
				bvnObj.put("statusCode", "04");
				bvnObj.put("statusMsg", "No Authorization Header");

				return bvnObj.toString();
			}

			log.debug("Request to BVN Validation WS");
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("bvn", bvn);
			jsonRequest.put("channel", "1");

			String resp1 = ServiceCall.callWrapperService(auth, Constants.BVN_VALIDATION_URL, jsonRequest.toString());
			// String resp1 = "{\r\n \"responseCode\": \"00\",\r\n \"bvn\":
			// \"12345678909\",\r\n \"firstName\": \"Oludotun\",\r\n \"middleName\":
			// \"Akeeb\",\r\n \"lastName\": \"Adigun\",\r\n \"dateOfBirth\":
			// \"31-Dec-2000\",\r\n \"phoneNumber1\": \"08053531833\",\r\n
			// \"registrationDate\": \"\",\r\n \"enrollmentBank\": \"011\",\r\n
			// \"enrollmentBranch\": \"LAPAL HOUSE\",\r\n \"email\": \"dot@yahoo.com\",\r\n
			// \"gender\": \"Male\",\r\n \"phoneNumber2\": \"\",\r\n \"levelOfAccount\":
			// \"Level 1 - Low Level Accounts\",\r\n \"lgaOfOrigin\": \"Ifelodun\",\r\n
			// \"lgaOfResidence\": \"Mushin\",\r\n \"maritalStatus\": \"Single\",\r\n
			// \"nin\": \"74336987786\",\r\n \"nameOnCard\": \"John John John\",\r\n
			// \"nationality\": \"Nigeria\",\r\n \"residentialAddress\": \"371, AGEGE MOTOR
			// ROAD, CHALLENGE, MUSHIN, LAGOS.\",\r\n \"stateOfOrigin\": \"Kwara
			// State\",\r\n \"stateOfResidence\": \"Lagos State\",\r\n \"title\": null,\r\n
			// \"watchListed\": \"NO\",\r\n \"base64Image\":
			// \"/9j/4AAQSkZJRgABAQEAkACQAAD/4RCQRXhpZgAATU0AKgAAAAgABAE7AAIAAAAKAAAISodpAAQAAAABAAAIVJydAAEAAAAUAAAQdOocAAcAAAgMAAAAPgAAAAAc6gAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGRhb25fdXNlcgAAAeocAAcAAAgMAAAIZgAAAAAc6gAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAZABhAG8AbgBfAHUAcwBlAHIAAAD/4QpiaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLwA8P3hwYWNrZXQgYmVnaW49J++7vycgaWQ9J1c1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCc/Pg0KPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyI+PHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj48cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0idXVpZDpmYWY1YmRkNS1iYTNkLTExZGEtYWQzMS1kMzNkNzUxODJmMWIiIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIvPjxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSJ1dWlkOmZhZjViZGQ1LWJhM2QtMTFkYS1hZDMxLWQzM2Q3NTE4MmYxYiIgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIj48ZGM6Y3JlYXRvcj48cmRmOlNlcSB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPjxyZGY6bGk+ZGFvbl91c2VyPC9yZGY6bGk+PC9yZGY6U2VxPg0KCQkJPC9kYzpjcmVhdG9yPjwvcmRmOkRlc2NyaXB0aW9uPjwvcmRmOlJERj48L3g6eG1wbWV0YT4NCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgPD94cGFja2V0IGVuZD0ndyc/Pv/bAEMABwUFBgUEBwYFBggHBwgKEQsKCQkKFQ8QDBEYFRoZGBUYFxseJyEbHSUdFxgiLiIlKCkrLCsaIC8zLyoyJyorKv/bAEMBBwgICgkKFAsLFCocGBwqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKv/AABEIAl4BxQMBIgACEQEDEQH/xAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNU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\"\r\n}";
			if (resp1.isEmpty() || resp1 == null) {
				log.debug("BVN Validation WS returned null");
				bvnObj.put("statusCode", "05");
				bvnObj.put("statusMsg", "Failed");
				bvnObj.put("responseMsg", "BVN Validation returned null");
				bvnObj.put("Match", Boolean.valueOf(false));
				return bvnObj.toString();

			} else {

				JSONObject obj = new JSONObject(resp1);

				if (obj.getString("responseCode").equalsIgnoreCase("00")) {
					log.debug("BVN validation successful");

					if (obj.has("dateOfBirth") && !obj.isNull("dateOfBirth")) {
						// converting dob from 30-Jan-1949 to 30-01-1949
						dob1 = AppUtil.getValueDateTime1(obj.getString("dateOfBirth"));

						log.debug("dob1 >>> " + dob1);
						if (dob1.equalsIgnoreCase(dob)) {
							String mob1 = obj.getString("phoneNumber1");
							mobilePhone1 = "234" + mob1.substring(mob1.length() - 10);
							log.debug("mobilePhone1 >>> " + mobilePhone1);

							if (obj.has("phoneNumber2") && obj.getString("phoneNumber2").equalsIgnoreCase("")) {
								log.debug("mobilePhone2 is empty ");
								mobilePhone2 = "";

							} else {
								log.debug("mobilePhone2 is not empty ");
								String mob2 = obj.getString("phoneNumber2");
								mobilePhone2 = "234" + mob2.substring(mob2.length() - 10);
							}
							if (mobilePhone1.equalsIgnoreCase(phoneNumber)
									|| mobilePhone2.equalsIgnoreCase(phoneNumber)) {
								bvnImage = obj.getString("base64Image");

								UserDetails userDetails = resourceRepository.pullUserDetails(daonUser);

								if (userDetails.getCreateId() == null) {
									bvnObj.put("statusCode", "12");
									bvnObj.put("statusMsg", "Failed");
									bvnObj.put("responseMsg", "Credentials can not be fetched");
									bvnObj.put("Match", Boolean.valueOf(false));
									return bvnObj.toString();
								}

								if (image != "" || !image.equalsIgnoreCase("")) {

									log.debug("Request to IdentityX WS Add Sefie");
									JSONObject jsonReq = new JSONObject();
									jsonReq.put("format", "JPG");
									jsonReq.put("data", image);

									String resp2 = ServiceCall.callIdentityXService(Constants.IDENTITYX_ADD_SEFIE_URL
											+ userDetails.getCreateId() + "/face/samples", jsonReq.toString());
									if (resp2.isEmpty() || resp2 == null) {
										log.debug("IdentityX WS Add Sefie returned null");
										bvnObj.put("statusCode", "10");
										bvnObj.put("statusMsg", "Failed");
										bvnObj.put("responseMsg", "Add Selfie WS returned null");
										bvnObj.put("Match", Boolean.valueOf(false));
										return bvnObj.toString();

									} else {

										JSONObject obj2 = new JSONObject(resp2);

										if (obj2.has("items"))
											if (!obj2.isNull("items")) {
												JSONArray arr = obj2.getJSONArray("items");
												usable = arr.getJSONObject(0).getBoolean("usable");
												evaluationResult = arr.getJSONObject(0).getString("evaluationResult");
											} else {
												usable = false;
												evaluationResult = "";
											}

										if (usable == true && evaluationResult.equalsIgnoreCase("OK - (0)")) {
											log.debug("IdentityX WS Add Sefie successful");

											log.debug("Request to IdentityX WS Add Face Image");
											JSONObject jsonRequest1 = new JSONObject();
											jsonRequest1.put("captured", AppUtil.getValueDateTime());
											jsonRequest1.put("subtype", "TRUSTED_SOURCE_PROVIDED");
											JSONObject jsonRequest2 = new JSONObject();
											jsonRequest2.put("imageFormat", "JPG");
											jsonRequest2.put("value", bvnImage);
											jsonRequest1.put("sensitiveData", jsonRequest2);
											String resp3 = ServiceCall.callIdentityXService(
													Constants.IDENTITYX_ADD_FACE_IMAGE_URL + userDetails.getCreateId()
															+ "/idchecks/" + userDetails.getIdChecks() + "/faces",
													jsonRequest1.toString());
											if (resp3.isEmpty() || resp3 == null) {
												log.debug("IdentityX WS Add Face Image returned null");
												bvnObj.put("statusCode", "12");
												bvnObj.put("statusMsg", "Failed");
												bvnObj.put("responseMsg", "Add Face Image returned null");
												bvnObj.put("Match", Boolean.valueOf(false));
												return bvnObj.toString();
											} else {
												String idFace = "";
												String code = "";
												JSONObject obj3 = new JSONObject(resp3);
												if (obj3.has("id"))
													if (!obj3.isNull("id")) {
														idFace = obj3.getString("id");

													} else {
														idFace = "";

													}

												if (obj3.has("code"))
													if (!obj3.isNull("code")) {
														code = obj3.getString("code");

													} else {
														code = "";

													}

												if (!idFace.isEmpty() || code.equalsIgnoreCase("5820")) {
													log.debug("IdentityX WS Add Face Image successful");
													log.debug("Request to IdentityX WS Face Evaluation");
													String resp4 = ServiceCall.callIdentityXService(
															Constants.IDENTITYX_REQUEST_FACE_EVALUATION_URL
																	+ userDetails.getCreateId() + "/idchecks/"
																	+ userDetails.getIdChecks()
																	+ "/evaluation?evaluationPolicyName=EvaluationPolicy",
															"");

													if (resp4.isEmpty() || resp4 == null) {
														log.debug("IdentityX WS Face Evaluation returned null");
														bvnObj.put("statusCode", "12");
														bvnObj.put("statusMsg", "Failed");
														bvnObj.put("responseMsg", "Add Face Image returned null");
														bvnObj.put("Match", Boolean.valueOf(false));
														return bvnObj.toString();
													} else {
														JSONObject obj4 = new JSONObject(resp4);
														String status = "";
														String type = "";
														if (obj4.has("evaluationPolicy"))
															if (!obj4.isNull("evaluationPolicy")) {
																// JSONArray arr =
																// obj4.getJSONObject("evaluationPolicy");
																// JSONObject resObj2 =
																// reqObj.getJSONObject("evaluationPolicy");
																type = obj4.getJSONObject("evaluationPolicy")
																		.getString("type");
																status = obj4.getJSONObject("evaluationPolicy")
																		.getString("status");
																// status = resObj2.getJSONObject("policy");
															} else {
																status = "";
																type = "";
															}

														if (status.equalsIgnoreCase("ACTIVE")
																&& type.equalsIgnoreCase("EVALUATION")) {
															log.debug("IdentityX WS Face Evaluation successful");

															bvnObj.put("statusCode", "00");
															bvnObj.put("statusMsg", "Success");
															bvnObj.put("responseMsg", "BVN Validation Successful");
															bvnObj.put("Match", Boolean.valueOf(true));

															return bvnObj.toString();

														} else {
															bvnObj.put("statusCode", "14");
															bvnObj.put("statusMsg", "Failed");
															bvnObj.put("responseMsg", "BVN Validation Failed");
															bvnObj.put("Match", Boolean.valueOf(false));
															return bvnObj.toString();
														}
													}

												} else {
													bvnObj.put("statusCode", "13");
													bvnObj.put("statusMsg", "Failed");
													bvnObj.put("responseMsg", "BVN Validation Failed");
													bvnObj.put("Match", Boolean.valueOf(false));
													return bvnObj.toString();
												}
											}

										} else {
											log.debug("IdentityXServices WS Add Sefie not successful");
											bvnObj.put("statusCode", "11");
											bvnObj.put("statusMsg", "Failed");
											bvnObj.put("responseMsg", "BVN Validation Failed");
											bvnObj.put("Match", Boolean.valueOf(false));
											return bvnObj.toString();
										}

									}

								} else {
									bvnObj.put("statusCode", "00");
									bvnObj.put("statusMsg", "Success");
									bvnObj.put("responseMsg", "BVN Validation Successful");
									bvnObj.put("Match", Boolean.valueOf(true));

									return bvnObj.toString();
								}

							} else {
								bvnObj.put("statusCode", "08");
								bvnObj.put("statusMsg", "Failed");
								bvnObj.put("responseMsg", "Phone does not Match BVN data");
								bvnObj.put("Match", Boolean.valueOf(false));
								return bvnObj.toString();
							}
						} else {
							bvnObj.put("statusCode", "09");
							bvnObj.put("statusMsg", "Failed");
							bvnObj.put("responseMsg", "DOB does not Match BVN data");
							bvnObj.put("Match", Boolean.valueOf(false));
							return bvnObj.toString();
						}
					} else {
						log.debug("Date Birth field is null");
						bvnObj.put("statusCode", "06");
						bvnObj.put("statusMsg", "Failed");
						bvnObj.put("responseMsg", "Date Birth field is null");
						bvnObj.put("Match", Boolean.valueOf(false));
						return bvnObj.toString();
					}

				} else {
					log.debug("BVN validation not successful");
					bvnObj.put("statusCode", "07");
					bvnObj.put("statusMsg", "Failed");
					bvnObj.put("responseMsg", "BVN Validation Failed");
					bvnObj.put("Match", Boolean.valueOf(false));

					return bvnObj.toString();
				}

			}

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			bvnObj.put("statusCode", "99");
			bvnObj.put("statusMsg", "Failed");
			bvnObj.put("responseMsg", "System Malfunction");
			bvnObj.put("Match", Boolean.valueOf(false));
			return bvnObj.toString();

		}

	}

	@Override
	public String createIdentityxUsers(String req) throws Exception {

		JSONObject resObj = new JSONObject();
		JSONObject userObj = new JSONObject(req);
		String userId = userObj.getString("userId");
		String Id = "";
		String uId = "";
		String ucreatedDate = "";
		String updatedDate = "";
		String createdDate = "";
		String Idchk = "";
		log.debug("userId >>> " + userId);

		try {
			if (userId == "" || userId.isEmpty() || userId == null) {
				log.debug("UserId field is empty");
				resObj.put("statusCode", "02");
				resObj.put("statusMsg", "UserId field is required");

				return resObj.toString();
			} else {

				log.debug("Request to Create User WS");
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("userId", userId + "-" + AppUtil.getMessageDateTime2());

				String resp1 = ServiceCall.callIdentityXService(Constants.IDENTITYX_CREATE_USERS_URL,
						jsonRequest.toString());
				if (resp1.isEmpty() || resp1 == null) {
					log.debug("Create User WS returned null");
					resObj.put("statusCode", "03");
					resObj.put("statusMsg", "Failed");
					return resObj.toString();

				} else {

					JSONObject obj = new JSONObject(resp1);

					if (obj.has("id"))
						if (!obj.isNull("id")) {
							Id = obj.getString("id");
						} else {
							Id = "";
						}

					if (obj.has("userId"))
						if (!obj.isNull("userId")) {
							uId = obj.getString("userId");
						} else {
							uId = "";
						}

					if (obj.has("created"))
						if (!obj.isNull("created")) {
							ucreatedDate = obj.getString("created");
						} else {
							ucreatedDate = "";
						}

					if (obj.has("updated"))
						if (!obj.isNull("updated")) {
							updatedDate = obj.getString("updated");
						} else {
							updatedDate = "";
						}

					if (obj.getString("status").equalsIgnoreCase("ACTIVE")) {
						log.debug("Create User successful");

						log.debug("Request to Create Id Check WS");
						JSONObject jsonReq = new JSONObject();
						String referenceId = userId + "-" + AppUtil.getMessageDateTime2();
						jsonReq.put("referenceId", referenceId);

						String resp2 = ServiceCall.callIdentityXService(
								Constants.IDENTITYX_CREATE_IDCHECK_URL + Id + "/idchecks", jsonReq.toString());
						if (resp2.isEmpty() || resp2 == null) {
							log.debug("Create Id Check WS returned null");
							resObj.put("statusCode", "04");
							resObj.put("statusMsg", "Failed");
							return resObj.toString();

						} else {

							JSONObject obj1 = new JSONObject(resp2);

							if (obj1.has("id"))
								if (!obj1.isNull("id")) {
									Idchk = obj1.getString("id");
								} else {
									Idchk = "";
								}

							if (obj.has("created"))
								if (!obj.isNull("created")) {
									createdDate = obj.getString("created");
								} else {
									createdDate = "";
								}

							if (obj.getString("status").equalsIgnoreCase("ACTIVE")) {

								if (!resourceRepository.insertUserLog(userId, Id, ucreatedDate, updatedDate,
										referenceId, Idchk, createdDate)) {
									log.debug("Unable to insert entry");
									resObj.put("statusCode", "96");
									resObj.put("statusMsg", "Failed");

									return resObj.toString();
								}
								resObj.put("statusCode", "00");
								resObj.put("statusMsg", "Success");

								return resObj.toString();

							} else {
								log.debug("Create Id Check WS not successful");
								resObj.put("statusCode", "05");
								resObj.put("statusMsg", "Failed");

								return resObj.toString();
							}
						}

					} else {
						log.debug("Create User WS not successful");
						resObj.put("statusCode", "06");
						resObj.put("statusMsg", "Failed");

						return resObj.toString();
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			resObj.put("statusMsg", "System Malfunction");

			return resObj.toString();

		}

	}

	@Override
	public String accountLienEnquiry(String accountNo) throws Exception {
		JSONObject resObj = new JSONObject();
		String soapReq = "";
		AccountLienInquiryResponse fiResponse = null;

		try {

			String fiXML = XMLParser.formFinacleAccountLienInquiryRequest(accountNo);
			soapReq = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA["
					+ fiXML + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			log.debug("Invoking AccountLienInquiry WS");
			fiResponse = XMLParser.parseFinacleAccountLienInquiryResponse(FinacleCall.processCall(fiUrl, soapReq));
			try {
				log.debug("Status Message >>> " + fiResponse.getStatusMessage());
				// log.debug("CIF_ID >>> " + fiResponse.getCifId());

				if (fiResponse.getStatusMessage().equalsIgnoreCase("SUCCESS")) {
					log.debug("AccountLienInquiry is successful");
					resObj.put("ResponseCode", "00");
					resObj.put("ResponseMessage", "SUCCESS");
					resObj.put("LienID", fiResponse.getLienID());
					resObj.put("LienAmt", fiResponse.getLienAmt());
					resObj.put("LienRmks", fiResponse.getLienRmks());
					resObj.put("LienStartDate", fiResponse.getLienStartDate());
					resObj.put("LienEndDate", fiResponse.getLienEndDate());
					resObj.put("LienReason", fiResponse.getLienReason());
					resObj.put("ErrorDetails", fiResponse.getResponseMessage());
					return resObj.toString();

				} else {
					log.debug("AccountLienInquiry not successful");
					resObj.put("ResponseCode", "01");
					resObj.put("ResponseMessage", "FAILED");
					resObj.put("LienID", fiResponse.getLienID());
					resObj.put("LienAmt", fiResponse.getLienAmt());
					resObj.put("LienRmks", fiResponse.getLienRmks());
					resObj.put("LienStartDate", fiResponse.getLienStartDate());
					resObj.put("LienEndDate", fiResponse.getLienEndDate());
					resObj.put("LienReason", fiResponse.getLienReason());
					resObj.put("ErrorDetails", "");
					return resObj.toString();
				}
			} catch (Exception e) {

				e.printStackTrace();
				log.error("Error >>> " + e.fillInStackTrace());
				resObj.put("ResponseCode", "99");
				resObj.put("ResponseMessage", "FAILED");
				resObj.put("ErrorDetails", "System Error");

				return resObj.toString();
			}

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			resObj.put("ResponseCode", "99");
			resObj.put("ResponseMessage", "FAILED");
			resObj.put("ErrorDetails", "System Error");

			return resObj.toString();

		}

	}

	@Override
	public String staffRestriction(AccountRestrictionRequest req) throws Exception {
		JSONObject resObj = new JSONObject();

		try {

			String fiXML = XMLParser.formFinacleAccountRestrictionRequest(req);
			String soapReq = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA["
					+ fiXML + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			log.debug("Invoking Account Restriction WS");
			AccountRestrictionResponse fiResponse = XMLParser
					.parseFinacleAccountRestrictionResponse(FinacleCall.processCall(fiUrl, soapReq));

			try {

				log.debug("Status Message >>> " + fiResponse.getStatusMessage());
				log.debug("OUTPUT >>> " + fiResponse.getResponseMessage());

				if (fiResponse.getStatusMessage().equalsIgnoreCase("SUCCESS")) {
					if (fiResponse.getResponseMessage() == "" || fiResponse.getResponseMessage() == null
							|| fiResponse.getResponseMessage().isEmpty()) {
						log.debug("Account Restriction is null");
						resObj.put("statusCode", "08");
						resObj.put("statusMsg", "Failed");
						resObj.put("responseMessage", fiResponse.getStatusMessage());
						return resObj.toString();
					} else {
						log.debug("Account Restriction is successful");
						resObj.put("statusCode", "00");
						resObj.put("statusMsg", "Success");
						resObj.put("responseMessage", fiResponse.getResponseMessage());
						return resObj.toString();
					}

				} else {
					log.debug("Account Restriction not successful");
					resObj.put("statusCode", "08");
					resObj.put("statusMsg", "Failed");
					resObj.put("responseMessage", fiResponse.getStatusMessage());
					return resObj.toString();
				}

			} catch (Exception e) {

				e.printStackTrace();
				log.error("Error >>> " + e.fillInStackTrace());
				resObj.put("statusCode", "99");
				resObj.put("statusMsg", "System Error");

				return resObj.toString();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error calling FI: " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			resObj.put("statusMsg", "An error occurred while processing the request.");
			// ResponseHandler.generateResponse("Error calling FI", HttpStatus.BAD_GATEWAY);

		}
		return resObj.toString();
	}

	@Override
	public String accountReactivation(AccountReactivationRequest req) throws Exception {
		JSONObject resObj = new JSONObject();

		try {

			String fiXML = XMLParser.formFinacleAccountReactivationRequest(req);
			String soapReq = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA["
					+ fiXML + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			log.debug("Invoking Account Reactivation WS");
			AccountReactivationResponse fiResponse = XMLParser
					.parseFinacleAccountReactivationResponse(FinacleCall.processCall(fiUrl, soapReq));

			try {

				log.debug("Status Message >>> " + fiResponse.getStatusMessage());
				log.debug("OUTPUT >>> " + fiResponse.getResponseMessage());

				if (fiResponse.getStatusMessage().equalsIgnoreCase("SUCCESS")) {
					if (fiResponse.getResponseMessage() == "" || fiResponse.getResponseMessage() == null
							|| fiResponse.getResponseMessage().isEmpty()) {
						log.debug("Account Reactivation is null");
						resObj.put("statusCode", "97");
						resObj.put("statusMsg", "Failed");
						resObj.put("message", fiResponse.getMessage());
						return resObj.toString();
					} else {

						if (fiResponse.getResponseMessage().equalsIgnoreCase("N")) {
							log.debug("Account Reactivation is successful");
							resObj.put("statusCode", "07");
							resObj.put("statusMsg", "Failed");
							resObj.put("accountStatus", fiResponse.getAccountStatus());
							resObj.put("message", fiResponse.getMessage());
							return resObj.toString();
						} else {
							log.debug("Account Reactivation is successful");
							resObj.put("statusCode", "00");
							resObj.put("statusMsg", "Success");
							resObj.put("accountStatus", fiResponse.getAccountStatus());
							resObj.put("message", fiResponse.getMessage());
							return resObj.toString();
						}

					}

				} else {
					log.debug("Account Reactivation not successful");
					resObj.put("statusCode", "09");
					resObj.put("statusMsg", "Failed");
					resObj.put("message", fiResponse.getMessage());
					resObj.put("responseMessage", fiResponse.getStatusMessage());
					return resObj.toString();
				}

			} catch (Exception e) {

				e.printStackTrace();
				log.error("Error >>> " + e.fillInStackTrace());
				resObj.put("statusCode", "99");
				resObj.put("statusMsg", "Failed");
				resObj.put("responseMessage", "System Error");
				return resObj.toString();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error calling FI: " + e.fillInStackTrace());
			resObj.put("statusCode", "99");
			resObj.put("statusMsg", "Failed");
			resObj.put("responseMessage", "An error occurred while processing the request.");
			// ResponseHandler.generateResponse("Error calling FI", HttpStatus.BAD_GATEWAY);

		}
		return resObj.toString();
	}

	@Override
	public AccountDetailsResponse getAccountDetails(String referralCode, String startDate, String endDate)
			throws Exception {

		log.debug("ReferralCode >>> " + referralCode);
		log.debug("StartDate >>> " + startDate);
		log.debug("EndDate >>> " + endDate);
		AccountDetailsResponse response = new AccountDetailsResponse();
		// String startDate = request.getStartDate();
		// String endDate = request.getEndDate();
		if (referralCode == "" || referralCode.isEmpty() || referralCode == null) {
			log.debug("ReferralCode value is empty");
			response.setStatus("03");
			response.setMessage("Failed");
			return response;
		}

		if (startDate == "" || startDate.isEmpty() || startDate == null) {
			log.debug("StartDate value is empty");
			response.setStatus("04");
			response.setMessage("Failed");
			return response;
		}

		if (endDate == "" || endDate.isEmpty() || endDate == null) {
			log.debug("EndDate value is empty");
			response.setStatus("05");
			response.setMessage("Failed");
			return response;
		}

		ArrayList<AcctDetails> acctDetails = null;

		try {
			acctDetails = resourceRepository.getAcctDetails(referralCode.toUpperCase(), startDate, endDate);
			if (acctDetails == null || acctDetails.size() == 0 || acctDetails.isEmpty()) {
				log.debug("Account opened not found");
				response.setStatus("02");
				response.setMessage("Failed");
				// response.setResult(0);
				return response;
			} else {

				ArrayList<Result> accRes = new ArrayList<Result>();

				for (AcctDetails accDetail : acctDetails) {
					Result t = new Result();
					t.setAccountNo(accDetail.getAccountNo());
					t.setAccountName(accDetail.getAccountName());
					t.setSol_id(accDetail.getSolId());
					t.setAcct_opn_date(accDetail.getAcctOpenDate());
					t.setClr_bal_amt(accDetail.getClrBalAmt());
					accRes.add(t);

				}

				response.setStatus("00");
				response.setMessage("Successful");
				response.setResult(accRes);
				return response;
			}

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Error >>> " + e.fillInStackTrace());
			response.setStatus("01");
			response.setMessage("Error encountered while processing " + e.fillInStackTrace());
			return response;
		}

	}

	@Override
	public SalaryAccountResponse getSalaryAccount(String emailId) throws Exception {
		// JSONObject resObj = new JSONObject();
		SalaryAccountResponse resp = new SalaryAccountResponse();
		SalaryAccountFIResult fiResponse = null;
		try {

			String fiXML = XMLParser.formFinacleSalaryAccountRequest(emailId);
			String soapReq = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA["
					+ fiXML + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

			fiResponse = XMLParser.parseFinacleSalaryAccountResponse(FinacleCall.processCall(fiUrl, soapReq));

			log.debug("StatusMessage : " + fiResponse.getStatusMessage());

			if (fiResponse.getStatusMessage().equalsIgnoreCase("SUCCESS")) {

				resp.setStatusCode("00");
				resp.setStatusMessage("Success");
				resp.setAccountNo(fiResponse.getvAccount());
				resp.setEmployeeId(fiResponse.getvEmpId());
				return resp;

			} else {
				resp.setStatusCode("08");
				resp.setStatusMessage("Failure");
				resp.setResponseMessage(fiResponse.getResponseMessage());
				return resp;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			log.error("Error calling FI: " + e.fillInStackTrace());
			resp.setStatusCode("99");
			resp.setStatusMessage("Failure");
			resp.setResponseMessage("System Error");
			return resp;

		}

	}

	public static String GenerateRequestid() {
		Random random = new Random();
		int randomNumber = 0;
		boolean loop = true;

		while (loop) {
			randomNumber = random.nextInt();
			if (Integer.toString(randomNumber).length() == 10 && !Integer.toString(randomNumber).startsWith("-")) {
				loop = false;
			}
		}

		return String.valueOf(randomNumber);
	}

	public String getLoginDetails(String username, String password) {
		log.debug("Retrieving bearer token....");
		String response = null;
		try {
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("username", username);
			jsonRequest.put("password", password);

			response = ServiceCall.callAuthService(Constants.AUTH_URL, jsonRequest.toString());

			JSONObject obj1 = new JSONObject(response);
			if (response != null && obj1.getString("responseCode").equalsIgnoreCase("00")) {

				return obj1.getString("token");
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error: " + e.fillInStackTrace());
			return null;
		}

	}

	public String formatAmount(String amountStr) {
		double amount = Double.parseDouble(amountStr);
		DecimalFormat formatter = new DecimalFormat("###.00");
		return formatter.format(amount);
	}

	public String formatAmount1(String amountStr1) {
		double amount = Double.parseDouble(amountStr1);
		DecimalFormat formatter = new DecimalFormat("###.0");
		return formatter.format(amount);
	}

	private String retailAccountOpening(String title, String crncy_code, String streetNo, String firstName,
			String lastName, String middleName, String gender, String dob, String phonNumber1, String phonNumber2,
			String phonNumber3, String emailAddress, String solId, String nationality, String maritalStatus,
			String city) throws IOException {

		Request json = new Request();
		log.debug("Invoking Retail customer creation....");
		return ServiceCall.callPostingSvc(accountOpeningUrl,
				json.formAcctOpeningRequest(title, crncy_code, streetNo, firstName, lastName, middleName, gender, dob,
						phonNumber1, phonNumber2, phonNumber3, emailAddress, solId, nationality, maritalStatus, city));

	}

	private String savingsAccountCreation(String cif_id, String bvn, String referalcode, String schmcode,
			String schmtype) throws IOException {

		Request json = new Request();
		log.debug("Invoking Saving Account creation....");
		return ServiceCall.callPostingSvc(savingAcctCreationUrl,
				json.formSavingsAccountCreation(cif_id, bvn, referalcode, schmcode, schmtype));

	}

	public void sendSms(String msg, String phoneNumber) {

		// Invoke sms service here..............

		JSONObject smsReq = new JSONObject();
		smsReq.put("RequestId", AppUtil.getValueDateTime());
		JSONObject smsObj = new JSONObject();
		smsObj.put("id", AppUtil.getValueDateTime());
		smsObj.put("receiver", "0" + phoneNumber.substring(phoneNumber.length() - 10));
		smsObj.put("message", msg);
		JSONArray ja = new JSONArray();
		ja.put(smsObj);
		smsReq.put("sms", ja);

		String resp1;
		try {
			resp1 = ServiceCall.callPostingSvc(smsUrl, smsReq.toString());
			if (resp1 == null) {
				log.debug("Null/Empty response from SMS service");
				// return "99";
			} else {
				JSONObject obj1 = new JSONObject(resp1);

				log.debug("responseCode: " + obj1.getString("ResponseCode"));
				if (obj1.getString("ResponseCode").equalsIgnoreCase("00")
						&& obj1.getString("ResponseMessage").equalsIgnoreCase("Success")) {
					log.debug("Sms call successful");
					// return "00";
				} else {
					log.debug("Sms call not successful");
					// return obj1.getString("ResponseCode");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error sending sms >>> " + e);
			// return "99";
		}

	}

	public static void main(String[] args) throws Exception {
		String email = "ecnwowo@gmail.com";
		String emailAddr[] = email.split("@");
		String phoneNo = "2348053531833";
		System.out.println(
				"Masked PhoneNo >>> " + phoneNo.substring(0, 4) + "******" + phoneNo.substring(phoneNo.length() - 3));
		System.out.println("Masked Email Address >>> " + emailAddr[0].substring(0, 3) + "******@" + emailAddr[1]);

		// XMLParser xl = new XMLParser();

		// String resp = "<soapenv:Envelope
		// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
		// xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"
		// xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Header/><soapenv:Body><p501:executeServiceResponse
		// xmlns:p501=\"http://webservice.fiusb.ci.infosys.com/\"><executeServiceReturn><FIXML
		// xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\"
		// xmlns=\"http://www.finacle.com/fixml\"
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Header><ResponseHeader><RequestMessageKey><RequestUUID>Req_24012022120104</RequestUUID><ServiceRequestId>executeFinacleScript</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId></RequestMessageKey><ResponseMessageInfo><BankId>01</BankId><TimeZone></TimeZone><MessageDateTime>2022-01-24T11:15:43.310</MessageDateTime></ResponseMessageInfo><UBUSTransaction><Id>null</Id><Status>FAILED</Status></UBUSTransaction><HostTransaction><Id>0000</Id><Status>FAILURE</Status></HostTransaction><HostParentTransaction><Id>null</Id><Status>null</Status></HostParentTransaction><CustomInfo/></ResponseHeader></Header><Body><Error><FIBusinessException><ErrorDetail><ErrorCode>8504</ErrorCode><ErrorDesc>No
		// record could be
		// retrieved</ErrorDesc><ErrorSource></ErrorSource><ErrorType>BE</ErrorType></ErrorDetail></FIBusinessException></Error></Body></FIXML></executeServiceReturn></p501:executeServiceResponse></soapenv:Body></soapenv:Envelope>";

		// String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<FIXML
		// xmlns=\"http://www.finacle.com/fixml\"
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
		// xsi:schemaLocation=\"http://www.finacle.com/fixml AcctLienInq.xsd\">\r\n
		// <Header>\r\n <ResponseHeader>\r\n <RequestMessageKey>\r\n
		// <RequestUUID>Req_08022022045154</RequestUUID>\r\n
		// <ServiceRequestId>AcctLienInq</ServiceRequestId>\r\n
		// <ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n
		// <ChannelId>COR</ChannelId>\r\n </RequestMessageKey>\r\n
		// <ResponseMessageInfo>\r\n <BankId>01</BankId>\r\n
		// <TimeZone>GMT+05:30</TimeZone>\r\n
		// <MessageDateTime>2022-04-25T17:21:53.764</MessageDateTime>\r\n
		// </ResponseMessageInfo>\r\n <UBUSTransaction>\r\n <Id/>\r\n <Status/>\r\n
		// </UBUSTransaction>\r\n <HostTransaction>\r\n <Id/>\r\n
		// <Status>SUCCESS</Status>\r\n </HostTransaction>\r\n
		// <HostParentTransaction>\r\n <Id/>\r\n <Status/>\r\n
		// </HostParentTransaction>\r\n <CustomInfo/>\r\n </ResponseHeader>\r\n
		// </Header>\r\n <Body>\r\n <AcctLienInqResponse>\r\n <AcctLienInqRs>\r\n
		// <AcctId>\r\n <AcctId>1000005313</AcctId>\r\n <AcctType>\r\n <SchmCode/>\r\n
		// <SchmType/>\r\n </AcctType>\r\n <AcctCurr>NGN</AcctCurr>\r\n <BankInfo>\r\n
		// <BankId/>\r\n <Name/>\r\n <BranchId/>\r\n <BranchName/>\r\n <PostAddr>\r\n
		// <Addr1/>\r\n <Addr2/>\r\n <Addr3/>\r\n <City/>\r\n <StateProv/>\r\n
		// <PostalCode/>\r\n <Country/>\r\n <AddrType/>\r\n </PostAddr>\r\n
		// </BankInfo>\r\n </AcctId>\r\n <ModuleType>ULIEN</ModuleType>\r\n
		// <LienDtlsRec>\r\n <NewLienAmt>\r\n <amountValue>253378.64</amountValue>\r\n
		// <currencyCode>NGN</currencyCode>\r\n </NewLienAmt>\r\n <OldLienAmt>\r\n
		// <amountValue>253378.64</amountValue>\r\n <currencyCode>NGN</currencyCode>\r\n
		// </OldLienAmt>\r\n <LienDt>\r\n <StartDt>2022-03-29T00:00:00.000</StartDt>\r\n
		// <EndDt>2099-04-01T00:00:00.000</EndDt>\r\n </LienDt>\r\n
		// <ReasonCode>0012</ReasonCode>\r\n <Rmks>LIEN ON CASHLITE CHARGE</Rmks>\r\n
		// <IsDeleted>N</IsDeleted>\r\n <LienId>FEP5078730000000065</LienId>\r\n
		// </LienDtlsRec>\r\n </AcctLienInqRs>\r\n <AcctLienInq_CustomData/>\r\n
		// </AcctLienInqResponse>\r\n </Body>\r\n</FIXML>";

		// String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<FIXML
		// xsi:schemaLocation=\"http://www.finacle.com/fixml AcctLienInq.xsd\"
		// xmlns=\"http://www.finacle.com/fixml\"
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n <Header>\r\n
		// <ResponseHeader>\r\n <RequestMessageKey>\r\n
		// <RequestUUID>Req_16062022014034</RequestUUID>\r\n
		// <ServiceRequestId>AcctLienInq</ServiceRequestId>\r\n
		// <ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n
		// <ChannelId>COR</ChannelId>\r\n </RequestMessageKey>\r\n
		// <ResponseMessageInfo>\r\n <BankId>01</BankId>\r\n
		// <TimeZone>GMT+05:30</TimeZone>\r\n
		// <MessageDateTime>2022-06-16T19:47:23.258</MessageDateTime>\r\n
		// </ResponseMessageInfo>\r\n <UBUSTransaction>\r\n <Id/>\r\n <Status/>\r\n
		// </UBUSTransaction>\r\n <HostTransaction>\r\n <Id/>\r\n
		// <Status>SUCCESS</Status>\r\n </HostTransaction>\r\n
		// <HostParentTransaction>\r\n <Id/>\r\n <Status/>\r\n
		// </HostParentTransaction>\r\n <CustomInfo/>\r\n </ResponseHeader>\r\n
		// </Header>\r\n <Body>\r\n <AcctLienInqResponse>\r\n <AcctLienInqRs>\r\n
		// <AcctId>\r\n <AcctId>2130021891</AcctId>\r\n <AcctType>\r\n
		// <SchmCode></SchmCode>\r\n <SchmType></SchmType>\r\n </AcctType>\r\n
		// <AcctCurr>NGN</AcctCurr>\r\n <BankInfo>\r\n <BankId></BankId>\r\n
		// <Name></Name>\r\n <BranchId></BranchId>\r\n <BranchName></BranchName>\r\n
		// <PostAddr>\r\n <Addr1></Addr1>\r\n <Addr2></Addr2>\r\n <Addr3></Addr3>\r\n
		// <City></City>\r\n <StateProv></StateProv>\r\n <PostalCode></PostalCode>\r\n
		// <Country></Country>\r\n <AddrType></AddrType>\r\n </PostAddr>\r\n
		// </BankInfo>\r\n </AcctId>\r\n <ModuleType>ULIEN</ModuleType>\r\n
		// <LienDtlsRec>\r\n <NewLienAmt>\r\n <amountValue>1.00</amountValue>\r\n
		// <currencyCode>NGN</currencyCode>\r\n </NewLienAmt>\r\n <OldLienAmt>\r\n
		// <amountValue>1.00</amountValue>\r\n <currencyCode>NGN</currencyCode>\r\n
		// </OldLienAmt>\r\n <LienDt>\r\n <StartDt>2022-05-17T00:00:00.000</StartDt>\r\n
		// </LienDt>\r\n <ReasonCode>TEST</ReasonCode>\r\n <Rmks></Rmks>\r\n
		// <IsDeleted>N</IsDeleted>\r\n <LienId>PB26921</LienId>\r\n </LienDtlsRec>\r\n
		// </AcctLienInqRs>\r\n <AcctLienInq_CustomData/>\r\n </AcctLienInqResponse>\r\n
		// </Body>\r\n </FIXML>";
		// StatementServiceImpl resp = new StatementServiceImpl();
		// System.out.println("XML: " + new
		// Gson().toJson(xl.parseFinacleAccountLienInquiryResponse(response)));
		// "AccountStatusCodes": [{"05"},{”06”}],
		/*
		 * AccountOpeningServiceImpl acc = new AccountOpeningServiceImpl(); String wName
		 * = ""; String wMatch = ""; String entityName = ""; String entityNMatch = "";
		 * String resStr =
		 * "[\r\n    {\r\n        \"Ind_Name\": \"Saheb Madani\",\r\n        \"Ind_NameMatch\": 100.0,\r\n        \"EntityName\": null,\r\n        \"EntityNameMatch\": 0.0\r\n    },\r\n    {\r\n        \"Ind_Name\": \"SAHEB Madani\",\r\n        \"Ind_NameMatch\": 66.666666666666671,\r\n        \"EntityName\": null,\r\n        \"EntityNameMatch\": 0.0\r\n    },\r\n    {\r\n        \"Ind_Name\": \"Ikram Jan Mohammad Madani\",\r\n        \"Ind_NameMatch\": 36.0,\r\n        \"EntityName\": null,\r\n        \"EntityNameMatch\": 0.0\r\n    },\r\n    {\r\n        \"Ind_Name\": \"Saheb Hanafi\",\r\n        \"Ind_NameMatch\": 75.0,\r\n        \"EntityName\": null,\r\n        \"EntityNameMatch\": 0.0\r\n    },\r\n    {\r\n        \"Ind_Name\": \"Saheb Agha\",\r\n        \"Ind_NameMatch\": 58.333333333333329,\r\n        \"EntityName\": null,\r\n        \"EntityNameMatch\": 0.0\r\n    }\r\n]"
		 * ; System.out.println("Response String >>> " + resStr);
		 * 
		 * JSONObject resObj = new JSONObject(); JSONArray jsonarray = new
		 * JSONArray(resStr); // JSONArray arrObj = obj.getJSONArray(""); JSONArray ja =
		 * new JSONArray();
		 * 
		 * // Iterating the contents of the array for (int i = 0; i <
		 * jsonarray.length(); i++) { JSONObject job = new JSONObject(); JSONObject
		 * jsonobject = jsonarray.getJSONObject(i);
		 * 
		 * if (jsonobject.has("Ind_Name") && jsonobject.getString("Ind_Name") != null) {
		 * job.put("Ind_Name", jsonobject.getString("Ind_Name")); } else {
		 * 
		 * wName = "X"; }
		 * 
		 * if (jsonobject.has("Ind_NameMatch") &&
		 * jsonobject.getBigDecimal("Ind_NameMatch") != null) { job.put("Ind_NameMatch",
		 * acc.formatAmount(String.valueOf(jsonobject.getBigDecimal("Ind_NameMatch"))));
		 * } else {
		 * 
		 * wMatch = ""; }
		 * 
		 * if (jsonobject.has("EntityName") && !jsonobject.isNull("EntityName")) {
		 * job.put("EntityName", jsonobject.getString("EntityName")); } else {
		 * 
		 * job.put("EntityName", ""); entityName = ""; }
		 * 
		 * if (jsonobject.has("EntityNameMatch") &&
		 * jsonobject.getBigDecimal("EntityNameMatch") != null) {
		 * job.put("EntityNameMatch", String.format("%.1f",
		 * jsonobject.getFloat("EntityNameMatch")));
		 * 
		 * } else { job.put("EntityNameMatch", "0.0"); entityNMatch = ""; }
		 * 
		 * // wName = arrObj.getJSONObject(i).getString("Ind_Name"); // wMatch =
		 * arrObj.getJSONObject(i).getString("Ind_NameMatch"); ja.put(job); }
		 * 
		 * if (!wName.equalsIgnoreCase("X")) {
		 * 
		 * resObj.put("statusCode", "00"); resObj.put("statusMsg", "Successful");
		 * resObj.put("details", ja);
		 * 
		 * System.out.println("Decrypted String >>> " + resObj.toString()); } else {
		 * log.debug("watchlist search not successful"); resObj.put("statusCode", "01");
		 * resObj.put("statusMsg", "Failed"); System.out.println("Decrypted String >>> "
		 * + resObj.toString()); }
		 */

		// resObj.put("watchName", watchName);
		// resObj.put("watchNameMatch", watchNameMatch);

	}

}
