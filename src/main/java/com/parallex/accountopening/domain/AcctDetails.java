package com.parallex.accountopening.domain;

public class AcctDetails {
	private String accountNo;
	private String accountName;
	private String solId;
	private String acctOpenDate;
	private String clrBalAmt;
	
	public AcctDetails(String accountNo, String accountName, String solId, String acctOpenDate, String clrBalAmt) {
		//super();
		this.accountNo = accountNo;
		this.accountName = accountName;
		this.solId = solId;
		this.acctOpenDate = acctOpenDate;
		this.clrBalAmt = clrBalAmt;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getSolId() {
		return solId;
	}
	public void setSolId(String solId) {
		this.solId = solId;
	}
	public String getAcctOpenDate() {
		return acctOpenDate;
	}
	public void setAcctOpenDate(String acctOpenDate) {
		this.acctOpenDate = acctOpenDate;
	}
	public String getClrBalAmt() {
		return clrBalAmt;
	}
	public void setClrBalAmt(String clrBalAmt) {
		this.clrBalAmt = clrBalAmt;
	}
	
	
	
	
	
}
