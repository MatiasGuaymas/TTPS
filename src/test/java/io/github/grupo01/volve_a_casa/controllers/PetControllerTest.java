package io.github.grupo01.volve_a_casa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.grupo01.volve_a_casa.controllers.PetController;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.exceptions.GlobalExceptionHandler;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.security.TokenValidator;
import io.github.grupo01.volve_a_casa.services.PetService;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private TokenValidator tokenValidator;

    @InjectMocks
    private PetController petController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(petController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createPet_whenValidDataAndToken_returnsCreated() throws Exception {
        long petId = 1L;
        long userId = 1L;
        String token = tokenFor(userId);

        PetCreateDTO dto = samplePetCreateDTO();

        doNothing().when(tokenValidator).validate(token);
        when(tokenValidator.extractUserId(userId + "123456")).thenReturn(userId);
        when(petService.createPet(userId, dto)).thenReturn(createPetResponse(petId, userId, "Tobby"));

        mockMvc.perform(post("/pets")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(petId))
                .andExpect(jsonPath("$.name").value("Tobby"))
                .andExpect(jsonPath("$.creatorId").value(userId));
    }

    @Test
    void createPet_whenInvalidToken_returnsUnauthorized() throws Exception {
        PetCreateDTO dto = samplePetCreateDTO();

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido"))
                .when(tokenValidator).validate(anyString());

        mockMvc.perform(post("/pets")
                        .header("token", "INVALIDO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token invalido"));
    }

    @Test
    void updatePet_whenTokenValid_updatesPet() throws Exception {
        long petId = 1L;
        long userId = 1L;
        String token = tokenFor(userId);

        PetUpdateDTO dto = new PetUpdateDTO(
                "Pepe",
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

        PetResponseDTO petResponseDTO = createPetResponse(1L, userId, "Pepe");


        doNothing().when(tokenValidator).validate(anyString());
        when(tokenValidator.extractUserId(userId + "123456")).thenReturn(userId);
        when(petService.updatePet(petId, userId, dto)).thenReturn(petResponseDTO);

        mockMvc.perform(put("/pets/" + petId)
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pepe"));
    }

    @Test
    void updatePet_whenTokenInvalid_throwsUnauthorized() throws Exception {
        PetUpdateDTO dto = new PetUpdateDTO(
                "Pepe",
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

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido"))
                .when(tokenValidator).validate(anyString());

        mockMvc.perform(put("/pets/1")
                        .header("token", "Invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token invalido"));
    }

    @Test
    void deletePet_whenTokenValid_returnsOk() throws Exception {
        long petId = 1L;
        long userId = 1L;
        String token = tokenFor(userId);


        doNothing().when(tokenValidator).validate(anyString());
        when(tokenValidator.extractUserId(userId + "123456")).thenReturn(userId);
        doNothing().when(petService).deletePet(petId, userId);

        mockMvc.perform(delete("/pets/" + petId)
                        .header("token", token))
                .andExpect(status().isNoContent());
    }


    @Test
    void listAllLostPets_whenEmpty_returnsNoContent() throws Exception {
        when(petService.findAllLostPets()).thenReturn(List.of());

        mockMvc.perform(get("/pets/lost")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllLostPets_whenPetsExist_returnsOkAndList() throws Exception {
        PetResponseDTO pet1 = createPetResponse(1L, 1L, "Canela");
        PetResponseDTO pet2 = createPetResponse(2L, 1L, "Mishi");

        when(petService.findAllLostPets()).thenReturn(List.of(pet1, pet2));

        mockMvc.perform(get("/pets/lost")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Canela"))
                .andExpect(jsonPath("$[1].name").value("Mishi"));
    }

    @Test
    void getPetById_whenPetExists_returnsOk() throws Exception {
        long petId = 1L;
        User user = new User(
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                -54.23f,
                -23.12f
        );
        Pet pet = new Pet(
                "Bigotes",
                "test",
                "test",
                "test",
                "test",
                10.0f,
                -54.23f,
                -23.12f,
                Pet.Type.PERRO,
                user,
                "test"
        );

        when(petService.findById(petId)).thenReturn(pet);

        mockMvc.perform(get("/pets/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bigotes"));
    }

    @Test
    void getPetById_whenPetDoesNotExist_returnsNotFound() throws Exception {
        long petId = 999L;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada")).when(petService).findById(petId);

        mockMvc.perform(get("/pets/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mascota no encontrada"));
    }

    @Test
    void listAllPets_whenEmpty_returnsNoContent() throws Exception {
        when(petService.findAll(Sort.by(Sort.Direction.DESC, "lostDate"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllPets_whenPetsExist_returnsOkAndList() throws Exception {
        PetResponseDTO pet1 = createPetResponse(1L, 1L, "Orejas");
        PetResponseDTO pet2 = createPetResponse(2L, 1L, "Negrito");
        PetResponseDTO pet3 = createPetResponse(3L, 2L, "Rocky");

        when(petService.findAll(Sort.by(Sort.Direction.DESC, "lostDate"))).thenReturn(List.of(pet1, pet2, pet3));

        mockMvc.perform(get("/pets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Orejas"))
                .andExpect(jsonPath("$[1].name").value("Negrito"))
                .andExpect(jsonPath("$[2].name").value("Rocky"));
    }

    @Test
    void listAllSightings_WhenEmpty_returnsNoContent() throws Exception {
        long petId = 1L;
        when(petService.getPetSightings(petId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets/" + petId + "/sightings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllSightings_whenSightingExists_returnsOkAndList() throws Exception {
        long petId = 1L;
        SightingResponseDTO pet1 = createSightingResponse(1L, petId, 1L);
        SightingResponseDTO pet2 = createSightingResponse(2L, petId, 2L);
        SightingResponseDTO pet3 = createSightingResponse(3L, petId, 2L);

        when(petService.getPetSightings(petId)).thenReturn(List.of(pet1, pet2, pet3));

        mockMvc.perform(get("/pets/1/sightings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[2].id").value(3L));
    }

    private String tokenFor(long userId) {
        return userId + "123456";
    }

    private PetCreateDTO samplePetCreateDTO() {
        return new PetCreateDTO(
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
    }

    private PetResponseDTO createPetResponse(long id, long creatorId, String name) {
        return new PetResponseDTO(
                id,
                name,
                "mediano",
                "perro mediano",
                "color",
                "race",
                10.0f,
                -54.21f,
                -23.128f,
                LocalDate.now(),
                Pet.State.PERDIDO_PROPIO,
                Pet.Type.PERRO,
                creatorId
        );
    }

    private SightingResponseDTO createSightingResponse(long id, long petId, long reporterId) {
        return new SightingResponseDTO(
                id,
                petId,
                reporterId,
                -54f,
                -21f,
                "foto",
                LocalDate.now(),
                "foto"
        );
    }
}