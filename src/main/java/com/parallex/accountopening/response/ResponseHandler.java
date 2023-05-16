package com.parallex.accountopening.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public static ResponseEntity<Object> generateResponse(String statusCode, String message, HttpStatus status){ //, Object responseObj) { 
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("statusCode", statusCode);
		map.put("statusMessage", message);
		return new ResponseEntity<Object>(map, status);

	}
	
	public ResponseHandler(String message) {
        super(message);
    }

}
