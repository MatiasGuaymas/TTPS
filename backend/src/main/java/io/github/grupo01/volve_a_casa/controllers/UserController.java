package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.interfaces.IUserController;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.security.UserAuthentication;
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
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE, name = "UserRestController")
public class UserController implements IUserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping
    public ResponseEntity<?> listAllUsersOrderByName() {
        List<UserResponseDTO> users = userService.findAll(Sort.by("name"));
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(UserAuthentication requester, @PathVariable("id") Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserResponseDTO.fromUser(user));
    }

    @Override
    @GetMapping("/my_pets")
    public ResponseEntity<?> getMyPets(UserAuthentication requester) {
        List<PetResponseDTO> pets = userService.getPetsCreatedByUser(requester.getDetails());

        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(pets);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(UserAuthentication requester, @Valid @RequestBody UserUpdateDTO updatedData) {
        UserResponseDTO user = userService.updateUser(requester.getPrincipal(), updatedData);
        return ResponseEntity.ok(user);
    }

}
