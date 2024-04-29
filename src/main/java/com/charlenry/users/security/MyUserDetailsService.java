package com.charlenry.users.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.charlenry.users.entities.User;
import com.charlenry.users.service.UserService;

/**
 * C'est une classe qui est utilisée pour personnaliser la façon dont Spring Security charge les détails d'un utilisateur. Cette classe doit fournir une implémentation pour la méthode loadUserByUsername de l'interface UserDetailsService.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserService userService;
	
	
	/** 
	 * Cette méthode est utilisée pour charger les détails d'un utilisateur via son nom d'utilisateur. Elle est appelée par Spring Security lors de l'authentification d'un utilisateur, par exemple lors de la soumission d'un formulaire de connexion.
	 * @param username - nom de l'utilisateur
	 * @return UserDetails - C'est une interface fournie par Spring Security qui contient des informations sur un utilisateur, comme son nom d'utilisateur, son mot de passe, ses autorités (c'est-à-dire ses rôles ou privilèges), et si l'utilisateur est activé, compte non expiré, etc.
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.findUserByUsername(username);
		
		if (user == null)
			throw new UsernameNotFoundException("Utilisateur introuvable !");
		
		List<GrantedAuthority> auths = new ArrayList<>();
		
		user.getRoles().forEach(role -> {
			GrantedAuthority auhority = new SimpleGrantedAuthority(role.getRole());
			auths.add(auhority);
		});
		
		return new org.springframework.security.core
				.userdetails.User(user.getUsername(),user.getPassword(),user.getEnabled(),true,true,true,auths);
	}

}
