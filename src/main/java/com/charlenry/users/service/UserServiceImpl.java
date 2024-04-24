package com.charlenry.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.charlenry.users.entities.Role;
import com.charlenry.users.entities.User;
import com.charlenry.users.repos.RoleRepository;
import com.charlenry.users.repos.UserRepository;
import com.charlenry.users.service.exceptions.EmailAlreadyExistsException;
import com.charlenry.users.service.register.RegistrationRequest;

import jakarta.transaction.Transactional;


@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User saveUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public Role addRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public User addRoleToUser(String username, String rolename) {
		User usr = userRepository.findByUsername(username);
		Role role = roleRepository.findByRole(rolename);
		
		usr.getRoles().add(role);
		
		//userRepository.save(usr);  // pas nécessaire avec l'annotation @Transactional
		
		return usr;
		
	}

	@Override
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User registerUser(RegistrationRequest request) {
		// Recherche un utilisateur par son email
		Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
		
		// Si l'utilisateur est déjà présent dans la BDD, renvoie une exception
		if(optionalUser.isPresent()) throw new EmailAlreadyExistsException("Email déjà existant !");
		
		// Récupère les données du nouvel utilisateur
		User newUser = new User();
		newUser.setUsername(request.getUsername());
		newUser.setEmail(request.getEmail());
		newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
		newUser.setEnabled(false); // Le compte de l'utilisateur est désactivé par défaut
		
		// Sauvegarde le nouvel utilisateur dans la table users
		userRepository.save(newUser);
		
		// Ajoute le role USER par défaut au nouvel utilisateur
		Role role = roleRepository.findByRole("USER");
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		newUser.setRoles(roles);		
		
		return userRepository.save(newUser);
	}

}
