package com.charlenry.users.entities;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;  // Génère les getters et les setters
import lombok.Data;                // Génère les getters et les setters
import lombok.NoArgsConstructor;   // Génère les getters et les setters

/**
 * Cette classe est une entité qui crée la table "user" dans la BDD.
 * Crée également la table "user_role" dans la BDD pour la contrainte d'intégrité 
 * avec les colonnes "user_id" de la table "user" et "role_id" de la table "role".
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
/**
 * The `public class User` is defining a Java class named `User` which represents an entity in the application. 
 * This class is annotated with `@Entity`, indicating that instances of this class will be mapped to a database table. 
 * The class contains fields representing attributes of a user entity such as `user_id`, `username`, `password`, 
 * `enabled`, `email`, and `roles`.
 * 
 */
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long user_id;
	@Column(unique = true)
	private String username;
	private String password;
	private Boolean enabled;
	private String email;
	
	// Un User peut avoir plusieurs rôles et un rôle peut appartenir à plusieurs Users
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
 /**
	* The `@JoinTable` annotation in the `User` entity class is used to define the mapping for a Many-to-Many relationship between the `User` entity and the `Role` entity.
	* 
	*/
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;
}
