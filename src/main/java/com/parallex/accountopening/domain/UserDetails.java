package com.parallex.accountopening.domain;

public class UserDetails {
	 private String createId;
	 private String idChecks;
	 
	
	public UserDetails(String createId, String idChecks) {
		//super();
		this.createId = createId;
		this.idChecks = idChecks;
	}
	public String getCreateId() {
		return createId;
	}
	public void setCreateId(String createId) {
		this.createId = createId;
	}
	public String getIdChecks() {
		return idChecks;
	}
	public void setIdChecks(String idChecks) {
		this.idChecks = idChecks;
	}
	 
	 
	 
}
