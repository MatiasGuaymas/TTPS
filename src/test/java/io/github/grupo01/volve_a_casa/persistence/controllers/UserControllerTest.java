package io.github.grupo01.volve_a_casa.persistence.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.grupo01.volve_a_casa.controllers.UserController;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.exceptions.GlobalExceptionHandler;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.security.TokenValidator;
import io.github.grupo01.volve_a_casa.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private UserService userService;
    @Mock
    private TokenValidator tokenValidator;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ========= LIST USERS =========

    @Test
    void listAllUsersOrderByName_whenEmpty_returnsNoContent() throws Exception {
        when(userService.findAll(Sort.by("name"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllUsersOrderByName_whenUsersExist_returnsOkAndList() throws Exception {
        UserResponseDTO user1 = createResponse(1L, "Juan", "Perez", "juan.perez@test.com");
        UserResponseDTO user2 = createResponse(2L, "Ana", "Gomez", "ana.gomez@test.com");

        when(userService.findAll(Sort.by("name"))).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan"))
                .andExpect(jsonPath("$[1].name").value("Ana"));
    }

    // ========= CREATE =========

    @Test
    void createUser_whenUserDoesNotExist_returnsCreated() throws Exception {

        UserCreateDTO dto = new UserCreateDTO(
                "luismartinez@test.com",
                "password",
                "nombre",
                "apellido",
                "11 1234-5678",
                "La Plata",
                "Manuel B. Gonnet",
                -51.03f,
                -43.23f
        );

        UserResponseDTO created = createResponse(10L, "nombre", "apellido", "luismartinez@test.com");

        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("luismartinez@test.com"));
    }

    @Test
    void createUser_whenUserExists_returnsConflict() throws Exception {
        UserCreateDTO dto = new UserCreateDTO(
                "luismartinez@test.com",
                "password",
                "nombre",
                "apellido",
                "11 1234-5678",
                "La Plata",
                "Manuel B. Gonnet",
                -51.03f,
                -43.23f
        );

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está siendo utilizado por otro usuario"))
                .when(userService).createUser(any(UserCreateDTO.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    // ========= GET USER BY ID =========

    @Test
    void getUserById_whenUserExistsAndTokenValid_returnsOk() throws Exception {
        long requesterId = 3L;
        long targetUserId = 1L;
        String token = requesterId + "123456";

        User user = createUser("Marta", "Sanchez", "martasanchez@test.com");

        doNothing().when(tokenValidator).validate(anyString());
        when(userService.findById(targetUserId)).thenReturn(user);

        mockMvc.perform(get("/users/" + targetUserId)
                        .header("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("martasanchez@test.com"));
    }

    @Test
    void getUserById_whenTokenInvalid_returnsUnauthorized() throws Exception {
        long requesterId = 3L;
        String token = requesterId + "123456";

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido"))
                .when(tokenValidator).validate(anyString());

        mockMvc.perform(get("/users/1")
                        .header("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token invalido"));
    }

    @Test
    void getUserById_whenUserDoesNotExist_returnsNotFound() throws Exception {
        long requesterId = 3L;
        long targetUserId = 999L;
        String token = requesterId + "123456";

        doNothing().when(tokenValidator).validate(anyString());
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User no encontrado"))
                .when(userService).findById(targetUserId);

        mockMvc.perform(get("/users/" + targetUserId)
                        .header("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User no encontrado"));
    }

    // ========= UPDATE USER =========

    @Test
    void updateUser_whenTokenValid_returnsOk() throws Exception {
        long requesterId = 1L;
        String token = requesterId + "123456";

        UserUpdateDTO updateDTO =
                new UserUpdateDTO("Juan Carlos", "Gomez", null, "Buenos Aires", null, 0f, 0f);

        UserResponseDTO updated = createResponse(requesterId, "Juan Carlos", "Gomez", "juan@test.com");

        doNothing().when(tokenValidator).validate(anyString());
        when(userService.updateUser(anyLong(), any(UserUpdateDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/users/update")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Carlos"))
                .andExpect(jsonPath("$.lastName").value("Gomez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void updateUser_whenTokenInvalid_returnsUnauthorized() throws Exception {
        UserUpdateDTO updateDTO =
                new UserUpdateDTO("Juan Carlos", "Gomez", null, "Buenos Aires", null, 0f, 0f);

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido"))
                .when(tokenValidator).validate(anyString());

        mockMvc.perform(put("/users/update")
                        .header("token", "Invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token invalido"));
    }

    private UserResponseDTO createResponse(Long id, String name, String lastname, String email) {
        return new UserResponseDTO(
                id,
                name,
                lastname,
                email,
                "221 333-3333",
                "Buenos Aires",
                "Lanús",
                -54.23f,
                -12.32f,
                100
        );
    }

    private User createUser(String name, String lastname, String email) {
        return new User(
                name,
                lastname,
                email,
                "password",
                "221 111-1111",
                "La Plata",
                "La Plata",
                -54.23f,
                -12.32f
        );
    }
}

