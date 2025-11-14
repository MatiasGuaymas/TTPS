package io.github.grupo01.volve_a_casa.persistence.security;

import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import io.github.grupo01.volve_a_casa.security.TokenValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenValidatorTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenValidator tokenValidator;

    @Test
    public void validate_whenTokenNull_throws() {
        assertThrows(ResponseStatusException.class, () -> tokenValidator.validate(null));
    }

    @Test
    public void validate_whenTokenNotEndsIn123456_throws() {
        String token = "1";
        assertThrows(ResponseStatusException.class, () -> tokenValidator.validate(token));
    }

    @Test
    public void validate_whenTokenNotStartsWithValidID_throws() {
        String token = "99123456";
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> tokenValidator.validate(token));
    }

    @Test
    void validate_whenTokenIsValid_doesNotThrow() {
        String token = "99123456";
        when(userRepository.existsById(99L)).thenReturn(true);

        assertDoesNotThrow(() -> tokenValidator.validate(token));
    }

    @Test
    void validate_whenTokenHasInvalidFormat_throws() {
        assertThrows(ResponseStatusException.class, () -> tokenValidator.validate("abc123456"));
    }

    @Test
    void extractUserId_successfulExtraction() {
        String token = "14123456";
        long id = tokenValidator.extractUserId(token);
        assertEquals(14, id);
    }

}
