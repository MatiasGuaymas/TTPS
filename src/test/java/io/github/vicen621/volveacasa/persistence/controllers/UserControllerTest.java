package io.github.vicen621.volveacasa.persistence.controllers;

import io.github.grupo01.volve_a_casa.controllers.UserController;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest extends BaseControllerTest {
    @Mock
    private UserRepository userRepository;
    private MockMvc mockMvc;

    @BeforeAll
    static void init() {
        createContext();
    }

    @BeforeEach
    void setup() {
        // cleanDatabase();
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userRepository)).build();
    }

    @Test
    void listAllUsersOrderByName_whenEmpty_returnsNoContent() throws Exception {
        when(userRepository.findAll(Sort.by("name"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllUsersOrderByName_whenUsersExist_returnsOkAndList() throws Exception {
        List<User> users = Arrays.asList(
            buildUser("Juan", "Perez", "juan.perez@test.com"),
            buildUser("Ana", "Gomez", "ana.gomez@test.com")
        );

        when(userRepository.findAll(Sort.by("name"))).thenReturn(users);

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Juan"))
                .andExpect(jsonPath("$[1].name").value("Ana"));
    }

    private User buildUser(String nombre, String apellidos, String email) {
        return User.builder()
                .nombre(nombre)
                .apellidos(apellidos)
                .email(email)
                .contrasena("1234")
                .telefono("123456789")
                .ciudad("CiudadTest")
                .barrio("BarrioTest")
                .latitud(-34.6037f)
                .longitud(-58.3816f)
                .build();
    }
}
