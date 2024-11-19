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
    //used to manage and validate JWT tokens
    private JwtService jwtService;

    // Constructor injecting the JwtService service to interact with JWT tokens
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Extraction and validation of the JWT token
        extractToken(request)// Get the token from the Authorization header
                .flatMap(this::getValidUsername)// Validates the token and extracts the username if valid
                .filter(username -> SecurityContextHolder.getContext().getAuthentication() == null) // Checks that there is no active authentication in the context
                .ifPresent(username -> setAuthentication(username, request));

        filterChain.doFilter(request, response);
    }

    // Method to extract the token from the Authorization header
    private Optional<String> extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }
        return Optional.empty();
    }

    // Method to validate the token and get the username if it is valid
    private Optional<String> getValidUsername(String token) {
        String username = jwtService.getUsernameFromToken(token);
        if (username != null && jwtService.validateToken(token, username)) {
            return Optional.of(username);
        }
        return Optional.empty();
    }

    // Method to authenticate user in Spring security context
    private void setAuthentication(String username, HttpServletRequest request) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username, null, null
                )
        );
    }


}
