package com.charlenry.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import com.charlenry.users.entities.Role;
//import com.charlenry.users.entities.User;
import com.charlenry.users.service.UserService;
//import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class UsersMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersMicroserviceApplication.class, args);
	}
	
	@Autowired
	UserService userService;

	// Crée les utilisateurs et les rôles au démarrage de l'application
	// @PostConstruct veut dire que init_users s'exécute après le constructeur de cette classe
	/*
	@PostConstruct
	void init_users() {
		// ajouter les rôles
		userService.addRole(new Role(null, "ADMIN"));
		userService.addRole(new Role(null, "USER"));
		// ajouter les users
		userService.saveUser(new User(null, "admin", "123", true, null));
		userService.saveUser(new User(null, "charles", "123", true, null));
		userService.saveUser(new User(null, "henri", "123", true, null));
		// ajouter les rôles aux users
		userService.addRoleToUser("admin", "ADMIN");
		userService.addRoleToUser("admin", "USER");
		userService.addRoleToUser("charles", "USER");
		userService.addRoleToUser("henri", "USER");
	}
	*/
	

}
