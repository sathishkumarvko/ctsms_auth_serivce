package com.cts.authenticationservice.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cts.authenticationservice.dto.User;
import com.cts.authenticationservice.dto.UserRequest;
import com.cts.authenticationservice.exception.AuthenticationServiceException;
import com.cts.authenticationservice.repo.UserRepo;
import com.cts.authenticationservice.response.ErrorResponse;
import com.cts.authenticationservice.response.SuccessResponse;
import com.cts.authenticationservice.utils.JwtUtil;

@Service
public class UserAuthServiceImpl implements UserAuthSerivce {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private EmailService emailservice;
	
	private static final long TOKEN_EXPIRATION = 60;
	
	@Override
	public ResponseEntity<?> registerUser(UserRequest userInfo) throws AuthenticationServiceException {
		SuccessResponse response = new SuccessResponse();
		try {
			var emailAlreadyExists = userRepo.existsByEmail(userInfo.getEmail());
			if(!emailAlreadyExists) {
				User user = new User();
				user.setEmail(userInfo.getEmail());
				user.setPassword(pwdEncoder(userInfo.getPassword()));
				user.setVerificationToken(generateToken(userInfo.getEmail()));
				user.setCreatedTimeStamp(LocalDateTime.now());
				var userRegisteredInfo = userRepo.save(user);
				if(userRegisteredInfo != null) {
					sendVerificationMail(user);
					response.setMessage("Registration Successfull. Verification email sent");
				} else {
					throw new AuthenticationServiceException("Registration Failed. Please try again later");
				}
			} else {
				ErrorResponse error = new ErrorResponse();
				error.setError("Email already exists");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthenticationServiceException("Registration Failed. Please try again later");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	private void sendVerificationMail(User user) {
		String content = "Please click the below link to verify your account: </br> " +  " http://localhost:8081/api/verify?token=" + user.getVerificationToken();
		emailservice.sendEmail(user.getEmail(), "Verification Email", content);
	}

	private String generateToken(String email) {
		return UUID.randomUUID().toString();
	}

	private String pwdEncoder(String password) {
		BCryptPasswordEncoder encryptor = new BCryptPasswordEncoder();
		return encryptor.encode(password);
	}

	@Override
	public String authenticaUser(User userInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> verifyUser(String token) throws AuthenticationServiceException {
		SuccessResponse response = new SuccessResponse();
		try {
			Optional<User> userInfo = userRepo.findByVerificationToken(token);
			if(userInfo.isPresent() && !isTokenExpire(userInfo.get().getCreatedTimeStamp())) {
				userInfo.get().setActive(true);
				userRepo.save(userInfo.get());
				response.setMessage("Verification Successfull.");
			} else {
				ErrorResponse error = new ErrorResponse();
				error.setError("Invalid/Expired token. Verfication Failed");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
			}
		} catch (Exception e) {
			throw new AuthenticationServiceException("Verfication Failed. Please try again later");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	private boolean isTokenExpire(LocalDateTime tokenDuration) {
		var isExpired = tokenDuration.plusMinutes(TOKEN_EXPIRATION).isBefore(LocalDateTime.now());
		return isExpired;
	}

	@Override
	public ResponseEntity<?> loginUser(UserRequest userInfo) throws AuthenticationServiceException {
		SuccessResponse response = new SuccessResponse();
		try {
			Optional<User> userDetails = userRepo.findByEmail(userInfo.getEmail());
			if(userDetails.isPresent()) {
				User userDetail = userDetails.get();
				if(isPwdMatches(userInfo.getPassword(), userDetail.getPassword())) {
					LocalDateTime ldt = LocalDateTime.now().plusMinutes(15);
					response.setToken(new JwtUtil(userDetail.getEmail(), userDetail.getPassword(), ldt.toEpochSecond(ZoneOffset.UTC)).toString());
					response.setMessage("Login Successfull.");
				} else {
					ErrorResponse error = new ErrorResponse();
					error.setError("Login Failed, Invalid Pasword. Please try again later");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);	
				}
			} else {
				ErrorResponse error = new ErrorResponse();
				error.setError("Login Failed, user doesnt exists. Please try again later");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthenticationServiceException("Login Failed. Please try again later");
		}		
		return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	private boolean isPwdMatches(String password, String encodedPwd) {
		BCryptPasswordEncoder encryptor = new BCryptPasswordEncoder();
		return encryptor.matches(password, encodedPwd);
	}

}
