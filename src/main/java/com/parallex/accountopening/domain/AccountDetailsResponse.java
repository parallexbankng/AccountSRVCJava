package com.parallex.accountopening.domain;

import java.util.ArrayList;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class AccountDetailsResponse {

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String status;

	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private String message;

	@Schema(description = "Result", required = true)
	@Size(max = 255)
	@Pattern(regexp = "(.*?)")
	private ArrayList<Result> result;

	public AccountDetailsResponse() {
		// super();
		// TODO Auto-generated constructor stub
	}

	public AccountDetailsResponse(@Size(max = 255) @Pattern(regexp = "(.*?)") String status,
			@Size(max = 255) @Pattern(regexp = "(.*?)") String message) {
		// super();
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<Result> getResult() {
		return result;
	}

	public void setResult(ArrayList<Result> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "AccountDetailsResponse {status=" + status + ", message=" + message + ", result=" + result + "}";
	}
	
	
	

}
