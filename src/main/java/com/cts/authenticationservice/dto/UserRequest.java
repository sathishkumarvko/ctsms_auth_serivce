package com.cts.authenticationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

	@NotNull(message = "Email cannot be null")
	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Email must be in valid format. (e.g : user@example.com)")
	private String email;
	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 8, max = 250, message = "Password must be atleast 8 char long")
	//@Pattern(regexp = " ^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[@#$%^&*!])$", message = "Password must contais 8 char long and contain mix of letter,numbers and special char")
	private String password;
}
