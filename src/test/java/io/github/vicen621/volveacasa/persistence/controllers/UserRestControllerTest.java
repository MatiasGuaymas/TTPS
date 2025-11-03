package io.github.vicen621.volveacasa.persistence.controllers;

import io.github.vicen621.volveacasa.controllers.UserRestController;
import io.github.vicen621.volveacasa.persistence.dao.hibernate.UsuarioDAOHibernate;
import io.github.vicen621.volveacasa.persistence.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.dao.hibernate.UsuarioDAOHibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserRestController.class)
@WebAppConfiguration
@ComponentScan(basePackages = "io.github.vicen621.volveacasa.persistence.dao.hibernate")
public class UserRestControllerTest extends BaseControllerTest{
    @Autowired
    private  WebApplicationContext webApplicationContext;
    @Autowired
    private UsuarioDAOHibernate usuarioDAO;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        createContext();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        cleanDatabase();
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
        usuarioDAO.persist(usuario1);
        usuarioDAO.persist(usuario2);
    }

    @Test
    void testListAllUsersOrderByName() throws Exception{
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[1].nombre").value("Juan"));
    }
}
