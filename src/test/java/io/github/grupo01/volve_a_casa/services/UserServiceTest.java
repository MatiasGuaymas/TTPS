package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;


    @Test
    public void createUser_whenUserDoesNotExists_createAndReturn() {
        String email = "test@gmail.com";
        UserCreateDTO dto = new UserCreateDTO(
                email,
                "password",
                "name",
                "lastName",
                "221 111-1111",
                "city",
                "neighborhood",
                -54f,
                -27f
        );
        User user = createUser("name", email);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponseDTO response = userService.createUser(dto);
        assertEquals(user.getName(), response.name());
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_whenUserExists_throws() {
        String email = "test@gmail.com";
        UserCreateDTO dto = new UserCreateDTO(
                email,
                "password",
                "name",
                "lastName",
                "221 111-1111",
                "city",
                "neighborhood",
                -54f,
                -27f
        );

        when(userRepository.existsByEmail(email)).thenReturn(true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.createUser(dto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("El email ya está siendo utilizado por otro usuario", ex.getReason());
        verify(passwordEncoder, times(0)).encode("password");
        verify(userRepository, times(0)).save(any(User.class));

    }

    @Test
    void updateUser_success() {
        User user = createUser("Juan", "test@gmail.com");
        UserUpdateDTO dto = new UserUpdateDTO("NuevoNombre", null, null, null, null, null, null);

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO response = userService.updateUser(10L, dto);

        assertEquals("NuevoNombre", user.getName());
        assertEquals(response.name(), user.getName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_userNotFound_throws() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        UserUpdateDTO dto = new UserUpdateDTO("NuevoNombre", null, null, null, null, null, null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.updateUser(10L, dto));

        assertEquals("404 NOT_FOUND \"User with id 10 not found\"", ex.getMessage());
    }

    @Test
    void authenticateUser_userNotFound_throwsForbidden() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.authenticateUser("test@test.com", "123456"));
    }

    @Test
    void authenticateUser_wrongPassword_throwsForbidden() {
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("123456");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "123456")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.authenticateUser("test@test.com", "wrongPass"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertEquals("Invalid credentials", ex.getReason());
    }

    @Test
    void authenticateUser_userDisabled_throwsForbidden() {
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("123456");
        when(user.isEnabled()).thenReturn(false);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "123456")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.authenticateUser("test@test.com", "123456"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertEquals("Invalid credentials", ex.getReason());
    }

    @Test
    void authenticateUser_success_returnsToken() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(10L);
        when(user.getPassword()).thenReturn("123456");
        when(user.isEnabled()).thenReturn(true);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "123456")).thenReturn(true);

        String token = userService.authenticateUser("test@test.com", "123456");
        assertEquals("10123456", token);
    }

    public User createUser(String name, String email) {
        return new User(
                name,
                "lastName",
                email,
                "password",
                "phone",
                "city",
                "neighborhood",
                -54f,
                -27f
        );
    }
}