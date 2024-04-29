package com.charlenry.users.service.register;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This line of code is defining an interface named `VerificationTokenRepository` that extends 
 * the `JpaRepository` interface. By extending `JpaRepository<VerificationToken, Long>`, 
 * the `VerificationTokenRepository` interface inherits methods for basic CRUD operations 
 * (Create, Read, Update, Delete) on entities of type `VerificationToken` with an identifier 
 * of type `Long`. This allows the `VerificationTokenRepository` interface to interact 
 * with a database using Spring Data JPA.
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
	VerificationToken findByToken(String token);
}