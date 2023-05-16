package com.parallex.accountopening.json;

import com.parallex.accountopening.utils.AppUtil;


public class Request {

	public String formAcctOpeningRequest(String title, String crncy_code, String streetNo, String firstName,
			String lastName, String middleName, String gender, String dob, String phonNumber1, String phonNumber2,
			String phonNumber3, String emailAddress, String solId, String nationality, String maritalStatus, String city) {
		String stret ="";
		String[] dob2 = dob.split("-");
		String day = dob2[0];
		String month = dob2[1];
		String year = dob2[2];
		
		String dob3 = year+"-"+month+"-"+day+"T00:00:00.000";
		
		if (streetNo.length() > 45 ) {
			stret = streetNo.substring(0,45);
		} else {
			stret = streetNo;
		}
		//AppUtil.getValueDateTime1()

		return "{\r\n    \"RetCustAddRequest\": {\r\n        \"RetCustAddRq\": {\r\n            \"CustDtls\": {\r\n                \"CustData\": {\r\n                    \"BankId\": \"01\",\r\n                    \"AddrDtls\": {\r\n                        \"AddrCategory\": \"Mailing\",\r\n                        \"CellNum\": \""
				+ phonNumber3
				+ "\",\r\n                        \"City\": \""+city+"\",\r\n                        \"Country\": \"NG\",\r\n                        \"HouseNum\": \"FACT\",\r\n                        \"PrefAddr\": \"Y\",\r\n                        \"PrefFormat\": \"FREE_TEXT_FORMAT\",\r\n                        \"StartDt\": \""
				+ "2022-01-07T11:51:01.585"
				+ "\",\r\n                        \"State\": \"15\",\r\n                        \"StreetName\": \"FACT\",\r\n                        \"StreetNum\": \"FACT\",\r\n                        \"PostalCode\": \"234\",\r\n                        \"AddrLine1\": \""+stret+"\",\r\n                        \"FreeTextLabel\": \"Mailing\",\r\n                        \"HoldMailFlag\": \"N\"\r\n                    },\r\n                    \"BirthDt\": \""
				+ day + "\",\r\n                    \"BirthMonth\": \"" + month
				+ "\",\r\n                    \"BirthYear\": " + Integer.parseInt(year)
				+ ",\r\n                    \"CreatedBySystemId\": \"FIVUSR\",\r\n                    \"DateOfBirth\": \""+dob3+"\",\r\n                    \"FirstName\": \""
				+ firstName
				+ "\",\r\n                    \"Language\": \"UK (English)\",\r\n                    \"LastName\": \""
				+ lastName + "\",\r\n                    \"MiddleName\": \"" + middleName
				+ "\",\r\n                    \"IsMinor\": \"N\",\r\n                    \"IsCustNRE\": \"N\",\r\n                    \"DefaultAddrType\": \"Mailing\",\r\n                    \"Gender\": \""
				+ gender
				+ "\",\r\n                    \"NativeLanguageCode\": \"INFENG\",\r\n                    \"Manager\": \"UBSADMIN\",\r\n                    \"Occupation\": \"DUMMY\",\r\n                    \"PhoneEmailDtls\": {\r\n                        \"PhoneEmailType\": \"CELLPH\",\r\n                        \"PhoneNumCityCode\": \""
				+ phonNumber2 + "\",\r\n                        \"PhoneNumCountryCode\": \"" + phonNumber1
				+ "\",\r\n                        \"PhoneNumLocalCode\": \"" + phonNumber3
				+ "\",\r\n                        \"PhoneOrEmail\": \"PHONE\",\r\n                        \"PrefFlag\": \"Y\"\r\n                    },\r\n                    \"PhoneEmailDtl1s\": {\r\n                        \"Email\": \""
				+ emailAddress
				+ "\",\r\n                        \"PhoneEmailType\": \"COMMEML\",\r\n                        \"PhoneOrEmail\": \"EMAIL\",\r\n                        \"PrefFlag\": \"Y\"\r\n                    },\r\n                    \"PrefName\": \""
				+ firstName + "\",\r\n                    \"PrimarySolId\": \"" + solId
				+ "\",\r\n                    \"Region\": \"Lagos\",\r\n                    \"RelationshipOpeningDt\": \"2020-08-03T00:00:00.000\",\r\n                    \"Salutation\": \""
				+ title
				+ "\",\r\n                    \"SecondaryRMId\": \"\",\r\n                    \"SegmentationClass\": \"DUMMY\",\r\n                    \"ShortName\": \""
				+ firstName
				+ "\",\r\n                    \"StaffFlag\": \"N\",\r\n                    \"SubSegment\": \"DUMMY\",\r\n                    \"TaxDeductionTable\": \"C1.00\",\r\n                    \"TertiaryRMId\": \"UBSADMIN\",\r\n                    \"TradeFinFlag\": \"Y\",\r\n                    \"RelationshipMgrID\": \"5\",\r\n                    \"RelationshipCreatedByID\": \"5\",\r\n                    \"IsEbankingEnabled\": \"N\"\r\n                }\r\n            },\r\n            \"RelatedDtls\": {\r\n                \"TradeFinData\": {\r\n                    \"CustNative\": \"Y\",\r\n                    \"InlandTradeAllowed\": \"Y\"\r\n                },\r\n                \"DemographicData\": {\r\n                    \"EmploymentStatus\": \"OTHER\",\r\n                    \"MaritalStatus\": \"DUMMY\",\r\n                    \"Nationality\": \"DUMMY\"\r\n                },\r\n                \"EntityDoctData\": {\r\n                    \"CountryOfIssue\": \"NG\",\r\n                    \"DocCode\": \"DUMMY\",\r\n                    \"IssueDt\": \"2020-08-03T00:00:00.000\",\r\n                    \"TypeCode\": \"DUMMY\",\r\n                    \"PlaceOfIssue\": \"DUMMY\",\r\n                    \"ReferenceNum\": \"1\",\r\n                    \"preferredUniqueId\": \"Y\",\r\n                    \"IDIssuedOrganisation\": \"DUMMY\"\r\n                },\r\n                \"PsychographicData\": {\r\n                    \"PsychographMiscData\": {\r\n                        \"StrText10\": \""
				+ crncy_code
				+ "\",\r\n                        \"Type\": \"CURRENCY\",\r\n                        \"DTDt1\": \"2099-12-31T00:00:00.000\"\r\n                    },\r\n                    \"preferred_Locale\": \"en_US\"\r\n                }\r\n            }\r\n        },\r\n        \"RetCustAdd_CustomData\": {\r\n            \"mlUserField2\": \"\",\r\n            \"dbFloat3_0\": 10\r\n        }\r\n    }\r\n}";

	}

