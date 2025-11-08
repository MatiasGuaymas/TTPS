package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value="/authentication", produces=MediaType.APPLICATION_JSON_VALUE, name="AuthController")
public class AuthController {
    private UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String,String>> authenticateUser(@RequestHeader("usuario")String mail, @RequestHeader("clave")String password) {
        Optional<User> optionalUser = userRepository.findByEmail(mail);
        Map<String, String> response = new HashMap<>();
        if (optionalUser.isEmpty()) {
            response.put("error", "Usuario no encontrado");
            response.put("message", "El correo ingresado no corresponde a ningún usuario registrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();


        if (!user.checkPassword(password) || !user.isEnabled()) {
            response.put("error", "Acceso denegado");
            response.put("message", "credenciales incorrectas o usuario deshabilitado");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        response.put("message", "Autenticación exitosa");
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", optionalUser.get().getId() + "123456");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
