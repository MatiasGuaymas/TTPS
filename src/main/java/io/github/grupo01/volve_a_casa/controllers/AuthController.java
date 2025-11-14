package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE, name = "AuthController")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> authenticateUser(@RequestHeader("email") String email, @RequestHeader("password") String password) {
        String token = userService.authenticateUser(email, password);
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", token);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
