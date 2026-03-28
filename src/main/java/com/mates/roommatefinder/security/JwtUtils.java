package com.mates.roommatefinder.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.issuer:roommatefinder}")
    private String jwtIssuer;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token for a user with role
     * @param userId the user ID to encode in the token
     * @param role the user's role
     * @return JWT token string
     */
    public String generateJwtToken(Long userId, UserRole role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role.toString())
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate JWT token for a user (defaults to USER role)
     * @param userId the user ID to encode in the token
     * @return JWT token string
     */
    public String generateJwtToken(Long userId) {
        return generateJwtToken(userId, UserRole.USER);
    }

    /**
     * Extract user ID from JWT token
     * @param token the JWT token
     * @return the user ID encoded in the token
     */
    public Long getUserIdFromJwt(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.parseLong(subject);
    }

    /**
     * Extract role from JWT token
     * @param token the JWT token
     * @return the user's role
     */
    public UserRole getRoleFromJwt(String token) {
        String roleStr = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
        
        try {
            return UserRole.valueOf(roleStr);
        } catch (Exception e) {
            return UserRole.USER;
        }
    }

    /**
     * Validate JWT token
     * @param authToken the JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}