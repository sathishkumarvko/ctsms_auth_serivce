package com.cts.authenticationservice.controlleradvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cts.authenticationservice.exception.AuthenticationServiceException;
import com.cts.authenticationservice.exception.UserNotFoundException;
import com.cts.authenticationservice.response.ErrorResponse;

@ControllerAdvice
public class UserControllerAdvice {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e){
		ErrorResponse error = new ErrorResponse();
		error.setError(e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(AuthenticationServiceException.class)
	public ResponseEntity<?> handleAuthenticationServiceException(AuthenticationServiceException e){
		ErrorResponse error = new ErrorResponse();
		error.setError(e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
