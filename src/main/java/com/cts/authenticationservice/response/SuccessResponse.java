package com.cts.authenticationservice.response;

import lombok.Data;

@Data
public class SuccessResponse {
	private String message;
	private String token;
}
