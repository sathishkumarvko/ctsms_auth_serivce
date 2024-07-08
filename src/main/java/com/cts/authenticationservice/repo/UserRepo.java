package com.cts.authenticationservice.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.authenticationservice.dto.User;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

	Boolean existsByEmail(String email);
	Boolean existsByVerificationToken(String token);
	Optional<User> findByVerificationToken(String token);
	Optional<User> findByEmail(String email);
}
