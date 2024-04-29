package com.charlenry.users.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.charlenry.users.entities.Role;
import com.charlenry.users.entities.User;
import com.charlenry.users.repos.RoleRepository;
import com.charlenry.users.repos.UserRepository;
import com.charlenry.users.service.exceptions.EmailAlreadyExistsException;
import com.charlenry.users.service.exceptions.ExpiredTokenException;
import com.charlenry.users.service.exceptions.InvalidTokenException;
import com.charlenry.users.service.register.RegistrationRequest;
import com.charlenry.users.service.register.VerificationToken;
import com.charlenry.users.service.register.VerificationTokenRepository;
import com.charlenry.users.util.EmailSender;

import jakarta.transaction.Transactional;

/**
 * The `@Transactional` annotation in Spring is used to indicate that a method
 * or class should be wrapped in a transaction. When a method or class is
 * annotated with `@Transactional`, Spring will manage the transaction
 * boundaries for that method or class. This means that if an exception occurs
 * during the execution of the annotated method, the transaction will be rolled
 * back, ensuring data consistency.
 */
@Transactional
/**
 * The `@Service` annotation in Spring is used to indicate that a class is a
 * service component in the business layer. It serves as a specialization of the
 * `@Component` annotation and is typically used to define a service bean that
 * contains business logic. When Spring scans the components in your
 * application, classes annotated with `@Service` are identified as service
 * beans and can be automatically wired into other components using dependency
 * injection.
 */
@Service
/**
 * The `public class UserServiceImpl` is implementing the `UserService`
 * interface. By doing this, the `UserServiceImpl` class is providing concrete
 * implementations for the methods defined in the `UserService` interface. This
 * allows the `UserServiceImpl` class to define its own logic for methods like
 * `saveUser`, `findUserByUsername`, `addRole`, `addRoleToUser`, `findAllUsers`,
 * `registerUser`, `sendEmailUser`, etc., which are declared in the
 * `UserService` interface.
 */
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	VerificationTokenRepository verificationTokenRepo;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	EmailSender emailSender;

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

		// userRepository.save(usr); // pas nécessaire avec l'annotation @Transactional

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
		if (optionalUser.isPresent())
			throw new EmailAlreadyExistsException("Email déjà existant !");

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

		// Génère le code secret
		String code = this.generateCode();

		// Récupère le code secret et les données de l'utilisateur,
		// puis sauvegarde le tout dans la table "verification_token".
		VerificationToken token = new VerificationToken(code, newUser);
		verificationTokenRepo.save(token);

		// Envoie le code secret par email à l'utilisateur
		sendEmailUser(newUser, token.getToken());

		return userRepository.save(newUser);
	}

	private String generateCode() {
		Random random = new Random();
		Integer code = 100000 + random.nextInt(900000);
		return code.toString();
	}

	@Override
	public void sendEmailUser(User u, String code) {
		String emailBody = "Bonjour " + "<h1>" + u.getUsername() + "</h1>" + 
		" Votre code de validation est " + "<h1>"+ code + "</h1>";
		emailSender.sendEmail(u.getEmail(), emailBody);
	}

	@Override
	public User validateToken(String code) {
		VerificationToken token = verificationTokenRepo.findByToken(code);
		if (token == null) {
			throw new InvalidTokenException("Invalid Token");
		}
		
		User user = token.getUser();
		Calendar calendar = Calendar.getInstance();
		
		// Si l'heure d'expiration est inférieure ou égale à l'heure actuelle
		if ((token.getExpirationTime().getTime() <= calendar.getTime().getTime())) {
			verificationTokenRepo.delete(token);
			throw new ExpiredTokenException("Expired Token");
		}
		
		user.setEnabled(true);
		userRepository.save(user);
		return user;
	}
}
