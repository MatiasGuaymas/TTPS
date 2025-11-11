package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value="/users", produces= MediaType.APPLICATION_JSON_VALUE, name="UserRestController")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> listAllUsersOrderByName() {
        List<User> users = userRepository.findAll(Sort.by("name"));
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // TODO: testear este metodo entero
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        Map<String, String> response = new HashMap<>();
        if (!userCreateDTO.isValid()) {
            response.put("error", "Datos inválidos");
            response.put("message", "Faltan campos obligatorios para crear el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (userRepository.findByEmail(userCreateDTO.email()).isPresent()) {
            response.put("error", "Email repetido");
            response.put("message", "El email ya está siendo utilizado por otro usuario");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        userRepository.save(new User(userCreateDTO));
        return new ResponseEntity<>(userCreateDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader("token") String token, @PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();
        if (!checkToken(token)) {
            response.put("error", "Token inválido");
            response.put("message", "El token proporcionado no es válido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            response.put("error", "User no encontrado");
            response.put("message", "No existe un usuario con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(user.get());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestHeader("token") String token, @RequestBody UserUpdateDTO updatedData) {
        Map<String, String> response = new HashMap<>();

        if (!checkToken(token)) {
            response.put("error", "Token inválido");
            response.put("message", "El token proporcionado no es válido");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Optional<User> optionalUser = userRepository.findById(Long.valueOf(token.replace("123456","")));

        if (optionalUser.isEmpty()) {
            response.put("error", "Usuario no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        user.updateFromDTO(updatedData);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    private boolean checkToken(String token){
        return token != null && token.endsWith("123456") && userRepository.existsById(Long.valueOf(token.replace("123456","")));
    }

}
