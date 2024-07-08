package com.cts.authenticationservice.service;

import org.springframework.http.ResponseEntity;

import com.cts.authenticationservice.dto.User;
import com.cts.authenticationservice.dto.UserRequest;
import com.cts.authenticationservice.exception.AuthenticationServiceException;

public interface UserAuthSerivce {
 
	public ResponseEntity<?> registerUser(UserRequest userInfo) throws AuthenticationServiceException;
	public ResponseEntity<?> loginUser(UserRequest userInfo) throws AuthenticationServiceException;
	public String authenticaUser(User userInfo);
	public ResponseEntity<?> verifyUser(String token) throws AuthenticationServiceException;
	
}
