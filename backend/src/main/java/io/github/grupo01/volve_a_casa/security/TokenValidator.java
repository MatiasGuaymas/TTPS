package io.github.grupo01.volve_a_casa.security;

import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TokenValidator {

    private final UserRepository userRepository;

    public TokenValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validate(String token) {
        if (token == null || !token.endsWith("123456")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido");
        }

        long id;
        try {
            id = extractUserId(token);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalido");
        }

        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido");
        }
    }

    public long extractUserId(String token) {
        return Long.parseLong(token.replace("123456", ""));
    }
}

