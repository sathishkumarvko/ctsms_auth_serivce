package com.cts.authenticationservice.exception;

public class UserNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7186666882196170817L;

	public UserNotFoundException(String msg) {
		super(msg);
	}

	@Override
	public String toString() {
		return super.getMessage();
	}
}
