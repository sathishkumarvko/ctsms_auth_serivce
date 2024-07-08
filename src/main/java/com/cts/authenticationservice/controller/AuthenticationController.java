package com.cts.authenticationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.authenticationservice.dto.UserRequest;
import com.cts.authenticationservice.exception.AuthenticationServiceException;
import com.cts.authenticationservice.service.UserAuthSerivce;


@RestController
@RequestMapping("/api")
public class AuthenticationController {

	@Autowired
	private UserAuthSerivce authService;

	@PostMapping("/register")
	public ResponseEntity<?> userRegistrations(@RequestBody UserRequest userInfo) throws AuthenticationServiceException{
		try {
			return authService.registerUser(userInfo);
		} catch (AuthenticationServiceException e) { 
			throw e;
		} catch (Exception e) { 
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Input");
		}
	}
	
	@PostMapping("/verify")
	public ResponseEntity<?> verfyUser(@RequestParam("token") String token) throws AuthenticationServiceException{
		try {
			return authService.verifyUser(token);
		} catch (Exception e) { 
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Input");
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserRequest userInfo) throws AuthenticationServiceException{
		try {
			return authService.loginUser(userInfo);
		} catch (Exception e) { 
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Input");
		}
	}

}
