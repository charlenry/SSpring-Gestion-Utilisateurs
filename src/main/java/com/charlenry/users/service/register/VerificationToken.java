package com.charlenry.users.service.register;

import java.util.Calendar;
import java.util.Date;

import com.charlenry.users.entities.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
/**
 * The `VerificationToken` class is a Java entity class that represents a table named "verification_token" 
 * in the database. It contains fields such as `id`, `token`, `expirationTime`, and a reference to 
 * a `User` entity. The class is annotated with `@Entity` to mark it as a JPA entity, and it uses Lombok annotations 
 * like `@Data` and `@NoArgsConstructor` for generating getters, setters, constructors, and other boilerplate code.
 */
public class VerificationToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String token;
	private Date expirationTime;
	private static final int EXPIRATION_TIME = 15;
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

  /**
	* The `public VerificationToken(String token, User user)` constructor in the `VerificationToken` 
	* class is a parameterized constructor that initializes a new `VerificationToken` object with 
	* the provided `token` and `user` values. It sets the `token` and `user` fields of 
	* the `VerificationToken` object based on the values passed as arguments to the constructor. 
	* Additionally, it calculates and sets the `expirationTime` field by calling the `getTokenExpirationTime()` 
	* method, which determines the expiration time of the token based on the current time plus a predefined 
	* expiration period.
	*/
	public VerificationToken(String token, User user) {
		super();
		this.token = token;
		this.user = user;
		this.expirationTime = this.getTokenExpirationTime();
	}

	public VerificationToken(String token) {
		super();
		this.token = token;
		this.expirationTime = this.getTokenExpirationTime();
	}

	public Date getTokenExpirationTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
		return new Date(calendar.getTime().getTime());
	}
}
