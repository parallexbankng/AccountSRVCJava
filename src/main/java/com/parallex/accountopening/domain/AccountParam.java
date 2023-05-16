package com.parallex.accountopening.domain;

public class AccountParam {
	
	private String rmId;
	private String solId;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String occupationCode;
	private String maritalStatus;
	private String typeOfCode;
	private String crncyCode;
	private String schmCode;
	private String glSubHeadCode;
	private String acctType;
	private String schmCodeDesc;
	private String minorInd;
	private String nonResInd;
	private String intBank;
	private String smsAlert;
	private String segMent;
	private String subSegment;
	private String staffInd;
	private String dualCurrency;
	private String freq;
	private String estatement;
	private String ccyLength;
	private String jointAcctFlag;
	private String usPers;
	private String pepFlag;
	private String pepRelationship;
	private String kycInd;
	private String otherRelationship;
	private String acctHold;
	private String isAcctDocComp;
	private String reltdPtyDetCnt;
	private String mobBank;
	private String riskRtg;
	private String nationality;
	private String countryOfResidence;
	private String title;
	private String addressType;
	private String relStartDate;
	private String address;
	private String localGovt;
	private String relationShipMangerId;
	private String typeOfId;
	private String issueAuthority;
	private String regNum;
	private String placeOfIssue;
	private String issueDate;
	private String empType;
	
	
	public AccountParam(String schmCode) {
		//super();
		this.schmCode = schmCode;
	}
	public AccountParam(String rmId, String solId, String city, String state, String country, String postalCode,
			String occupationCode, String maritalStatus, String typeOfCode, String crncyCode, String schmCode,
			String glSubHeadCode, String acctType, String schmCodeDesc, String minorInd, String nonResInd,
			String intBank, String smsAlert, String segMent, String subSegment, String staffInd, String dualCurrency,
			String freq, String estatement, String ccyLength, String jointAcctFlag, String usPers, String pepFlag,
			String pepRelationship, String kycInd, String otherRelationship, String acctHold, String isAcctDocComp,
			String reltdPtyDetCnt, String mobBank, String riskRtg, String nationality, String countryOfResidence,
			String title, String addressType, String relStartDate, String address, String localGovt,
			String relationShipMangerId, String typeOfId, String issueAuthority, String regNum, String placeOfIssue,
			String issueDate, String empType) {
		//super();
		this.rmId = rmId;
		this.solId = solId;
		this.city = city;
		this.state = state;
		this.country = country;
		this.postalCode = postalCode;
		this.occupationCode = occupationCode;
		this.maritalStatus = maritalStatus;
		this.typeOfCode = typeOfCode;
		this.crncyCode = crncyCode;
		this.schmCode = schmCode;
		this.glSubHeadCode = glSubHeadCode;
		this.acctType = acctType;
		this.schmCodeDesc = schmCodeDesc;
		this.minorInd = minorInd;
		this.nonResInd = nonResInd;
		this.intBank = intBank;
		this.smsAlert = smsAlert;
		this.segMent = segMent;
		this.subSegment = subSegment;
		this.staffInd = staffInd;
		this.dualCurrency = dualCurrency;
		this.freq = freq;
		this.estatement = estatement;
		this.ccyLength = ccyLength;
		this.jointAcctFlag = jointAcctFlag;
		this.usPers = usPers;
		this.pepFlag = pepFlag;
		this.pepRelationship = pepRelationship;
		this.kycInd = kycInd;
		this.otherRelationship = otherRelationship;
		this.acctHold = acctHold;
		this.isAcctDocComp = isAcctDocComp;
		this.reltdPtyDetCnt = reltdPtyDetCnt;
		this.mobBank = mobBank;
		this.riskRtg = riskRtg;
		this.nationality = nationality;
		this.countryOfResidence = countryOfResidence;
		this.title = title;
		this.addressType = addressType;
		this.relStartDate = relStartDate;
		this.address = address;
		this.localGovt = localGovt;
		this.relationShipMangerId = relationShipMangerId;
		this.typeOfId = typeOfId;
		this.issueAuthority = issueAuthority;
		this.regNum = regNum;
		this.placeOfIssue = placeOfIssue;
		this.issueDate = issueDate;
		this.empType = empType;
	}
	public String getRmId() {
		return rmId;
	}
	public void setRmId(String rmId) {
		this.rmId = rmId;
	}
	public String getSolId() {
		return solId;
	}
	public void setSolId(String solId) {
		this.solId = solId;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getOccupationCode() {
		return occupationCode;
	}
	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getTypeOfCode() {
		return typeOfCode;
	}
	public void setTypeOfCode(String typeOfCode) {
		this.typeOfCode = typeOfCode;
	}
	public String getCrncyCode() {
		return crncyCode;
	}
	public void setCrncyCode(String crncyCode) {
		this.crncyCode = crncyCode;
	}
	public String getSchmCode() {
		return schmCode;
	}
	public void setSchmCode(String schmCode) {
		this.schmCode = schmCode;
	}
	public String getGlSubHeadCode() {
		return glSubHeadCode;
	}
	public void setGlSubHeadCode(String glSubHeadCode) {
		this.glSubHeadCode = glSubHeadCode;
	}
	public String getAcctType() {
		return acctType;
	}
	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}
	public String getSchmCodeDesc() {
		return schmCodeDesc;
	}
	public void setSchmCodeDesc(String schmCodeDesc) {
		this.schmCodeDesc = schmCodeDesc;
	}
	public String getMinorInd() {
		return minorInd;
	}
	public void setMinorInd(String minorInd) {
		this.minorInd = minorInd;
	}
	public String getNonResInd() {
		return nonResInd;
	}
	public void setNonResInd(String nonResInd) {
		this.nonResInd = nonResInd;
	}
	public String getIntBank() {
		return intBank;
	}
	public void setIntBank(String intBank) {
		this.intBank = intBank;
	}
	public String getSmsAlert() {
		return smsAlert;
	}
	public void setSmsAlert(String smsAlert) {
		this.smsAlert = smsAlert;
	}
	public String getSegMent() {
		return segMent;
	}
	public void setSegMent(String segMent) {
		this.segMent = segMent;
	}
	public String getSubSegment() {
		return subSegment;
	}
	public void setSubSegment(String subSegment) {
		this.subSegment = subSegment;
	}
	public String getStaffInd() {
		return staffInd;
	}
	public void setStaffInd(String staffInd) {
		this.staffInd = staffInd;
	}
	public String getDualCurrency() {
		return dualCurrency;
	}
	public void setDualCurrency(String dualCurrency) {
		this.dualCurrency = dualCurrency;
	}
	public String getFreq() {
		return freq;
	}
	public void setFreq(String freq) {
		this.freq = freq;
	}
	public String getEstatement() {
		return estatement;
	}
	public void setEstatement(String estatement) {
		this.estatement = estatement;
	}
	public String getCcyLength() {
		return ccyLength;
	}
	public void setCcyLength(String ccyLength) {
		this.ccyLength = ccyLength;
	}
	public String getJointAcctFlag() {
		return jointAcctFlag;
	}
	public void setJointAcctFlag(String jointAcctFlag) {
		this.jointAcctFlag = jointAcctFlag;
	}
	public String getUsPers() {
		return usPers;
	}
	public void setUsPers(String usPers) {
		this.usPers = usPers;
	}
	public String getPepFlag() {
		return pepFlag;
	}
	public void setPepFlag(String pepFlag) {
		this.pepFlag = pepFlag;
	}
	public String getPepRelationship() {
		return pepRelationship;
	}
	public void setPepRelationship(String pepRelationship) {
		this.pepRelationship = pepRelationship;
	}
	public String getKycInd() {
		return kycInd;
	}
	public void setKycInd(String kycInd) {
		this.kycInd = kycInd;
	}
	public String getOtherRelationship() {
		return otherRelationship;
	}
	public void setOtherRelationship(String otherRelationship) {
		this.otherRelationship = otherRelationship;
	}
	public String getAcctHold() {
		return acctHold;
	}
	public void setAcctHold(String acctHold) {
		this.acctHold = acctHold;
	}
	public String getIsAcctDocComp() {
		return isAcctDocComp;
	}
	public void setIsAcctDocComp(String isAcctDocComp) {
		this.isAcctDocComp = isAcctDocComp;
	}
	public String getReltdPtyDetCnt() {
		return reltdPtyDetCnt;
	}
	public void setReltdPtyDetCnt(String reltdPtyDetCnt) {
		this.reltdPtyDetCnt = reltdPtyDetCnt;
	}
	public String getMobBank() {
		return mobBank;
	}
	public void setMobBank(String mobBank) {
		this.mobBank = mobBank;
	}
	public String getRiskRtg() {
		return riskRtg;
	}
	public void setRiskRtg(String riskRtg) {
		this.riskRtg = riskRtg;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getCountryOfResidence() {
		return countryOfResidence;
	}
	public void setCountryOfResidence(String countryOfResidence) {
		this.countryOfResidence = countryOfResidence;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	public String getRelStartDate() {
		return relStartDate;
	}
	public void setRelStartDate(String relStartDate) {
		this.relStartDate = relStartDate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocalGovt() {
		return localGovt;
	}
	public void setLocalGovt(String localGovt) {
		this.localGovt = localGovt;
	}
	public String getRelationShipMangerId() {
		return relationShipMangerId;
	}
	public void setRelationShipMangerId(String relationShipMangerId) {
		this.relationShipMangerId = relationShipMangerId;
	}
	public String getTypeOfId() {
		return typeOfId;
	}
	public void setTypeOfId(String typeOfId) {
		this.typeOfId = typeOfId;
	}
	public String getIssueAuthority() {
		return issueAuthority;
	}
	public void setIssueAuthority(String issueAuthority) {
		this.issueAuthority = issueAuthority;
	}
	public String getRegNum() {
		return regNum;
	}
	public void setRegNum(String regNum) {
		this.regNum = regNum;
	}
	public String getPlaceOfIssue() {
		return placeOfIssue;
	}
	public void setPlaceOfIssue(String placeOfIssue) {
		this.placeOfIssue = placeOfIssue;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public String getEmpType() {
		return empType;
	}
	public void setEmpType(String empType) {
		this.empType = empType;
	}
	
	
	

}
