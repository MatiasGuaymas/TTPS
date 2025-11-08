package io.github.grupo01.volve_a_casa.persistence.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.grupo01.volve_a_casa.controllers.UserController;
import io.github.grupo01.volve_a_casa.controllers.dto.UserUpdateDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest extends BaseControllerTest {
    @Mock
    private UserRepository userRepository;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeAll
    static void init() {
        createContext();
    }

    @BeforeEach
    void setup() {
        // cleanDatabase();
        this.objectMapper = new ObjectMapper();
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

    @Test
    void createUser_whenUserDoesNotExist_returnsCreated() throws Exception {
        User newUser = buildUser("Luis", "Martinez", "luismartinez@test.com");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("luismartinez@test.com"));
    }

    @Test
    void createUser_whenUserExists_returnsConflict() throws Exception {
        User existingUser = buildUser("Carlos", "Lopez", "carloslopez@test.com");

        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(java.util.Optional.of(existingUser));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingUser)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserById_whenUserExistsAndTokenValid_returnsOk() throws Exception {
        User existingUser = buildUser("Marta", "Sanchez", "martasanchez@test.com");
        //id del usuario que hace la request
        long idRequester = 3L;
        //id del usuario que se quiere obtener
        long idExistingUser = 1L;

        // Simular que el token es válido
        when(userRepository.existsById(idRequester)).thenReturn(true);
        // Simular que el usuario existe
        when(userRepository.findById(idExistingUser)).thenReturn(java.util.Optional.of(existingUser));
        mockMvc.perform(get("/users/" + idExistingUser)
                        .header("token", idRequester + "123456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("martasanchez@test.com"));
    }

    @Test
    void getUserById_whenTokenInvalid_returnsUnauthorized() throws Exception {
        //id del usuario que hace la request
        long idRequester = 3L;

        // Simular que el token es inválido
        when(userRepository.existsById(idRequester)).thenReturn(false);
        mockMvc.perform(get("/users/1")
                        .header("token", idRequester + "123456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido"));
    }

    @Test
    void getUserById_whenUserDoesNotExist_returnsNotFound() throws Exception {
        Long userId = 999L;
        //id del usuario que hace la request
        long idRequester = 3L;

        // Simular que el token es válido
        when(userRepository.existsById(idRequester)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/users/" + userId)
                        .header("token", idRequester+"123456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User no encontrado"));
    }

    @Test
    void updateUser_whenTokenValid_returnsOk() throws Exception {
        long idRequester = 1L;
        String token = idRequester + "123456";
        User existingUser = buildUser("Juan", "Perez", "juanperez@test.com");
        existingUser.setEmail("juan@test.com");
        existingUser.setName("Juan");
        existingUser.setLastName("Perez");
        existingUser.setCity("La Plata");

        UserUpdateDTO dto = new UserUpdateDTO("Juan Carlos", "Gomez", null, "Buenos Aires", null, 0, 0);

        when(userRepository.existsById(idRequester)).thenReturn(true);
        when(userRepository.findById(idRequester)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        mockMvc.perform(put("/users/update")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Carlos"))
                .andExpect(jsonPath("$.lastName").value("Gomez"))
                .andExpect(jsonPath("$.city").value("Buenos Aires"));

        // Assert
        assertEquals("Juan Carlos", existingUser.getName());
        assertEquals("Gomez", existingUser.getLastName());
        assertEquals("Buenos Aires", existingUser.getCity());
    }

    //TODO: agregar más tests para update (token inválido, usuario no existe, etc)


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
