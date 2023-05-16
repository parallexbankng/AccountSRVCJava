package com.parallex.accountopening.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.parallex.accountopening.domain.AccountLienInquiryResponse;
import com.parallex.accountopening.domain.AccountReactivationRequest;
import com.parallex.accountopening.domain.AccountReactivationResponse;
import com.parallex.accountopening.domain.AccountRestrictionRequest;
import com.parallex.accountopening.domain.AccountRestrictionResponse;
import com.parallex.accountopening.domain.DedupRequest;
import com.parallex.accountopening.domain.DedupResponse;
import com.parallex.accountopening.domain.RiskRatingRequest;
import com.parallex.accountopening.domain.RiskRatingResponse;
import com.parallex.accountopening.domain.SalaryAccountFIResult;
import com.parallex.accountopening.utils.AppUtil;

public class XMLParser {

	private static final Logger logger = LoggerFactory.getLogger(XMLParser.class);

	public static String formFinacleRequest(DedupRequest request) {

		StringBuilder sb = new StringBuilder();
		String bvn = "";

		if (request.getBvn() == "" || request.getBvn() == null || request.getBvn().isEmpty()) {
			bvn = "";
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
					"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\">")
					.append("<Header><RequestHeader><MessageKey><RequestUUID>Req_").append(AppUtil.generateUUID())
					.append("</RequestUUID><ServiceRequestId>executeFinacleScript</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId><LanguageId /></MessageKey><RequestMessageInfo><BankId>")
					.append("01")
					.append("</BankId><TimeZone /><EntityId /><EntityType /><ArmCorrelationId /><MessageDateTime>")
					.append(AppUtil.getTime())
					.append("</MessageDateTime></RequestMessageInfo><Security><Token><PasswordToken><UserId /><Password /></PasswordToken></Token><FICertToken /><RealUserLoginSessionId /><RealUser /><RealUserPwd /><SSOTransferToken /></Security></RequestHeader></Header><Body><executeFinacleScriptRequest><ExecuteFinacleScriptInputVO><requestId>FI_NOBVNdedup.scr</requestId></ExecuteFinacleScriptInputVO><executeFinacleScript_CustomData><init_mod_ref_num>")
					.append("01").append("</init_mod_ref_num><acctTyp>").append(request.getAcctType())
					.append("</acctTyp><DOB>").append(request.getDob()).append("</DOB><firstNam>")
					.append(request.getFirstName()).append("</firstNam><lastName>").append(request.getLastName())
					.append("</lastName><midName>").append(request.getMiddleName()).append("</midName><phoneNum>")
					.append(request.getPhoneNumber()).append("</phoneNum><bvn>").append("" + bvn + "").append("</bvn>");
			sb.append("</executeFinacleScript_CustomData></executeFinacleScriptRequest></Body></FIXML>");

		} else {
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
					"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\">")
					.append("<Header><RequestHeader><MessageKey><RequestUUID>Req_").append(AppUtil.generateUUID())
					.append("</RequestUUID><ServiceRequestId>executeFinacleScript</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId><LanguageId /></MessageKey><RequestMessageInfo><BankId>")
					.append("01")
					.append("</BankId><TimeZone /><EntityId /><EntityType /><ArmCorrelationId /><MessageDateTime>")
					.append(AppUtil.getTime())
					.append("</MessageDateTime></RequestMessageInfo><Security><Token><PasswordToken><UserId /><Password /></PasswordToken></Token><FICertToken /><RealUserLoginSessionId /><RealUser /><RealUserPwd /><SSOTransferToken /></Security></RequestHeader></Header><Body><executeFinacleScriptRequest><ExecuteFinacleScriptInputVO><requestId>FI_DeDup.scr</requestId></ExecuteFinacleScriptInputVO><executeFinacleScript_CustomData><init_mod_ref_num>")
					.append("01").append("</init_mod_ref_num><acctTyp>").append(request.getAcctType())
					.append("</acctTyp><DOB>").append(request.getDob()).append("</DOB><firstNam>")
					.append(request.getFirstName()).append("</firstNam><lastName>").append(request.getLastName())
					.append("</lastName><midName>").append(request.getMiddleName()).append("</midName><phoneNum>")
					.append(request.getPhoneNumber()).append("</phoneNum><bvn>").append(request.getBvn()).append("</bvn>");
			sb.append("</executeFinacleScript_CustomData></executeFinacleScriptRequest></Body></FIXML>");

		}

		return sb.toString();
	}
	
	public static String formFinacleRiskRatingRequest(RiskRatingRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\">")
				.append("<Header><RequestHeader><MessageKey><RequestUUID>Req_").append(AppUtil.generateUUID())
				.append("</RequestUUID><ServiceRequestId>executeFinacleScript</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId><LanguageId /></MessageKey><RequestMessageInfo><BankId>")
				.append("01")
				.append("</BankId><TimeZone /><EntityId /><EntityType /><ArmCorrelationId /><MessageDateTime>")
				.append(AppUtil.getTime())
				.append("</MessageDateTime></RequestMessageInfo><Security><Token><PasswordToken><UserId /><Password /></PasswordToken></Token><FICertToken /><RealUserLoginSessionId /><RealUser /><RealUserPwd /><SSOTransferToken /></Security></RequestHeader></Header><Body><executeFinacleScriptRequest><ExecuteFinacleScriptInputVO><requestId>FI_RiskRating.scr</requestId></ExecuteFinacleScriptInputVO><executeFinacleScript_CustomData><CIF_ID>")
				.append(request.getCifId()).append("</CIF_ID><RATING>").append(request.getRating()).append("</RATING>");
		sb.append("</executeFinacleScript_CustomData></executeFinacleScriptRequest></Body></FIXML>");
		
		return sb.toString();
		
	}
	
	public static String formFinacleAccountRestrictionRequest(AccountRestrictionRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\">")
				.append("<Header><RequestHeader><MessageKey><RequestUUID>Req_").append(AppUtil.generateUUID())
				.append("</RequestUUID><ServiceRequestId>executeFinacleScript</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId><LanguageId /></MessageKey><RequestMessageInfo><BankId>")
				.append("01")
				.append("</BankId><TimeZone /><EntityId /><EntityType /><ArmCorrelationId /><MessageDateTime>")
				.append(AppUtil.getTime())
				.append("</MessageDateTime></RequestMessageInfo><Security><Token><PasswordToken><UserId /><Password /></PasswordToken></Token><FICertToken /><RealUserLoginSessionId /><RealUser /><RealUserPwd /><SSOTransferToken /></Security></RequestHeader></Header><Body><executeFinacleScriptRequest><ExecuteFinacleScriptInputVO><requestId>FI_acct_access.scr</requestId></ExecuteFinacleScriptInputVO><executeFinacleScript_CustomData><email>")
				.append(request.getEmailId()).append("</email><acctno>").append(request.getAccountNo()).append("</acctno>");
		sb.append("</executeFinacleScript_CustomData></executeFinacleScriptRequest></Body></FIXML>");
		
		return sb.toString();
		
	}
	
	
	public static String formFinacleAccountReactivationRequest(AccountReactivationRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\">")
				.append("<Header><RequestHeader><MessageKey><RequestUUID>Req_").append(AppUtil.generateUUID())
				.append("</RequestUUID><ServiceRequestId>executeFinacleScript</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId><LanguageId /></MessageKey><RequestMessageInfo><BankId>")
				.append("01")
				.append("</BankId><TimeZone /><EntityId /><EntityType /><ArmCorrelationId /><MessageDateTime>")
				.append(AppUtil.getTime())
				.append("</MessageDateTime></RequestMessageInfo><Security><Token><PasswordToken><UserId /><Password /></PasswordToken></Token><FICertToken /><RealUserLoginSessionId /><RealUser /><RealUserPwd /><SSOTransferToken /></Security></RequestHeader></Header><Body><executeFinacleScriptRequest><ExecuteFinacleScriptInputVO><requestId>FI_ReActivateAcct.scr</requestId></ExecuteFinacleScriptInputVO><executeFinacleScript_CustomData><AcctNo>")
				.append(request.getAccountNo()).append("</AcctNo>");
		sb.append("</executeFinacleScript_CustomData></executeFinacleScriptRequest></Body></FIXML>");
		
		return sb.toString();
		
	}
	
	
	public static String formFinacleAccountLienInquiryRequest(String accountNo) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml AcctLienInq.xsd\">")
				.append("<Header><RequestHeader><MessageKey><RequestUUID>Req_").append(AppUtil.generateUUID())
				.append("</RequestUUID><ServiceRequestId>AcctLienInq</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId><LanguageId /></MessageKey><RequestMessageInfo><BankId>")
				.append("01")
				.append("</BankId><TimeZone /><EntityId /><EntityType /><ArmCorrelationId /><MessageDateTime>")
				.append(AppUtil.getTime())
				.append("</MessageDateTime></RequestMessageInfo><Security><Token><PasswordToken><UserId /><Password /></PasswordToken></Token><FICertToken /><RealUserLoginSessionId /><RealUser /><RealUserPwd /><SSOTransferToken /></Security></RequestHeader></Header><Body><AcctLienInqRequest><AcctLienInqRq><AcctId><AcctId>")
				.append(accountNo).append("</AcctId></AcctId><ModuleType>").append("ULIEN").append("</ModuleType>");
		sb.append("</AcctLienInqRq></AcctLienInqRequest></Body></FIXML>");
		
		return sb.toString();
		
	}
	
	
	public static String formFinacleSalaryAccountRequest(String emailId) {

		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\">")
				.append("<Header><RequestHeader><MessageKey><RequestUUID>Req_").append(AppUtil.generateUUID())
				.append("</RequestUUID><ServiceRequestId>executeFinacleScript</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId><LanguageId /></MessageKey><RequestMessageInfo><BankId>")
				.append("01")
				.append("</BankId><TimeZone /><EntityId /><EntityType /><ArmCorrelationId /><MessageDateTime>")
				.append(AppUtil.getTime())
				.append("</MessageDateTime></RequestMessageInfo><Security><Token><PasswordToken><UserId /><Password /></PasswordToken></Token><FICertToken /><RealUserLoginSessionId /><RealUser /><RealUserPwd /><SSOTransferToken /></Security></RequestHeader></Header><Body><executeFinacleScriptRequest><ExecuteFinacleScriptInputVO><requestId>FI_salaryaccount.scr</requestId></ExecuteFinacleScriptInputVO><executeFinacleScript_CustomData><email>")
				.append(emailId).append("</email>");
		sb.append("</executeFinacleScript_CustomData></executeFinacleScriptRequest></Body></FIXML>");

		return sb.toString();
	}


	public static DedupResponse parseFinacleResponse(String response)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src = null;
		String parseStr = "";
		parseStr = response.replaceAll("&lt;", "<");
		parseStr = parseStr.replaceAll("&gt;", ">");
		parseStr = parseStr.replaceAll("&quot;", "\"");
		parseStr = parseStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		logger.debug("XML: " + parseStr);
		src = new InputSource();
		src.setCharacterStream(new StringReader(parseStr));
		Document doc = builder.parse(src);
		DedupResponse resp = new DedupResponse();
		NodeList nList = null;
		NodeList nList1 = null;

		if (response.contains("<FISystemException>") || response.contains("<FIBusinessException>")) {
			// nList = doc.getElementsByTagName("ErrorDetail");
			nList = doc.getElementsByTagName("HostTransaction");
			nList1 = doc.getElementsByTagName("ErrorDetail");
		} else
			nList = doc.getElementsByTagName("ResponseHeader");
			nList1 = doc.getElementsByTagName("executeFinacleScript_CustomData");

		for (int temp = 0; temp < nList.getLength(); ++temp) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				resp.setStatusMessage(
						eElement.getElementsByTagName("HostTransaction").item(0).getTextContent().replace("\n", ""));
			}
		}

		for (int temp = 0; temp < nList1.item(0).getChildNodes().getLength(); ++temp) {

			System.out.println("Node name >>> " + nList1.item(0).getChildNodes().item(temp).getNodeName());

			switch (nList1.item(0).getChildNodes().item(temp).getNodeName()) {
			
			case "SuccessOrFailure":
				resp.setStatusFlag(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Error_0":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Error_1":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;	
			case "ErrorDesc":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrMsg":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Status":
				resp.setStatusMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;	
			case "cif":
				resp.setCifId(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "MESSAGE":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			}

		}

		return resp;
	}
	
	public static RiskRatingResponse parseFinacleRiskRatingResponse(String response)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src = null;
		String parseStr = "";
		parseStr = response.replaceAll("&lt;", "<");
		parseStr = parseStr.replaceAll("&gt;", ">");
		parseStr = parseStr.replaceAll("&quot;", "\"");
		parseStr = parseStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		logger.debug("XML: " + parseStr);
		src = new InputSource();
		src.setCharacterStream(new StringReader(parseStr));
		Document doc = builder.parse(src);
		RiskRatingResponse resp = new RiskRatingResponse();
		NodeList nList = null;
		NodeList nList1 = null;

		if (response.contains("<FISystemException>") || response.contains("<FIBusinessException>")) {
			// nList = doc.getElementsByTagName("ErrorDetail");
			nList = doc.getElementsByTagName("HostTransaction");
			nList1 = doc.getElementsByTagName("ErrorDetail");
		} else
			nList = doc.getElementsByTagName("ResponseHeader");
		nList1 = doc.getElementsByTagName("executeFinacleScript_CustomData");

		for (int temp = 0; temp < nList.getLength(); ++temp) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				resp.setStatusMessage(
						eElement.getElementsByTagName("HostTransaction").item(0).getTextContent().replace("\n", ""));
			}
		}

		for (int temp = 0; temp < nList1.item(0).getChildNodes().getLength(); ++temp) {

			System.out.println("Node name >>> " + nList1.item(0).getChildNodes().item(temp).getNodeName());

			switch (nList1.item(0).getChildNodes().item(temp).getNodeName()) {

			case "Error_0":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrorDesc":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrMsg":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Status":
				resp.setStatusMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "OUTPUT":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			}

		}

		return resp;
	}
	
	public static AccountRestrictionResponse parseFinacleAccountRestrictionResponse(String response)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src = null;
		String parseStr = "";
		parseStr = response.replaceAll("&lt;", "<");
		parseStr = parseStr.replaceAll("&gt;", ">");
		parseStr = parseStr.replaceAll("&quot;", "\"");
		parseStr = parseStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		logger.debug("XML: " + parseStr);
		src = new InputSource();
		src.setCharacterStream(new StringReader(parseStr));
		Document doc = builder.parse(src);
		AccountRestrictionResponse resp = new AccountRestrictionResponse();
		NodeList nList = null;
		NodeList nList1 = null;

		if (response.contains("<FISystemException>") || response.contains("<FIBusinessException>")) {
			// nList = doc.getElementsByTagName("ErrorDetail");
			nList = doc.getElementsByTagName("HostTransaction");
			nList1 = doc.getElementsByTagName("ErrorDetail");
		} else
			nList = doc.getElementsByTagName("ResponseHeader");
		nList1 = doc.getElementsByTagName("executeFinacleScript_CustomData");

		for (int temp = 0; temp < nList.getLength(); ++temp) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				resp.setStatusMessage(
						eElement.getElementsByTagName("HostTransaction").item(0).getTextContent().replace("\n", ""));
			}
		}

		for (int temp = 0; temp < nList1.item(0).getChildNodes().getLength(); ++temp) {

			System.out.println("Node name >>> " + nList1.item(0).getChildNodes().item(temp).getNodeName());

			switch (nList1.item(0).getChildNodes().item(temp).getNodeName()) {

			case "Error_0":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrorDesc":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrMsg":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Status":
				resp.setStatusMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "v_have_access":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			}

		}

		return resp;
	}
	
	public static AccountReactivationResponse parseFinacleAccountReactivationResponse(String response)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src = null;
		String parseStr = "";
		parseStr = response.replaceAll("&lt;", "<");
		parseStr = parseStr.replaceAll("&gt;", ">");
		parseStr = parseStr.replaceAll("&quot;", "\"");
		parseStr = parseStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		logger.debug("XML: " + parseStr);
		src = new InputSource();
		src.setCharacterStream(new StringReader(parseStr));
		Document doc = builder.parse(src);
		AccountReactivationResponse resp = new AccountReactivationResponse();
		NodeList nList = null;
		NodeList nList1 = null;

		if (response.contains("<FISystemException>") || response.contains("<FIBusinessException>")) {
			// nList = doc.getElementsByTagName("ErrorDetail");
			nList = doc.getElementsByTagName("HostTransaction");
			nList1 = doc.getElementsByTagName("ErrorDetail");
		} else
			nList = doc.getElementsByTagName("ResponseHeader");
		nList1 = doc.getElementsByTagName("executeFinacleScript_CustomData");

		for (int temp = 0; temp < nList.getLength(); ++temp) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				resp.setStatusMessage(
						eElement.getElementsByTagName("HostTransaction").item(0).getTextContent().replace("\n", ""));
			}
		}

		for (int temp = 0; temp < nList1.item(0).getChildNodes().getLength(); ++temp) {

			System.out.println("Node name >>> " + nList1.item(0).getChildNodes().item(temp).getNodeName());

			switch (nList1.item(0).getChildNodes().item(temp).getNodeName()) {

			case "Error_0":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrorDesc":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrMsg":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Status":
				resp.setStatusMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "SuccessOrFailure":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;	
			case "acctID":
				resp.setAcctID(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "message":
				resp.setMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Account_Sattus":
				resp.setAccountStatus(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;	
			}

		}

		return resp;
	}
	
	public static AccountLienInquiryResponse parseFinacleAccountLienInquiryResponse(String response)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src = null;
		String parseStr = "";
		parseStr = response.replaceAll("&lt;", "<");
		parseStr = parseStr.replaceAll("&gt;", ">");
		parseStr = parseStr.replaceAll("&quot;", "\"");
		parseStr = parseStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		logger.debug("XML: " + parseStr);
		src = new InputSource();
		src.setCharacterStream(new StringReader(parseStr));
		Document doc = builder.parse(src);
		AccountLienInquiryResponse resp = new AccountLienInquiryResponse();
		NodeList nList = null;
		NodeList nList1 = null;

		if (response.contains("<FISystemException>") || response.contains("<FIBusinessException>")) {
			// nList = doc.getElementsByTagName("ErrorDetail");
			nList = doc.getElementsByTagName("HostTransaction");
			nList1 = doc.getElementsByTagName("ErrorDetail");
		} else
			nList = doc.getElementsByTagName("ResponseHeader");
			nList1 = doc.getElementsByTagName("LienDtlsRec");

		for (int temp = 0; temp < nList.getLength(); ++temp) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				resp.setStatusMessage(
					eElement.getElementsByTagName("HostTransaction").item(0).getTextContent().replace("\n", "").trim());
				//resp.setStatusMessage(eElement.getElementsByTagName("HostTransaction").item(0).getNextSibling().getTextContent());
			}
		}

		for (int temp = 0; temp < nList1.item(0).getChildNodes().getLength(); ++temp) {

			//System.out.println("Node name >>> " + nList1.item(0).getChildNodes().item(temp).getNodeName());

			switch (nList1.item(0).getChildNodes().item(temp).getNodeName()) {

			case "Error_0":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Error_1":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;	
			case "ErrorDesc":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrMsg":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Status":
				resp.setStatusMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;	
			case "ReasonCode":
				resp.setLienReason(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Rmks":
				resp.setLienRmks(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "LienId":
				resp.setLienID(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;		
			case "NewLienAmt":
				Node node1 = nList1.item(0);
				if (node1.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node1;
					resp.setLienAmt(element.getElementsByTagName("amountValue").item(0)
							.getFirstChild().getTextContent());
				}

				break;
			case "LienDt":
				Node node2 = nList1.item(0);
				if (node2.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node2;
					resp.setLienStartDate(element.getElementsByTagName("StartDt").item(0)
							.getFirstChild().getTextContent());
					
					NodeList nl = element.getElementsByTagName("EndDt");
					if (nl.getLength() > 0) {
						resp.setLienEndDate(element.getElementsByTagName("EndDt").item(0)
								.getFirstChild().getTextContent());
					} else {
						resp.setLienEndDate("");
					}
				
				}

				break;	
			}

		}

		return resp;
	}
	
	
	public static SalaryAccountFIResult parseFinacleSalaryAccountResponse(String response)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src = null;
		String parseStr = "";
		parseStr = response.replaceAll("&lt;", "<");
		parseStr = parseStr.replaceAll("&gt;", ">");
		parseStr = parseStr.replaceAll("&quot;", "\"");
		parseStr = parseStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		logger.debug("XML: " + parseStr);
		src = new InputSource();
		src.setCharacterStream(new StringReader(parseStr));
		Document doc = builder.parse(src);
		SalaryAccountFIResult resp = new SalaryAccountFIResult();
		NodeList nList = null;
		NodeList nList1 = null;

		if (response.contains("<FISystemException>") || response.contains("<FIBusinessException>")) {
			nList = doc.getElementsByTagName("HostTransaction");
			nList1 = doc.getElementsByTagName("ErrorDetail");

		} else

			nList = doc.getElementsByTagName("ResponseHeader");
		nList1 = doc.getElementsByTagName("executeFinacleScript_CustomData");

		for (int temp = 0; temp < nList.getLength(); ++temp) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				resp.setStatusMessage(
						eElement.getElementsByTagName("HostTransaction").item(0).getTextContent().replace("\n", ""));
			}
		}

		for (int temp = 0; temp < nList1.item(0).getChildNodes().getLength(); ++temp) {
			//System.out.println("Node name >>> " + nList1.item(0).getChildNodes().item(temp).getNodeName());

			switch (nList1.item(0).getChildNodes().item(temp).getNodeName()) {
			
			case "Message":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "RESULT_MSG":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Error_0":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Error_1":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrorDesc":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "ErrMsg":
				resp.setResponseMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "Status":
				resp.setStatusMessage(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;	
			case "v_account":
				resp.setvAccount(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			case "v_emp_id":
				resp.setvEmpId(nList1.item(0).getChildNodes().item(temp).getTextContent());
				break;
			}

		}

		return resp;
	}

}
