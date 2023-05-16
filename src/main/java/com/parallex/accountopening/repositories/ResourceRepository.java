package com.parallex.accountopening.repositories;

import java.util.ArrayList;

import com.parallex.accountopening.domain.AccountParam;
import com.parallex.accountopening.domain.AcctDetails;
import com.parallex.accountopening.domain.UserDetails;

public interface ResourceRepository {

	String getAccountbyCifid(String schmCode, String cif_id, String currencyCode);

	boolean updateAcctOpeningLog(String sessionId, String uploadFlag);

	Integer getSchemeCodeNo(String sourceId, String schmCode, String glSubHeadCode, String accountCurrencyCode);

	AccountParam getAccountOpeningDefValues(String sourceId, String schmCode, String glSubHeadCode,
			String accountCurrencyCode);

	void insertAcctOpeningLog(String sourceType, String sessionId, String accountno, String cifId, String phoneNo,
			String bvn, String accountType, String bankId, String address, String email, String schmCode,
			String respCode, String cardPickupAddress, String preferredNameonCard);

	boolean insertUserLog(String userId, String createId, String createdDate, String updatedDate, String referenceId,
			String idChecks, String idCheckCreatedDate);

	UserDetails pullUserDetails(String userId);

	//ArrayList<AcctDetails> getAcctDetails(String referralCode);

	ArrayList<AcctDetails> getAcctDetails(String referralCode, String startDate, String endDate);


}
