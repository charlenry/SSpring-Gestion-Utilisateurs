package com.charlenry.users.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cette classe est une entité qui crée la table "role" dans la BDD.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
/**
 * The `public class Role` declaration is defining a Java class named `Role`. 
 * This class represents an entity that maps to a database table named "role". 
 * It contains fields representing columns in the database table, such as `role_id` and `role`. 
 * The class is annotated with `@Entity` to indicate that it is a JPA entity, 
 * and it uses Lombok annotations `@Data`, `@NoArgsConstructor`, and `@AllArgsConstructor` 
 * to generate getters, setters, constructors, and other boilerplate code automatically.
 * 
 */
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long role_id;
	private String role;
}
