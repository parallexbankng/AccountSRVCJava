package com.parallex.accountopening.domain;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryAccountResponse {

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String statusCode;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String statusMessage;

	
	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String responseMessage;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String accountNo;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String employeeId;
	
	
	
	
	public SalaryAccountResponse() {
		//super();
		// TODO Auto-generated constructor stub
	}
	
	

	public SalaryAccountResponse(@Size(max = 255) @Pattern(regexp = "(.*?)") String statusCode,
			@Size(max = 255) @Pattern(regexp = "(.*?)") String statusMessage) {
		//super();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
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

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}



	@Override
	public String toString() {
		return "SalaryAccountResponse {statusCode=" + statusCode + ", statusMessage=" + statusMessage
				+ ", responseMessage=" + responseMessage + ", accountNo=" + accountNo + ", employeeId=" + employeeId
				+ "}";
	}
	
	

}
