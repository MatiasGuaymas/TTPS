package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.exceptions.GlobalExceptionHandler;
import io.github.grupo01.volve_a_casa.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void authenticateUser_whenValid_returnsTokenHeader() throws Exception {
        when(userService.authenticateUser("test@test.com", "pass")).thenReturn("42123456");

        mockMvc.perform(post("/auth")
                        .header("email", "test@test.com")
                        .header("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(header().string("token", "42123456"));
    }

    @Test
    void authenticateUser_whenInvalid_throwsForbidden() throws Exception {
        when(userService.authenticateUser("test@test.com", "wrong"))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Mail o contraseña incorrectos"));

        mockMvc.perform(post("/auth")
                        .header("email", "test@test.com")
                        .header("password", "wrong"))
                .andExpect(status().isForbidden());
    }
}
