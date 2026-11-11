
package com.tripmind.backend.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    // Constructor: Load JWT secret and expiration
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        this.secretKey =
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        this.expirationMs = expirationMs;
    }

    // Generate JWT token
    public String generateToken(Long userId, String email) {

        Date now = new Date();

        Date expiration =
                new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // Extract user ID from JWT
    public Long extractUserId(String token) {

        Claims claims = extractAllClaims(token);

        return Long.valueOf(claims.getSubject());
    }

    // Verify signature and extract claims
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Validate JWT
    public boolean isTokenValid(String token) {

        try {
            extractAllClaims(token);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
