package com.chatop.api.config;

import com.chatop.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //utilisé pour gérer et valider les tokens JWT
    private JwtService jwtService;

    // Constructeur injectant le service JwtService pour interagir avec les tokens JWT
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Extraction et validation du token JWT
        extractToken(request)// Récupère le token de l'en-tête Authorization
                .flatMap(this::getValidUsername)// Valide le token et extrait le nom d'utilisateur s'il est valide
                .filter(username -> SecurityContextHolder.getContext().getAuthentication() == null) // Vérifie qu'il n'y a pas d'authentification active dans le contexte
                .ifPresent(username -> setAuthentication(username, request));

        filterChain.doFilter(request, response);
    }

    // Méthode pour extraire le token depuis l'en-tête Authorization
    private Optional<String> extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }
        return Optional.empty();
    }

    // Méthode pour valider le token et obtenir le nom d'utilisateur s'il est valide
    private Optional<String> getValidUsername(String token) {
        String username = jwtService.getUsernameFromToken(token);
        if (username != null && jwtService.validateToken(token, username)) {
            return Optional.of(username);
        }
        return Optional.empty();
    }

    // Méthode pour authentifier l'utilisateur dans le contexte de sécurité de Spring
    private void setAuthentication(String username, HttpServletRequest request) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username, null, null
                )
        );
    }


}
