package com.parallex.accountopening.domain;

public class AccountLienInquiryResponse {

	private String statusMessage;
	private String responseMessage;
	private String lienAmt;
	private String lienReason;
	private String lienRmks;
	private String lienID;
	private String lienStartDate;
	private String lienEndDate;

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getLienAmt() {
		return lienAmt;
	}

	public void setLienAmt(String lienAmt) {
		this.lienAmt = lienAmt;
	}

	public String getLienReason() {
		return lienReason;
	}

	public void setLienReason(String lienReason) {
		this.lienReason = lienReason;
	}

	public String getLienRmks() {
		return lienRmks;
	}

	public void setLienRmks(String lienRmks) {
		this.lienRmks = lienRmks;
	}

	public String getLienID() {
		return lienID;
	}

	public void setLienID(String lienID) {
		this.lienID = lienID;
	}

	public String getLienStartDate() {
		return lienStartDate;
	}

	public void setLienStartDate(String lienStartDate) {
		this.lienStartDate = lienStartDate;
	}

	public String getLienEndDate() {
		return lienEndDate;
	}

	public void setLienEndDate(String lienEndDate) {
		this.lienEndDate = lienEndDate;
	}

}
