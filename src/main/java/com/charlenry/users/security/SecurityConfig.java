package com.charlenry.users.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Cette une classe utilisée pour configurer Spring Security dans l'application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // Permet d'ajouter des filtres directement dans les restControllers ou au niveau des méthodes de service.
public class SecurityConfig {
	@Autowired
	AuthenticationManager authMgr;
	
	/** 
	 * Cette méthode est utilisée pour configurer la chaîne de filtres de sécurité de Spring Security.
	 * Une chaîne de filtres est une liste ordonnée de filtres qui sont appliqués à chaque requête entrante. 
	 * Chaque filtre dans la chaîne peut décider de traiter la requête et de générer une réponse, 
	 * ou de passer la requête au filtre suivant dans la chaîne.
	 * 
	 * @param http - Objet de la classe HttpSecurity fournie par Spring Security et est utilisé pour configurer les détails de sécurité au niveau des requêtes HTTP.
	 * @return SecurityFilterChain - C'est une interface fournie par Spring Security qui représente une chaîne de filtres de sécurité.
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.csrf( csrf -> csrf.disable())
		.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration cors = new CorsConfiguration();
				cors.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
				cors.setAllowedMethods(Collections.singletonList("*"));
				cors.setAllowedHeaders(Collections.singletonList("*"));
				cors.setExposedHeaders(Collections.singletonList("Authorization"));
				return cors;
			}
		}))
		.authorizeHttpRequests(requests -> requests
			.requestMatchers(HttpMethod.POST, "/login", "/register/**").permitAll()
			.requestMatchers(HttpMethod.GET, "/all").hasAuthority("ADMIN")
			.anyRequest().authenticated()
		)
		.addFilterBefore(new JWTAuthenticationFilter (authMgr), UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
