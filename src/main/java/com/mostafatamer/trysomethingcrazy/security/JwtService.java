package com.mostafatamer.trysomethingcrazy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    // JWT secret key and expiration time (in milliseconds)
    String secretKey = "mostafaf94302q8yur098ru4q02323qr54qtamermostafa";
    String jwtExpiration = "7776000000";

    // Method to generate JWT token from UserDetails
    public String generateToken(UserDetails userDetails) {
        // Extracting authorities from UserDetails and mapping them to a list of strings
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Building JWT token with claims, subject, issued at, expiration, and signing key
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(System.currentTimeMillis() + Long.parseLong(jwtExpiration))
                ).claim("authorities", authorities)
                .signWith(getSignedKey())
                .compact();
    }

    // Method to validate JWT token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // Extracting claims from token
        Claims claims = getClaims(token);
        // Extracting username from claims
        String username = claims.getSubject();
        // Checking if username matches UserDetails username and token is not expired
        return username.equals(userDetails.getUsername()) && !isTokenExpired(claims);
    }

    // Method to extract claims from JWT token
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignedKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Method to check if token is expired
    boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // Method to get the signing key for JWT
    private Key getSignedKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
    }

    // Method to extract username from JWT token
    public String extractUsername(String jwt) {
        return getClaims(jwt).getSubject();
    }
}
