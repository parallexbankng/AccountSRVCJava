package com.parallex.accountopening.domain;

public class AccountReactivationResponse {

	private String statusMessage;
	private String accountStatus;
	private String message;
	private String acctID;
	private String responseMessage;

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAcctID() {
		return acctID;
	}

	public void setAcctID(String acctID) {
		this.acctID = acctID;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}


}