	public String formSavingsAccountCreation(String cif_id, String bvn, String referalcode, String schmcode,
			String schmtype) {
		return "{\n    \"sbAcctAddRq\": {\n        \"custId\": {\n            \"custId\": \"" + cif_id
				+ "\",\n            \"custBVN\": \"" + bvn + "\",\n            \"referralCode\": \"" + referalcode
				+ "\"\n        },\n        \"sbAcctId\": {\n            \"acctType\": {\n                \"schmCode\": \""
				+ schmcode + "\",\n                \"schmType\": \"" + schmtype
				+ "\"\n            },\n            \"acctCurr\": \"NGN\",\n            \"bankInfo\": {\n                \"bankId\": \"01\",\n                \"branchId\": \"001\"\n            }\n        }\n    }\n}";
	}

	
	public String formAcctLienModificationRequest(String accountNo, String txnAmount, String lienId) {
	
		
		return "{\r\n  \"AcctLienModRequest\": {\r\n    \"AcctLienModRq\": {\r\n      \"BankId\": \"01\",\r\n      \"AcctId\": {\r\n        \"AcctId\": \""+accountNo+"\"\r\n      },\r\n      \"ModuleType\": \"ULIEN\",\r\n      \"LienDtls\": {\r\n        \"LienId\": \""+lienId+"\",\r\n        \"NewLienAmt\": {\r\n          \"amountValue\": "+Integer.parseInt(txnAmount)+",\r\n          \"currencyCode\": \"NGN\"\r\n        },\r\n        \"LienDt\": {\r\n          \"StartDt\": \""+AppUtil.getValueDateTime()+"\",\r\n          \"EndDt\": \"2099-12-31T23:00:00.000\"\r\n        },\r\n        \"ReasonCode\": \"Test\"\r\n      }\r\n    }\r\n  }\r\n}";

	}

}
