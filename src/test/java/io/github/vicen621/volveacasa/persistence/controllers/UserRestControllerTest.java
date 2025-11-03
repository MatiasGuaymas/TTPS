package io.github.vicen621.volveacasa.persistence.controllers;

import io.github.vicen621.volveacasa.controllers.UserRestController;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import io.github.vicen621.volveacasa.persistence.entities.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class UserRestControllerTest extends BaseControllerTest {
    @Mock
    private UsuarioDAO usuarioDAO;
    private MockMvc mockMvc;

    @BeforeAll
    static void init() {
        createContext();
    }

    @BeforeEach
    void setup() {
        cleanDatabase();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserRestController(usuarioDAO)).build();
    }

    @Test
    void listAllUsersOrderByName_whenEmpty_returnsNoContent() throws Exception {
        when(usuarioDAO.getAll("nombre")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllUsersOrderByName_whenUsersExist_returnsOkAndList() throws Exception {
        Usuario usuario1 = Usuario.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@test.com")
                .contrasena("1234")
                .telefono("123456789")
                .ciudad("CiudadTest")
                .barrio("BarrioTest")
                .latitud(-34.6037f)
                .longitud(-58.3816f)
                .build();
        Usuario usuario2 = Usuario.builder()
                .nombre("Ana")
                .apellidos("Gomez")
                .email("ana.gomez@test.com")
                .contrasena("abcd")
                .telefono("987654321")
                .ciudad("OtraCiudad")
                .barrio("OtroBarrio")
                .latitud(-34.6037f)
                .longitud(-58.3816f)
                .build();
        List<Usuario> users = Arrays.asList(usuario1, usuario2);

        when(usuarioDAO.getAll("nombre")).thenReturn(users);

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[1].nombre").value("Ana"));
    }
}
