package io.github.grupo01.volve_a_casa.persistence.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.grupo01.volve_a_casa.controllers.SightingController;
import io.github.grupo01.volve_a_casa.controllers.dto.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.SightingRepository;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SightingControllerTest extends BaseControllerTest {
    @Mock
    private SightingRepository sightingRepository;
    @Mock
    private PetRepository petRepository;
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
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                new SightingController(sightingRepository, petRepository, userRepository)
        ).build();
    }

    @Test
    void listAllSightings_whenEmpty_returnsNoContent() throws Exception {
        when(sightingRepository.findAll(Sort.by(Sort.Direction.DESC, "date")))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/sightings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllSightings_whenSightingsExist_returnsOkAndList() throws Exception {
        User owner = buildUser("Rodolfo Alfredo", "Bertone", "bertone@info.unlp.edu.ar");
        User reporter = buildUser("Laura Cristina", "De Giusti", "ldgiusti@info.unlp.edu.ar");
        Pet pet = buildPet("Firulais", owner);

        Sighting sighting1 = buildSighting(pet, reporter, LocalDate.now());
        Sighting sighting2 = buildSighting(pet, reporter, LocalDate.now().minusDays(1));

        List<Sighting> sightings = Arrays.asList(sighting1, sighting2);

        when(sightingRepository.findAll(Sort.by(Sort.Direction.DESC, "date")))
                .thenReturn(sightings);

        mockMvc.perform(get("/sightings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].petName").value("Firulais"))
                .andExpect(jsonPath("$[1].petName").value("Firulais"));
    }

    @Test
    void getSightingsByPetId_whenPetDoesNotExist_returnsNotFound() throws Exception {
        Long petId = 999L;

        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/sightings/pet/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mascota no encontrada"));
    }

    @Test
    void getSightingsByPetId_whenPetExistsButNoSightings_returnsNoContent() throws Exception {
        Long petId = 1L;
        User owner = buildUser("Laura Andrea", "Fava", "lfava@info.unlp.edu.ar");
        Pet pet = buildPet("Sultán", owner);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

        mockMvc.perform(get("/sightings/pet/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSightingsByPetId_whenSightingsExist_returnsOkAndList() throws Exception {
        Long petId = 1L;
        User owner = buildUser("Alejandro", "Fernandez", "fernandez@info.unlp.edu.ar");
        User reporter = buildUser("Alejandra", "Garrido", "garrido@info.unlp.edu.ar");
        Pet pet = buildPet("Colita", owner);

        Sighting sighting = buildSighting(pet, reporter, LocalDate.now());
        pet.addAvistamiento(sighting);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

        mockMvc.perform(get("/sightings/pet/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].petName").value("Colita"));
    }

    @Test
    void createSighting_whenTokenDoesNotEndWith123456_returnsUnauthorized() throws Exception {
        String invalidToken = "1654321";

        SightingCreateDTO dto = new SightingCreateDTO(
                1L,
                -34.6037f,
                -58.3816f,
                "photoBase64",
                LocalDate.now(),
                "Vi la mascota en el parque"
        );

        mockMvc.perform(post("/sightings")
                        .header("token", invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido"));
    }

    @Test
    void createSighting_whenUserDoesNotExist_returnsUnauthorized() throws Exception {
        Long userId = 999L;
        String token = userId + "123456";

        SightingCreateDTO dto = new SightingCreateDTO(
                1L,
                -34.6037f,
                -58.3816f,
                "photoBase64",
                LocalDate.now(),
                "Vi la mascota en el parque"
        );

        when(userRepository.existsById(userId)).thenReturn(false);

        mockMvc.perform(post("/sightings")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido"));
    }

    /*
    @Test
    void createSighting_whenTokenIsNull_returnsUnauthorized() throws Exception {
        SightingCreateDTO dto = new SightingCreateDTO(
                1L,
                -34.6037f,
                -58.3816f,
                "photoBase64",
                LocalDate.now(),
                "Vi la mascota en el parque"
        );

        mockMvc.perform(post("/sightings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido"));
    } */

    @Test
    void createSighting_whenDataInvalid_returnsBadRequest() throws Exception {
        Long userId = 1L;
        String token = userId + "123456";

        SightingCreateDTO dto = new SightingCreateDTO(
                null,
                -34.6037f,
                -58.3816f,
                "photoBase64",
                LocalDate.now(),
                "Comentario"
        );

        when(userRepository.existsById(userId)).thenReturn(true);

        mockMvc.perform(post("/sightings")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Datos inválidos"));
    }

    @Test
    void createSighting_whenUserNotFound_returnsNotFound() throws Exception {
        Long userId = 1L;
        String token = userId + "123456";

        SightingCreateDTO dto = new SightingCreateDTO(
                1L,
                -34.6037f,
                -58.3816f,
                "photoBase64",
                LocalDate.now(),
                "Comentario"
        );

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/sightings")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }

    @Test
    void createSighting_whenPetNotFound_returnsNotFound() throws Exception {
        Long userId = 1L;
        String token = userId + "123456";
        Long petId = 999L;

        SightingCreateDTO dto = new SightingCreateDTO(
                petId,
                -34.6037f,
                -58.3816f,
                "photoBase64",
                LocalDate.now(),
                "Comentario"
        );

        User reporter = buildUser("Ivana", "Harari", "iharari@info.unlp.edu.ar");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(reporter));
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/sightings")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mascota no encontrada"));
    }

    @Test
    void createSighting_whenValidData_returnsCreated() throws Exception {
        Long userId = 1L;
        String token = userId + "123456";
        Long petId = 1L;

        SightingCreateDTO dto = new SightingCreateDTO(
                petId,
                -34.6037f,
                -58.3816f,
                "photoBase64",
                LocalDate.now(),
                "Vi la mascota en el parque"
        );

        User reporter = buildUser("Alejandra Beatriz", "Lliteras", "lliteras@info.unlp.edu.ar");
        User owner = buildUser("Ricardo Marcelo", "Naiouf", "naiouf@info.unlp.edu.ar");
        Pet pet = buildPet("Pelusa", owner);
        Sighting savedSighting = buildSighting(pet, reporter, dto.date());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(reporter));
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(sightingRepository.save(any(Sighting.class))).thenReturn(savedSighting);

        mockMvc.perform(post("/sightings")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.petName").value("Pelusa"))
                .andExpect(jsonPath("$.reporterName").value("Alejandra Beatriz"));
    }

    @Test
    void getSightingById_whenSightingDoesNotExist_returnsNotFound() throws Exception {
        Long sightingId = 999L;

        when(sightingRepository.findById(sightingId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/sightings/" + sightingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Avistamiento no encontrado"));
    }

    @Test
    void getSightingById_whenSightingExists_returnsOk() throws Exception {
        Long sightingId = 1L;
        User owner = buildUser("Cecilia Verónica", "Sanz", "csanz@info.unlp.edu.ar");
        User reporter = buildUser("Pablo Javier", "Thomas", "pthomas@info.unlp.edu.ar");
        Pet pet = buildPet("Manchita", owner);
        Sighting sighting = buildSighting(pet, reporter, LocalDate.now());

        when(sightingRepository.findById(sightingId)).thenReturn(Optional.of(sighting));

        mockMvc.perform(get("/sightings/" + sightingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.petName").value("Manchita"))
                .andExpect(jsonPath("$.reporterName").value("Pablo Javier"));
    }

    private User buildUser(String nombre, String apellidos, String email) {
        return User.builder()
                .nombre(nombre)
                .apellidos(apellidos)
                .email(email)
                .contrasena("1234")
                .telefono("123456789")
                .ciudad("La Plata")
                .barrio("Centro")
                .latitud(-34.6037f)
                .longitud(-58.3816f)
                .build();
    }

    private Pet buildPet(String nombre, User creador) {
        return Pet.builder()
                .nombre(nombre)
                .tamano("Mediano")
                .descripcion("Mascota de prueba")
                .color("Marrón")
                .raza("Mestizo")
                .peso(10.0f)
                .latitud(-34.6037f)
                .longitud(-58.3816f)
                .fechaPerdida(LocalDate.now())
                .estado(Pet.State.PERDIDO_PROPIO)
                .tipo(Pet.Type.PERRO)
                .agregarFoto("fotoBase64Test")
                .creador(creador)
                .build();
    }

    private Sighting buildSighting(Pet pet, User reporter, LocalDate date) {
        return Sighting.builder()
                .mascota(pet)
                .reportador(reporter)
                .latitud(-34.6037f)
                .longitud(-58.3816f)
                .fotoBase64("fotoBase64Test")
                .fecha(date)
                .comentario("Avistamiento de prueba")
                .build();
    }
}