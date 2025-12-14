package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.AdminUserUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.interfaces.IUserController;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.filters.UserFilter;
import io.github.grupo01.volve_a_casa.security.UserAuthentication;
import io.github.grupo01.volve_a_casa.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> listAllUsers(
            @ModelAttribute UserFilter filter,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        List<UserResponseDTO> users = userService.findAllFiltered(filter, pageable);
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@AuthenticationPrincipal User requester, @PathVariable("id") Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserResponseDTO.fromUser(user));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal User requester, @Valid @RequestBody UserUpdateDTO updatedData) {
        UserResponseDTO user = userService.updateUser(requester, updatedData);
        return ResponseEntity.ok(user);
    }

    // Admin endpoints
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateUserStatus(
            Authentication authentication,
            @PathVariable("id") Long userId,
            @RequestParam("enabled") Boolean enabled
    ) {
        User requester = (User) authentication.getPrincipal();
        
        if (requester.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can update user status");
        }

        UserResponseDTO updatedUser = userService.updateUserStatus(userId, enabled);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> adminUpdateUser(
            Authentication authentication,
            @PathVariable("id") Long userId,
            @Valid @RequestBody AdminUserUpdateDTO updatedData
    ) {
        User requester = (User) authentication.getPrincipal();
        
        if (requester.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can update users");
        }

        UserResponseDTO updatedUser = userService.adminUpdateUser(userId, updatedData);
        return ResponseEntity.ok(updatedUser);
    }

}
