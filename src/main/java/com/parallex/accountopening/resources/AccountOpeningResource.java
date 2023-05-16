package com.parallex.accountopening.resources;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.parallex.accountopening.domain.AccountDetailsResponse;
import com.parallex.accountopening.domain.AccountReactivationRequest;
import com.parallex.accountopening.domain.AccountRestrictionRequest;
import com.parallex.accountopening.domain.DedupRequest;
import com.parallex.accountopening.domain.RiskRatingRequest;
import com.parallex.accountopening.domain.SalaryAccountResponse;
import com.parallex.accountopening.response.ResponseHandler;
import com.parallex.accountopening.services.AccountOpeningService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
public class AccountOpeningResource {
	private static Logger logger = LoggerFactory.getLogger(AccountOpeningResource.class);

	// @Autowired
	// AuthService authService;

	@Autowired
	AccountOpeningService accountOpeningService;

	// @Autowired
	// WebUtils webUtils;

	@PostMapping(value = "/dedup")
	public ResponseEntity<Object> dedup(@RequestBody DedupRequest request) {
		logger.debug("=================================");
		logger.debug("Dedup Request");
		logger.debug("=================================");

		try {

			String response = accountOpeningService.dedup(request);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(value = "/riskrating")
	public ResponseEntity<Object> riskRating(@RequestBody RiskRatingRequest request) {
		logger.debug("=================================");
		logger.debug("Risk Rating Request");
		logger.debug("=================================");

		try {

			String response = accountOpeningService.riskRating(request);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@GetMapping(value = "/searchPep/{term}")
	public ResponseEntity<Object> searchPep(@PathVariable("term") String term) {
		logger.debug("=================================");
		logger.debug("Search Pep Request");
		logger.debug("=================================");
		
		logger.debug("Term >>> " + term);

		try {

			String response = accountOpeningService.searchPep(term);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@GetMapping(value = "/searchWatchlist/{term}")
	public ResponseEntity<Object> searchWatchlist(@PathVariable("term") String term) {
		logger.debug("=================================");
		logger.debug("Search Watchlist Request");
		logger.debug("=================================");
		
		logger.debug("Term >>> " + term);

		try {

			String response = accountOpeningService.searchWatchlist(term);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(value = "/retail/savingsCurrent")
	public ResponseEntity<Object> accountCreation(@RequestBody Map<String, Object> payload) {
		logger.debug("=================================");
		logger.debug("Account Opening Request");
		logger.debug("=================================");
		Gson gson = new Gson();
		String json = gson.toJson(payload);
		
		try {

			String response = accountOpeningService.accountCreation(json);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(value = "/retail/savingsCurrent1")
	public ResponseEntity<Object> accountCreation1(@RequestBody Map<String, Object> payload) {
		logger.debug("=================================");
		logger.debug("Account Opening Request");
		logger.debug("=================================");
		Gson gson = new Gson();
		String json = gson.toJson(payload);
		
		try {

			String response = accountOpeningService.accountCreation1(json);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	
	@PostMapping(value = "/bvnvalidation")
	public ResponseEntity<Object> bvnValidation(@RequestBody Map<String, Object> payload) {
		logger.debug("=================================");
		logger.debug("BVN Validation Request");
		logger.debug("=================================");
		Gson gson = new Gson();
		String json = gson.toJson(payload);
		
		try {

			String response = accountOpeningService.bvnValidation(json);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	
	@PostMapping(value = "/validationbvn")
	public ResponseEntity<Object> validationBVN(@RequestBody Map<String, Object> payload) {
		logger.debug("=================================");
		logger.debug("BVN Validation to Generate OTP Request");
		logger.debug("=================================");
		Gson gson = new Gson();
		String json = gson.toJson(payload);
		
		try {

			String response = accountOpeningService.validationBVN(json);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel for BVN Validation and OTP Generation: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(value = "/bvnvalidation1")
	public ResponseEntity<Object> bvnValidation1(@RequestBody Map<String, Object> payload) {
		logger.debug("=================================");
		logger.debug("BVN Validation Request");
		logger.debug("=================================");
		Gson gson = new Gson();
		String json = gson.toJson(payload);
		
		try {

			String response = accountOpeningService.bvnValidation1(json);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@GetMapping(value = "/AccountLienEnquiry/{accountNo}")
	public ResponseEntity<Object> accountLienEnquiry(@PathVariable("accountNo") String accountNo) {
		logger.debug("=================================");
		logger.debug("Account Lien Enquiry Request");
		logger.debug("=================================");
		
		logger.debug("AccountNo >>> " + accountNo);

		try {

			String response = accountOpeningService.accountLienEnquiry(accountNo);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(value = "/accountRestriction")
	public ResponseEntity<Object> staffRestriction(@RequestBody AccountRestrictionRequest request) {
		logger.debug("=================================");
		logger.debug("Account Access Restriction Request");
		logger.debug("=================================");
		
		try {

			String response = accountOpeningService.staffRestriction(request);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	
	@PostMapping(value = "/accountReactivation")
	public ResponseEntity<Object> accountReactivation(@RequestBody AccountReactivationRequest request) {
		logger.debug("=================================");
		logger.debug("Account Reactivation Request");
		logger.debug("=================================");
		
		try {

			String response = accountOpeningService.accountReactivation(request);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
	
	@GetMapping(value = "/AccountDetails")
	public ResponseEntity<AccountDetailsResponse> accountDetails(@RequestParam String ReferralCode, @RequestParam String StartDate, @RequestParam String EndDate) {
		logger.debug("====================================");
		logger.debug("Account Details Request");
		logger.debug("====================================");
		
		try {
			AccountDetailsResponse response = accountOpeningService.getAccountDetails(ReferralCode, StartDate, EndDate);
			logger.debug("Response to the channel for Account Details: " + response);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {

			AccountDetailsResponse response = new AccountDetailsResponse("99", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

	}
	
	@GetMapping(value = "/salaryAccount")
	public ResponseEntity<SalaryAccountResponse> salaryAccount(@RequestParam String EmailId) {
		logger.debug("====================================");
		logger.debug("Account Details Request");
		logger.debug("====================================");
		
		try {
			SalaryAccountResponse response = accountOpeningService.getSalaryAccount(EmailId);
			logger.debug("Response to the channel for Account Details: " + response);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {

			SalaryAccountResponse response = new SalaryAccountResponse("99", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(value = "/createUserIdCheck")
	public ResponseEntity<Object> createIdentityxUsers(@RequestBody Map<String, Object> payload) {
		logger.debug("=================================");
		logger.debug("Create IdentityX Users Request");
		logger.debug("=================================");
		Gson gson = new Gson();
		String json = gson.toJson(payload);
		
		try {

			String response = accountOpeningService.createIdentityxUsers(json);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			logger.debug("Response to the channel: " + response);
			return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {

			return ResponseHandler.generateResponse("99", e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

}
