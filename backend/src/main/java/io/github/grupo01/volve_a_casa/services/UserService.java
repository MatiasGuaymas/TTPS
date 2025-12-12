package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.auth.AuthResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.auth.UserAuthDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // TODO: Test de integracion
    public User findById(long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found"));
    }

    // TODO: Test de integracion
    public List<UserResponseDTO> findAll(Sort sorted) {
        return userRepository.findAll(sorted)
                .stream()
                .map(UserResponseDTO::fromUser)
                .toList();
    }

    public UserResponseDTO createUser(UserCreateDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El email ya está siendo utilizado por otro usuario"
            );
        }

        String hashedPassword = passwordEncoder.encode(dto.password());
        User user = new User(
                dto.name(),
                dto.lastName(),
                dto.email(),
                hashedPassword,
                dto.phoneNumber(),
                dto.city(),
                dto.neighborhood(),
                dto.latitude(),
                dto.longitude()
        );

        return UserResponseDTO.fromUser(userRepository.save(user));
    }

    public UserResponseDTO updateUser(User user, UserUpdateDTO dto) {
        user.updateFromDTO(dto);
        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromUser(savedUser);
    }

    public AuthResponseDTO authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is disabled");
        }

        String token = tokenService.generateToken(user.getId());
        UserAuthDTO userAuthDTO = new UserAuthDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());

        return new AuthResponseDTO(token, userAuthDTO);
    }
}
