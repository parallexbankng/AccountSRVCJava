package com.parallex.accountopening.domain;

public class RiskRatingRequest {

	private String cifId;
	private String rating;

	public String getCifId() {
		return cifId;
	}

	public void setCifId(String cifId) {
		this.cifId = cifId;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

}
