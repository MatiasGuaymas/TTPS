package io.github.grupo01.volve_a_casa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserLoginDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.exceptions.GlobalExceptionHandler;
import io.github.grupo01.volve_a_casa.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30";

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void authenticateUser_whenValid_returnsTokenInBody() throws Exception {
        when(userService.authenticateUser("test@test.com", "pass")).thenReturn(JWT_TOKEN);

        UserLoginDTO loginDTO = new UserLoginDTO("test@test.com", "pass");

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(JWT_TOKEN));
    }

    @Test
    void authenticateUser_whenInvalid_throwsForbidden() throws Exception {
        when(userService.authenticateUser("test@test.com", "wrong"))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Mail o contraseña incorrectos"));

        UserLoginDTO loginDTO = new UserLoginDTO("test@test.com", "wrong");

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isForbidden());
    }

    // ========= Register =========

    @Test
    void registerUser_whenUserDoesNotExist_returnsCreated() throws Exception {

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

        UserResponseDTO created = new UserResponseDTO(
                10L,
                "nombre",
                "apellido",
                "luismartinez@test.com",
                "221 333-3333",
                "Buenos Aires",
                "Lanús",
                -54.23f,
                -12.32f,
                100
        );

        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("luismartinez@test.com"));
    }

    @Test
    void registerUser_whenUserExists_returnsConflict() throws Exception {
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

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }
}
