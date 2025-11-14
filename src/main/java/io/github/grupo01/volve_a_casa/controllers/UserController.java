package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.security.TokenValidator;
import io.github.grupo01.volve_a_casa.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE, name = "UserRestController")
public class UserController {
    private final UserService userService;
    private final TokenValidator tokenValidator;

    @Autowired
    public UserController(UserService userService, TokenValidator tokenValidator) {
        this.userService = userService;
        this.tokenValidator = tokenValidator;
    }

    @GetMapping
    public ResponseEntity<?> listAllUsersOrderByName() {
        List<UserResponseDTO> users = userService.findAll(Sort.by("name"));
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO user = userService.createUser(userCreateDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader("token") String token, @PathVariable("id") Long id) {
        tokenValidator.validate(token);
        User user = userService.findById(id);
        return ResponseEntity.ok(UserResponseDTO.fromUser(user));
    }

    @GetMapping("/my_pets")
    public ResponseEntity<?> getMyPets(@RequestHeader("token") String token) {
        tokenValidator.validate(token);
        List<PetResponseDTO> pets = userService.getPetsCreatedByUser(tokenValidator.extractUserId(token));
        return ResponseEntity.ok(pets);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestHeader("token") String token, @Valid @RequestBody UserUpdateDTO updatedData) {
        tokenValidator.validate(token);
        UserResponseDTO user = userService.updateUser(tokenValidator.extractUserId(token), updatedData);
        return ResponseEntity.ok(user);
    }

}
