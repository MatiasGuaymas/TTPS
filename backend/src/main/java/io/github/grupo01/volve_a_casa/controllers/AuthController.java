package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.auth.AuthResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserLoginDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.interfaces.IAuthController;
import io.github.grupo01.volve_a_casa.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE, name = "AuthController")
public class AuthController implements IAuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Override
    public ResponseEntity<?> authenticateUser(@RequestBody UserLoginDTO userLoginDTO) {
        AuthResponseDTO authResponse = userService.authenticateUser(userLoginDTO.email(), userLoginDTO.password());
        return ResponseEntity.ok(authResponse);
    }

    // @Override
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO user = userService.createUser(userCreateDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
