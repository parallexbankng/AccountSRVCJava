package com.parallex.accountopening;

public class Constants {

	//public static final String CLIENT_ID = "chukwuka12333";
	//public static final String CLIENT_KEY = "a55a11df-943f-43e2-ac82-77634c9477aa";
	
	//public static final String USERNAME = "AgentBanking"; //AgentBanking
	//public static final String PASSWORD = "String@123"; //String@123
	
	public static final String CLIENT_ID = "Service"; 
	public static final String CLIENT_KEY = "b8c87d7d-2ba9-40d7-9fe4-fb0dbb0a2dff";
	
	public static final String USERNAME = "bvnservice"; //AgentBanking
	public static final String PASSWORD = "3%wMTS$Y1V00"; //String@123
	
	public static final String BANK_ID = "01";
	
	//https://digitalapi.parallexbank.com
	//http://172.18.14.180:8080
	public static final String AUTH_URL = "https://digitalapi.parallexbank.com/authenticationservice/api/Authenticate/login";
	public static final String BVN_VALIDATION_URL = "https://digitalapi.parallexbank.com/utilityservice/api/BVN/GetSingleBVNDetail";
	public static final String DEDUP_URL = "http://172.18.14.180:8080/accountsrvc/api/dedup";
	
	public static final String VALIDATE_BVN_URL = "https://digitalapi.parallexbank.com/authenticationservice/api/Authenticate/validatebvn";
	public static final String AUTHENTICATE_OTP_URL = "https://digitalapi.parallexbank.com/authenticationservice/api/Authenticate/authenticateotp";
	
	//prod - https://identityx.parallexbank.com:8443
	//test - https://uatidentityx.parallexbank.com:8443
	
	public static final String IDENTITYX_CREATE_USERS_URL = "https://identityx.parallexbank.com:8443/retail-banking/IdentityXServices/rest/v1/users";
	public static final String IDENTITYX_CREATE_IDCHECK_URL = "https://identityx.parallexbank.com:8443/retail-banking/DigitalOnBoardingServices/rest/v1/users/";
	public static final String IDENTITYX_ADD_SEFIE_URL = "https://identityx.parallexbank.com:8443/retail-banking/IdentityXServices/rest/v1/users/";
	public static final String IDENTITYX_ADD_FACE_IMAGE_URL = "https://identityx.parallexbank.com:8443/retail-banking/DigitalOnBoardingServices/rest/v1/users/";
	public static final String IDENTITYX_REQUEST_FACE_EVALUATION_URL = "https://identityx.parallexbank.com:8443/retail-banking/DigitalOnBoardingServices/rest/v1/users/";

}
