package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public List<UserResponseDTO> findAll(Sort sorted) {
        return userRepository.findAll(sorted)
                .stream()
                .map(UserResponseDTO::fromUser)
                .toList();
    }

    public UserResponseDTO createUser(UserCreateDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
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

    public UserResponseDTO updateUser(long id, UserUpdateDTO dto) {
        User user = this.findById(id);
        user.updateFromDTO(dto);
        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromUser(savedUser);
    }

    public List<PetResponseDTO> getPetsCreatedByUser(long id) {
        User user = this.findById(id);
        return user.getCreatedPets()
                .stream()
                .map(PetResponseDTO::fromPet)
                .toList();
    }

    public String authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Mail o contraseña incorrectos"));


        if (!user.getPassword().equals(passwordEncoder.encode(password)) || !user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mail o contraseña incorrectos");
        }

        return user.getId() + "123456";
    }
}
