package com.parallex.accountopening.domain;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryAccountFIResult {

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String vAccount;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String vEmpId;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String statusMessage;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String responseMessage;

	public String getvAccount() {
		return vAccount;
	}

	public void setvAccount(String vAccount) {
		this.vAccount = vAccount;
	}

	public String getvEmpId() {
		return vEmpId;
	}

	public void setvEmpId(String vEmpId) {
		this.vEmpId = vEmpId;
	}

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

}
