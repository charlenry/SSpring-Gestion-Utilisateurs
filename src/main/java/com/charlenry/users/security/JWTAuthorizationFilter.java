package com.charlenry.users.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Récupère le token JWT et décode les informations et gère l'autorisation JWT (JSON Web Token).
 * Cette classe est une extension de la classe OncePerRequestFilter qui est une classe intégrée de Spring Framework.
 * La classe OncePerRequestFilter est une implémentation spécifique d'un filtre qui est utilisé pour garantir qu'une certaine logique est exécutée une seule fois par requête, même si la requête est redirigée à plusieurs reprises à l'intérieur du serveur.
 * En résumé, laclasse JWTAuthorizationFilter est utilisée pour vérifier si un JWT valide est présent dans les en-têtes de requête et, si c'est le cas, pour extraire les détails de l'utilisateur à partir du JWT et les utiliser pour autoriser l'accès à certaines ressources.
 */
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	/** 
	 * Cette méhode permet de vérifier le JWT dans la requête, extrait les détails de l'utilisateur à partir du JWT, et autorise ou refuse l'accès à la ressource demandée en fonction de ces détails.
	 * Cette méthode est un remplacement de la méthode doFilterInternal de la classe parente OncePerRequestFilter.
	 * @param request Requête HTTP
	 * @param response Réponse HTTP
	 * @param filterChain - Chaîne de filtres qui peut être appliquée à la requête et à la réponse.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain) throws ServletException, IOException {
		
		String jwt = request.getHeader("Authorization");
		
		// System.out.println("JWT = " + jwt);
		
		if (jwt == null || !jwt.startsWith(SecParams.PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecParams.SECRET)).build();
		
		// enlever le préfixe Bearer du jwt
		jwt = jwt.substring(SecParams.PREFIX.length()); // 7 caractères dans "Bearer "
		
		DecodedJWT decodedJWT = verifier.verify(jwt);
		
		String username = decodedJWT.getSubject();
		
		List<String> roles = decodedJWT.getClaims().get("roles").asList(String.class);
		
		Collection <GrantedAuthority> authorities = new	ArrayList<GrantedAuthority>();
		
		for (String role : roles) authorities.add(new SimpleGrantedAuthority(role));
		
		UsernamePasswordAuthenticationToken user = 
				new UsernamePasswordAuthenticationToken(username, null, authorities);
		
		SecurityContextHolder.getContext().setAuthentication(user);
		
		filterChain.doFilter(request, response);
	}

}
