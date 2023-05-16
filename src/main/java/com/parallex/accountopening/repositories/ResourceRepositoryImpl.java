package com.parallex.accountopening.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.parallex.accountopening.Constants;
import com.parallex.accountopening.domain.AccountParam;
import com.parallex.accountopening.domain.AcctDetails;
import com.parallex.accountopening.domain.UserDetails;



@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

	private static Logger log = LoggerFactory.getLogger(ResourceRepositoryImpl.class);

	private static final String SQL_CREATE_ACCOUNTOPENING = "INSERT INTO ACCT_OPN_HISTORY(SOURCE_TYPE,SESSION_ID,ACCOUNT_NUMBER,CIF_ID,PHONE_NUMBER,BVN,ACCOUNT_TYPE,BANK_ID,ADDRESS,EMAIL,SCHM_CODE,REQUEST_DATE,RESPONSE_MESSAGE,DELIVERY_ADDRESS,PREFERRED_NAME) values (?,?,?,?,?,?,?,?,?,?,?,GETDATE(),?,?,?)";
	
	private static final String SQL_CREATE_DAON_USER = "INSERT INTO DAON_USERS(USERID,CREATE_ID,CREATED_DATE,UPDATED_DATE,REFERENCE_ID,ID_CHECKS,ID_CHECKS_CREATED_DATE,REQUESTDATE) values (?,?,?,?,?,?,?,GETDATE())";
	
	private static final String SQL_GET_DETAILS_CIFID = "Select FORACID from tbaadm.gam where cif_id=? and SCHM_CODE=? and acct_crncy_code =? and bank_id='01' and del_flg='N' and acct_cls_flg='N'";
	
	private static final String SQL_UPDATE_ACCOUNTOPENING = "UPDATE ACCT_OPN_HISTORY SET MANDATE_CARD_UPLOAD_FLG = ? WHERE SESSION_ID = ?";
	
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_1 ="SELECT count(*) FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and SCHM_CODE=? and GL_SUB_HEAD_CODE=? and CRNCY_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_2 ="SELECT count(*) FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and SCHM_CODE=? and GL_SUB_HEAD_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_3 ="SELECT count(*) FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and SCHM_CODE=? and CRNCY_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_4 ="SELECT count(*) FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and SCHM_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_5 ="SELECT count(*) FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and CRNCY_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_6 ="SELECT count(*) FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=?";
	
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_7 ="SELECT * FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and SCHM_CODE=? and GL_SUB_HEAD_CODE=? and CRNCY_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_8 ="SELECT * FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and SCHM_CODE=? and GL_SUB_HEAD_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_9 ="SELECT * FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and SCHM_CODE=? and CRNCY_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_10 ="SELECT * FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and SCHM_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_11="SELECT * FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=? and CRNCY_CODE=?";
	private static final String SQL_GET_ACCOUNTOPENING_CONFIG_12 ="SELECT * FROM ACCT_OPEN_NEW_DEF WHERE BANKID=? and SOURCE_ID=?";
	
	private static final String SQL_FIND_BY_USERID = "SELECT CREATE_ID, ID_CHECKS FROM DAON_USERS WHERE USERID = ?";
	
	//private static final String SQL_GET_ACCT_DETAILS = "select g.foracid, g.acct_name, g.sol_id, g.acct_opn_date, g.CLR_BAL_AMT from tbaadm.gam g, tbaadm.gac c where g.acid=c.acid and c.free_code_2=?";
	
	//private static final String SQL_GET_ACCT_DETAILS = "select g.foracid, g.acct_name, g.sol_id, g.acct_opn_date, g.CLR_BAL_AMT from tbaadm.gam g, tbaadm.gac c where g.acid=c.acid and c.free_code_2=? and ACCT_OPN_DATE Between to_date(?,'yyyy-mm-dd') And to_date(?,'yyyy-mm-dd')";
	private static final String SQL_GET_ACCT_DETAILS = "select g.foracid, g.acct_name, g.sol_id, g.acct_opn_date, g.CLR_BAL_AMT from tbaadm.gam g, tbaadm.gac c where g.acid=c.acid and upper(c.free_code_2)=? and ACCT_OPN_DATE Between to_date(?,'yyyy-mm-dd') And to_date(?,'yyyy-mm-dd')";

	
	
	@Autowired
	@Qualifier("mssqlJDBCTemplate")
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("finacleJDBCTemplate")
	private JdbcTemplate jdbcTemplate1;
	
	@Override
	public void insertAcctOpeningLog(String sourceType, String sessionId, String accountno, String cifId, String phoneNo, String bvn, String accountType, String bankId, String address, String email, String schmCode,  String respCode, String cardPickupAddress, String preferredNameonCard) {
		try {
			log.debug("Query >>> "+ SQL_CREATE_ACCOUNTOPENING);
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(SQL_CREATE_ACCOUNTOPENING,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, sourceType);
				ps.setString(2, sessionId);
				ps.setString(3, accountno);
				ps.setString(4, cifId);
				ps.setString(5, phoneNo);
				ps.setString(6, bvn);
				ps.setString(7, accountType);
				ps.setString(8, bankId);
				ps.setString(9, address);
				ps.setString(10, email);
				ps.setString(11, schmCode);
				ps.setString(12, respCode);
				ps.setString(13, cardPickupAddress);
				ps.setString(14, preferredNameonCard);
				return ps;
			}, keyHolder);

			log.debug("Record Inserted to ACCT_OPN_HISTORY table successfully");
			return;

		} catch (Exception e) {
			log.error("Error inserting record: " + e.fillInStackTrace());
			// new NqrBadRequestException("Invalid request");
		}

		return;
	}
	
	@Override

	public boolean insertUserLog(String userId, String createId, String createdDate, String updatedDate, String referenceId, String idChecks, String idCheckCreatedDate) {
		boolean response = false;
		try {
			log.debug("Query >>> "+ SQL_CREATE_DAON_USER);
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(SQL_CREATE_DAON_USER,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, userId);
				ps.setString(2, createId);
				ps.setString(3, createdDate);
				ps.setString(4, updatedDate);
				ps.setString(5, referenceId);
				ps.setString(6, idChecks);
				ps.setString(7, idCheckCreatedDate);
				return ps;
			}, keyHolder);

			log.debug("Record Inserted to User Creation table successfully");
			return true;

		} catch (Exception e) {
			log.error("Error inserting record: " + e.fillInStackTrace());
			// new NqrBadRequestException("Invalid request");
			response = false;
		}

		return response;
	}
	
	@Override
	public UserDetails pullUserDetails(String userId) {
		log.debug("Query : " + SQL_FIND_BY_USERID);
		try {
			return jdbcTemplate.queryForObject(SQL_FIND_BY_USERID, new Object[] { userId }, userDetailsRowMapper);
		} catch (Exception e) {
			log.error("Get Credentials By UserId Error: " + e.fillInStackTrace());
			//throw new NqrResourceNotFoundException("Unknown reference");
			return null;
		}
	}
	
	private RowMapper<UserDetails> userDetailsRowMapper = ((rs, rowNum) -> {
		return new UserDetails(rs.getString("CREATE_ID"), rs.getString("ID_CHECKS"));
	});
	
	@Override
	public String getAccountbyCifid(String schmCode, String cif_id, String currencyCode) {
		String result = null;
		log.debug("Query >>> " + SQL_GET_DETAILS_CIFID);
		log.debug("Schmcode >>> " + schmCode);
		log.debug("Cif_id >>> " + cif_id);
		log.debug("CurrencyCode >>> " + currencyCode);
		
		try {
			return jdbcTemplate1.queryForObject(SQL_GET_DETAILS_CIFID, new Object[] { cif_id, schmCode, currencyCode }, String.class);
		} catch (EmptyResultDataAccessException e) {
			// return new FinAccount("");
			result = null;
		} catch (Exception e) {

			log.error("Oops!!! error encountered while retrieving class: " + e.fillInStackTrace());
			result = null;
		}
		
		return result;


	}
	
	@Override
	public boolean updateAcctOpeningLog(String sessionId, String uploadFlag) {
		boolean res = false;
		log.debug("Query >>> " + SQL_UPDATE_ACCOUNTOPENING);

		try {
			jdbcTemplate.update(SQL_UPDATE_ACCOUNTOPENING, new Object[] { uploadFlag, sessionId});
			log.debug("Record updated successfully with sessionid >>> " + sessionId);
			res = true;
		
			log.debug("res boolean  >>> " + Boolean.valueOf(res));
			return res;
		} catch (Exception e) {
			log.error("Update Mandate Upload Error: " + e.fillInStackTrace());
			res = false;
		}
		return res;
	}
	
	
	@Override
	public Integer getSchemeCodeNo(String sourceId, String schmCode, String glSubHeadCode, String accountCurrencyCode) {
		int res = 0;
		
		try {
			
			if (!schmCode.equalsIgnoreCase("") && !glSubHeadCode.equalsIgnoreCase("") && !accountCurrencyCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_1);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_1, new Object[] { Constants.BANK_ID, sourceId, schmCode,glSubHeadCode,accountCurrencyCode }, Integer.class);
			} else if (!schmCode.equalsIgnoreCase("") && !glSubHeadCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_2);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_2, new Object[] { Constants.BANK_ID,sourceId, schmCode,glSubHeadCode }, Integer.class);
			} else if (!schmCode.equalsIgnoreCase("") && !accountCurrencyCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_3);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_3, new Object[] { Constants.BANK_ID,sourceId,schmCode,accountCurrencyCode }, Integer.class);
			} else if (!schmCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_4);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_4, new Object[] { Constants.BANK_ID,sourceId,schmCode }, Integer.class);
			} else if (!accountCurrencyCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_5);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_5, new Object[] { Constants.BANK_ID,sourceId,accountCurrencyCode }, Integer.class);
			} else {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_6);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_6, new Object[] { Constants.BANK_ID,sourceId }, Integer.class);
			
			}
			
			

		} catch (EmptyResultDataAccessException e) {
			// throw new AtAuthException("Invalid username/password");
			return res;
		} catch (Exception e) {
			log.error("Error fetching customer details: " + e.fillInStackTrace());
			return res;
		}
		// return null;
	}
	
	
	@Override
	public AccountParam getAccountOpeningDefValues(String sourceId, String schmCode, String glSubHeadCode, String accountCurrencyCode) {


		try {
			if (!schmCode.equalsIgnoreCase("") && !glSubHeadCode.equalsIgnoreCase("") && !accountCurrencyCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_7);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_7, new Object[] { Constants.BANK_ID, sourceId, schmCode,glSubHeadCode,accountCurrencyCode }, hsmDetailsRowMapper);
			} else if (!schmCode.equalsIgnoreCase("") && !glSubHeadCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_8);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_8, new Object[] { Constants.BANK_ID,sourceId, schmCode,glSubHeadCode }, hsmDetailsRowMapper);
			} else if (!schmCode.equalsIgnoreCase("") && !accountCurrencyCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_9);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_9, new Object[] { Constants.BANK_ID,sourceId,schmCode,accountCurrencyCode }, hsmDetailsRowMapper);
			} else if (!schmCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_10);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_10, new Object[] { Constants.BANK_ID,sourceId,schmCode }, hsmDetailsRowMapper);
			} else if (!accountCurrencyCode.equalsIgnoreCase("")) {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_11);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_11, new Object[] { Constants.BANK_ID,sourceId,accountCurrencyCode }, hsmDetailsRowMapper);
			} else {
				log.debug("Query >>> " + SQL_GET_ACCOUNTOPENING_CONFIG_12);
				return jdbcTemplate.queryForObject(SQL_GET_ACCOUNTOPENING_CONFIG_12, new Object[] { Constants.BANK_ID,sourceId }, hsmDetailsRowMapper);
			
			}
			

		} catch (EmptyResultDataAccessException e) {
			// log.error("Oops!!! error encountered while getting pan details " +
			// e.fillInStackTrace());
			return new AccountParam("");
		} catch (Exception e) {

			log.error("Oops!!! error encountered while getting details " + e.fillInStackTrace());

		}
		return null;

	}

	private RowMapper<AccountParam> hsmDetailsRowMapper = ((rs, rowNum) -> {

		AccountParam acctDt = new AccountParam(rs.getString("RM_ID"), rs.getString("SOL_ID"), rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getString("POSTAL_CODE"),
				rs.getString("OCCUPATION_CODE"), rs.getString("MARITAL_STATUS"), rs.getString("TYPE_OF_CODE"), rs.getString("CRNCY_CODE"), rs.getString("SCHM_CODE"),
				rs.getString("GL_SUB_HEAD_CODE"), rs.getString("ACCT_TYPE"), rs.getString("SCHM_CODE_DESCRIPTION"), rs.getString("MINOR_IND"), rs.getString("NON_RES_IND"),
				rs.getString("INT_BANK"), rs.getString("SMS_ALERT"), rs.getString("SEG_MENT"), rs.getString("SUB_SEG_MENT"), rs.getString("STAFF_IND"), rs.getString("DUAL_RESIDENCY"),
				rs.getString("FREQ"), rs.getString("ESTATEMENT"), rs.getString("CCY_LENGTH"), rs.getString("JOINT_ACCT_FLG"), rs.getString("USPERS"), rs.getString("PEP_FLG"),
				rs.getString("PEP_RELATIONSHIP"), rs.getString("KYC_IND"), rs.getString("OTHER_RELATIONSHIP"), rs.getString("ACCT_HOLD"), rs.getString("IS_ACCT_DOC_COMP"),
				rs.getString("RELTD_PTY_DET_CNT"), rs.getString("MOB_BANK"), rs.getString("RISK_RTG"), rs.getString("NATIONALITY"), rs.getString("COUNTRY_0F_RESIDENCE"),
				rs.getString("TITLE"), rs.getString("ADDRESS_TYPE"), rs.getString("REL_STRT_DATE"), rs.getString("ADDRESS"), rs.getString("LOCAL_GOVT"),
				rs.getString("RELATIONSHIP_MANAGER_ID"), rs.getString("TYPE_OF_ID"), rs.getString("ISSUE_AUTHORITY"), rs.getString("REG_NUM"), rs.getString("PLACE_OF_ISSUE"),
				rs.getString("ISSUE_DATE"), rs.getString("EMP_TYPE"));

		return acctDt;

	});
	
	
	@Override
	public ArrayList<AcctDetails> getAcctDetails(String referralCode, String startDate, String endDate) {

		log.debug("Query >>> " + SQL_GET_ACCT_DETAILS);

		try {
			return jdbcTemplate1.queryForObject(SQL_GET_ACCT_DETAILS, new Object[] { referralCode, startDate, endDate },
					finTxntDetailsRowMapper);

		} catch (EmptyResultDataAccessException e) {
			// log.error("Oops!!! error encountered while getting pan details " +
			// e.fillInStackTrace());
			return null;
		} catch (Exception e) {

			log.error("Oops!!! error encountered while getting transaction details" + e.fillInStackTrace());
			return null;
		}
		

	}

	private RowMapper<ArrayList<AcctDetails>> finTxntDetailsRowMapper = ((rs, rowNum) -> {
		ArrayList<AcctDetails> accounts = new ArrayList<>();
		do {
			AcctDetails acct = new  AcctDetails(rs.getString("FORACID"), rs.getString("ACCT_NAME"), rs.getString("SOL_ID"), rs.getString("ACCT_OPN_DATE"), rs.getString("CLR_BAL_AMT"));
			
			accounts.add(acct);

		} while (rs.next());

		log.debug("statement executed successfully!!!");
		return accounts;

	});

	

	

}
