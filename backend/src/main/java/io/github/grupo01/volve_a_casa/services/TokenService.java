package io.github.grupo01.volve_a_casa.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {
    private final static SecretKey key = Jwts.SIG.HS256.key().build();


    public String generateToken(Long id) {
        Date expirationDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

        return Jwts.builder()
                .subject(String.valueOf(id))
                .signWith(key)
                .expiration(expirationDate)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        String prefix = "Bearer";
        if (token.startsWith(prefix)) {
            token = token.substring(prefix.length()).trim();
        }
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            // Option 1: return null to indicate invalid token
            // return null;
            // Option 2: throw a runtime exception with a clear message
            throw new IllegalArgumentException("Token subject is not a valid user ID", e);
        }
}
