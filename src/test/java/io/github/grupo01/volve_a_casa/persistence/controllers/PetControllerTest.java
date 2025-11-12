package io.github.grupo01.volve_a_casa.persistence.controllers;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.grupo01.volve_a_casa.controllers.PetController;
import io.github.grupo01.volve_a_casa.controllers.dto.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class PetControllerTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new PetController(petRepository, userRepository)).build();
    }

    @Test
    void createPet_whenValidDataAndToken_returnsCreated() throws Exception {
        long userId = 1L;
        String token = userId + "123456";

        User user = buildUser(userId);
        PetCreateDTO dto = new PetCreateDTO(
                "Tobby",
                "Mediano",
                "Perro marrón con manchas",
                "Marrón",
                "Labrador",
                12.5f,
                -34.6f,
                -58.4f,
                Pet.Type.PERRO
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/pets/create")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Tobby"))
                .andExpect(jsonPath("$.color").value("Marrón"))
                .andExpect(jsonPath("$.race").value("Labrador"));
    }

    @Test
    void createPet_whenInvalidToken_returnsUnauthorized() throws Exception {
        PetCreateDTO dto = new PetCreateDTO(
                "Tobby",
                "Mediano",
                "Perro marrón con manchas",
                "Marrón",
                "Labrador",
                12.5f,
                -34.6f,
                -58.4f,
                Pet.Type.PERRO
        );

        mockMvc.perform(post("/pets/create")
                        .header("token", "INVALIDO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido"));
    }

    @Test
    void updatePet_whenOwnerValid_updatesPet() throws Exception {
        long userId = 1L;
        String token = userId + "123456";

        User user = buildUser(userId);
        Pet pet = buildPet(user);

        PetUpdateDTO dto = new PetUpdateDTO(
        "Tobby",                
        "Nueva descripción",   
        "Nuevo color",         
        "Pequeño",              
        "Poodle",               
        10.0f,
        Pet.Type.PERRO,
        Pet.State.PERDIDO_PROPIO,
        -34.6f,
        -58.4f
);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/pets/1")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Nuevo color"))
                .andExpect(jsonPath("$.race").value("Poodle"));
    }

    @Test
    void deletePet_whenOwnerValid_returnsOk() throws Exception {
        long userId = 1L;
        String token = userId + "123456";
        User user = buildUser(userId);
        Pet pet = buildPet(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        mockMvc.perform(delete("/pets/1")
                        .header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mascota eliminada correctamente"));
    }

    private User buildUser(Long id) {
        User user = User.builder()
                .nombre("Juan")
                .apellidos("Pérez")
                .email("juan@test.com")
                .contrasena("1234")
                .telefono("123456789")
                .ciudad("La Plata")
                .barrio("Gonnet")
                .latitud(-34.6037f)
                .longitud(-58.3816f)
                .puntos(100)
                .habilitado(true)
                .rol(User.Role.USER)
                .build();
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception ignored) {}

        return user;
    }

    private Pet buildPet(User owner) {
        return Pet.builder()
                .nombre("Tobby")
                .tamano("Mediano")
                .descripcion("Perro marrón")
                .color("Marrón")
                .raza("Labrador")
                .peso(12.5f)
                .latitud(-34.6f)
                .longitud(-58.4f)
                .fechaPerdida(LocalDate.now())
                .estado(Pet.State.PERDIDO_PROPIO)
                .tipo(Pet.Type.PERRO)
                .creador(owner)
                .agregarFoto("base64ej")
                .build();
    }

}