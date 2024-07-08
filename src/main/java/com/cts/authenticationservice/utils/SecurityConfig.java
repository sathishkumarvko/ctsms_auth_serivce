package com.cts.authenticationservice.utils;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@SuppressWarnings("deprecation")
	@Bean
	SecurityFilterChain webSecurityCustomizer(HttpSecurity http) throws Exception {
		http
		.csrf(c -> c.disable())
		.authorizeRequests(e -> e
				.requestMatchers("/api/**").permitAll()
				.anyRequest().authenticated()
				)
		.formLogin(withDefaults());
		return http.build();
	}

}
