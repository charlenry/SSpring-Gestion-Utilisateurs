package com.charlenry.users.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.charlenry.users.entities.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Cette classe est une extension de la classe UsernamePasswordAuthenticationFilter 
 * qui est une classe intégrée de Spring Security.
 * Dans ce cas, JWTAuthenticationFilter est une sous-classe de UsernamePasswordAuthenticationFilter.
 * Cela signifie que JWTAuthenticationFilter hérite de toutes les méthodes et variables publiques et protégées de UsernamePasswordAuthenticationFilter.
 * La classe UsernamePasswordAuthenticationFilter est une implémentation spécifique d'un filtre d'authentification qui est utilisé pour traiter les demandes d'authentification par nom d'utilisateur et mot de passe.
 * En étendant cette classe, JWTAuthenticationFilter peut ajouter ou modifier le comportement de cette classe pour gérer l'authentification JWT (JSON Web Token).
 * En résumé, cette classe est utilisée pour personnaliser le processus d'authentification dans une application Spring Security en utilisant des JWT.
 */
 public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private AuthenticationManager authenticationManager;

	/**
	 * Définit le constructeur de la classe JWTAuthenticationFilter
	 * 
	 * @param authenticationManager - Objet utilisé pour gérer l'authentification
	 */
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	
	/** 
	 * Tente d'authentifier une requête HTTP dans le cadre de Spring Security.
	 * Si l'authenficication réussit, retourne un objet Authentication. 
	 * Sinon lance AuthenticationException.
	 * 
	 * @param request - Requête HTTP
	 * @param response - Réponse HTTP
	 * @return Authentication - Contient les informations d'authentification d'un utilisateur.
	 * @throws AuthenticationException
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		User user =null;
		
		try {
			user = new ObjectMapper().readValue(request.getInputStream(), User.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
	}

	
	/** 
	 * Crée le token JWT en cas de succès d'authentification et le met dans la réponse HTTP.
	 * 
	 * @param request - Requête HTTP
	 * @param response - Réponse HTTP
	 * @param filterChain - Chaîne de filtres qui peut être appliquée à la requête et à la réponse.
	 * @param authResult - Objet contenant les détails de l'authentification réussie.
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain, Authentication authResult) throws IOException, ServletException {
		
		org.springframework.security.core.userdetails.User springUser = 
				(org.springframework.security.core.userdetails.User) authResult.getPrincipal();
		
		List<String> roles = new ArrayList<>();
		
		springUser.getAuthorities().forEach(auth -> {
			roles.add(auth.getAuthority());
		});
		
		String jwt = JWT.create().withSubject(springUser.getUsername())
				.withArrayClaim("roles", roles.toArray(new String[roles.size()]))
				.withExpiresAt(new Date(System.currentTimeMillis() + SecParams.EXP_TIME))
				.sign(Algorithm.HMAC256(SecParams.SECRET));
		response.addHeader("Authorization", jwt);
				
	}
	
	
	/**
	 * The `unsuccessfulAuthentication` method in the `JWTAuthenticationFilter` class is an overridden method from 
	 * the superclass `UsernamePasswordAuthenticationFilter`. This method is called when the authentication process 
	 * fails for a user attempting to log in.
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		if (failed instanceof DisabledException) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			Map<String, Object> data = new HashMap<>();
			data.put("errorCause", "disabled");
			data.put("message", "L'utilisateur est désactivé !");
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(data);
			PrintWriter writer = response.getWriter();
			writer.println(json);
			writer.flush();
		} else {
			super.unsuccessfulAuthentication(request, response, failed);
		}
	}
	
}