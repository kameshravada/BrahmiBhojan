package com.brahmibhojan.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretBase64;
    private final long accessExpirationSeconds;

    public JwtService(
            @Value("${security.jwt.secret-base64}") String secretBase64,
            @Value("${security.jwt.access-expiration-seconds:${security.jwt.expiration-seconds:3600}}") long accessExpirationSeconds
    ) {
        this.secretBase64 = secretBase64;
        this.accessExpirationSeconds = accessExpirationSeconds;
    }

    public String generateToken(String subject, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        Instant now = Instant.now();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessExpirationSeconds)))
                .signWith(signingKey())
                .compact();
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String subject = extractSubject(token);
        return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public long getExpirationSeconds() {
        return accessExpirationSeconds;
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
        return resolver.apply(claims);
    }

    private SecretKey signingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

