package com.parallex.accountopening.domain;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Result {

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String accountNo;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String accountName;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String sol_id;
	
	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String acct_opn_date;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String clr_bal_amt;

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

	public String getSol_id() {
		return sol_id;
	}

	public void setSol_id(String sol_id) {
		this.sol_id = sol_id;
	}

	public String getAcct_opn_date() {
		return acct_opn_date;
	}

	public void setAcct_opn_date(String acct_opn_date) {
		this.acct_opn_date = acct_opn_date;
	}

	public String getClr_bal_amt() {
		return clr_bal_amt;
	}

	public void setClr_bal_amt(String clr_bal_amt) {
		this.clr_bal_amt = clr_bal_amt;
	}

	

	
}
