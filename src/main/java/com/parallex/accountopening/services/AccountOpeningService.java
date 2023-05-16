package com.parallex.accountopening.services;

import org.springframework.stereotype.Component;

import com.parallex.accountopening.domain.AccountDetailsResponse;
import com.parallex.accountopening.domain.AccountReactivationRequest;
import com.parallex.accountopening.domain.AccountRestrictionRequest;
import com.parallex.accountopening.domain.DedupRequest;
import com.parallex.accountopening.domain.RiskRatingRequest;
import com.parallex.accountopening.domain.SalaryAccountResponse;

@Component
public interface AccountOpeningService {

	String dedup(DedupRequest req) throws Exception;

	String accountCreation(String req) throws Exception;

	String riskRating(RiskRatingRequest req) throws Exception;

	String searchPep(String term);

	String searchWatchlist(String term);

	String bvnValidation(String req) throws Exception;

	String bvnValidation1(String req) throws Exception;

	String createIdentityxUsers(String req) throws Exception;

	String accountLienEnquiry(String accountNo) throws Exception;

	String staffRestriction(AccountRestrictionRequest req) throws Exception;

	String accountReactivation(AccountReactivationRequest req) throws Exception;

	//AccountDetailsResponse getAccountDetails(String referralCode) throws Exception;

	AccountDetailsResponse getAccountDetails(String referralCode, String startDate, String endDate) throws Exception;

	SalaryAccountResponse getSalaryAccount(String emailId) throws Exception;

	String validationBVN(String req) throws Exception;

	String accountCreation1(String req) throws Exception;

}
