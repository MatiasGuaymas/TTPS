package io.github.grupo01.volve_a_casa.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        // Arrange
        Long userId = 123L;

        // Act
        String token = tokenService.generateToken(userId);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length); // JWT format: header.payload.signature
    }

    @Test
    void generateToken_shouldWorkWithDifferentUserIds() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 999L;
        Long userId3 = 12345L;

        // Act
        String token1 = tokenService.generateToken(userId1);
        String token2 = tokenService.generateToken(userId2);
        String token3 = tokenService.generateToken(userId3);

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token3);
        assertNotEquals(token1, token2);
        assertNotEquals(token2, token3);
        assertNotEquals(token1, token3);
    }

    @Test
    void getUserIdFromToken_shouldReturnCorrectUserId() {
        // Arrange
        Long expectedUserId = 123L;
        String token = tokenService.generateToken(expectedUserId);

        // Act
        Long actualUserId = tokenService.getUserIdFromToken(token);

        // Assert
        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void getUserIdFromToken_shouldWorkWithBearerPrefix() {
        // Arrange
        Long expectedUserId = 456L;
        String token = tokenService.generateToken(expectedUserId);
        String tokenWithBearer = "Bearer " + token;

        // Act
        Long actualUserId = tokenService.getUserIdFromToken(tokenWithBearer);

        // Assert
        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void getUserIdFromToken_shouldWorkWithBearerPrefixAndExtraSpaces() {
        // Arrange
        Long expectedUserId = 789L;
        String token = tokenService.generateToken(expectedUserId);
        String tokenWithBearer = "Bearer   " + token;

        // Act
        Long actualUserId = tokenService.getUserIdFromToken(tokenWithBearer);

        // Assert
        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void getUserIdFromToken_shouldWorkWithoutBearerPrefix() {
        // Arrange
        Long expectedUserId = 321L;
        String token = tokenService.generateToken(expectedUserId);

        // Act
        Long actualUserId = tokenService.getUserIdFromToken(token);

        // Assert
        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void getUserIdFromToken_shouldThrowException_whenTokenIsInvalid() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(JwtException.class, () -> tokenService.getUserIdFromToken(invalidToken));
    }

    @Test
    void getUserIdFromToken_shouldThrowException_whenTokenIsEmpty() {
        // Arrange
        String emptyToken = "";

        // Act & Assert
        assertThrows(Exception.class, () -> tokenService.getUserIdFromToken(emptyToken));
    }

    @Test
    void getUserIdFromToken_shouldThrowException_whenTokenIsOnlyBearer() {
        // Arrange
        String bearerOnly = "Bearer ";

        // Act & Assert
        assertThrows(Exception.class, () -> tokenService.getUserIdFromToken(bearerOnly));
    }

    @Test
    void getUserIdFromToken_shouldThrowException_whenTokenIsMalformed() {
        // Arrange
        String malformedToken = "Bearer not.a.valid.jwt.token.format";

        // Act & Assert
        assertThrows(JwtException.class, () -> tokenService.getUserIdFromToken(malformedToken));
    }

    @Test
    void getUserIdFromToken_shouldThrowException_whenTokenHasWrongSignature() {
        // Arrange
        // Create a token with a different key
        SecretKey differentKey = Jwts.SIG.HS256.key().build();
        String tokenWithDifferentKey = Jwts.builder()
                .subject("123")
                .signWith(differentKey)
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .compact();

        // Act & Assert
        assertThrows(JwtException.class, () -> tokenService.getUserIdFromToken(tokenWithDifferentKey));
    }

    @Test
    void roundTrip_generateAndExtractUserId_shouldPreserveUserId() {
        // Arrange
        Long[] userIds = {1L, 100L, 999999L, 12345678L};

        for (Long userId : userIds) {
            // Act
            String token = tokenService.generateToken(userId);
            Long extractedUserId = tokenService.getUserIdFromToken(token);

            // Assert
            assertEquals(userId, extractedUserId,
                    "User ID should be preserved in round trip for userId: " + userId);
        }
    }

    @Test
    void roundTrip_withBearerPrefix_shouldPreserveUserId() {
        // Arrange
        Long userId = 555L;

        // Act
        String token = tokenService.generateToken(userId);
        String tokenWithBearer = "Bearer " + token;
        Long extractedUserId = tokenService.getUserIdFromToken(tokenWithBearer);

        // Assert
        assertEquals(userId, extractedUserId);
    }

    @Test
    void generateToken_shouldNotReturnNull() {
        // Arrange
        Long userId = 1L;

        // Act
        String token = tokenService.generateToken(userId);

        // Assert
        assertNotNull(token, "Generated token should never be null");
    }

    @Test
    void getUserIdFromToken_shouldHandleVeryLargeUserIds() {
        // Arrange
        Long largeUserId = Long.MAX_VALUE;

        // Act
        String token = tokenService.generateToken(largeUserId);
        Long extractedUserId = tokenService.getUserIdFromToken(token);

        // Assert
        assertEquals(largeUserId, extractedUserId);
    }

    @Test
    void getUserIdFromToken_shouldHandleSmallUserIds() {
        // Arrange
        Long smallUserId = 1L;

        // Act
        String token = tokenService.generateToken(smallUserId);
        Long extractedUserId = tokenService.getUserIdFromToken(token);

        // Assert
        assertEquals(smallUserId, extractedUserId);
    }
}
