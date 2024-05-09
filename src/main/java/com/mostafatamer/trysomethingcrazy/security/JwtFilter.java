package com.mostafatamer.trysomethingcrazy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter for JWT-based authentication in Spring Boot applications.
 * This filter extracts JWT tokens from incoming requests, validates them,
 * and sets up the authentication context for authenticated users.
 */
@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // Dependency injections: UserDetailsService and JwtService
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    // Overriding doFilterInternal method to implement JWT-based authentication logic
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extracting JWT token from request header
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // If no token is found in the header, continue with filter chain
        if (header == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extracting username from JWT token
        String username = jwtService.extractUsername(header);

        // Checking if user is authenticated and no existing authentication in SecurityContextHolder
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Loading UserDetails from UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validating JWT token
            if (jwtService.isTokenValid(header, userDetails)) {
                // Creating UsernamePasswordAuthenticationToken with UserDetails and authorities
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Setting authentication details
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Setting authentication in SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            // Proceeding with the filter chain after authentication
            filterChain.doFilter(request, response);
        }
    }
}

