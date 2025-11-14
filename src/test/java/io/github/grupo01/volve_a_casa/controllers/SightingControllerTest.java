package io.github.grupo01.volve_a_casa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.grupo01.volve_a_casa.controllers.SightingController;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.exceptions.GlobalExceptionHandler;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.security.TokenValidator;
import io.github.grupo01.volve_a_casa.services.SightingService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SightingControllerTest {

    @Mock
    private SightingService sightingService;

    @Mock
    private TokenValidator tokenValidator;

    @InjectMocks
    private SightingController sightingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.standaloneSetup(sightingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listAllSightings_whenEmpty_returnsNoContent() throws Exception {
        when(sightingService.findAll(Sort.by(Sort.Direction.DESC, "date"))).thenReturn(List.of());

        mockMvc.perform(get("/sightings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllSightings_whenSightingsExists_returnsOkAndList() throws Exception {
        SightingResponseDTO pet1 = createSightingResponse(1L, 1L, 1L);
        SightingResponseDTO pet2 = createSightingResponse(2L, 1L, 2L);
        SightingResponseDTO pet3 = createSightingResponse(3L, 2L, 2L);

        when(sightingService.findAll(Sort.by(Sort.Direction.DESC, "date"))).thenReturn(List.of(pet1, pet2, pet3));


        mockMvc.perform(get("/sightings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[2].id").value(3L));
    }

    @Test
    void getSightingById_whenSightingExist_returnsOk() throws Exception {
        long sightingId = 1L;
        Pet pet = mock(Pet.class);
        when(pet.getId()).thenReturn(10L);

        User reporter = mock(User.class);
        when(reporter.getId()).thenReturn(20L);

        Sighting sighting = new Sighting(
                reporter,
                pet,
                -54.23f,
                -27f,
                "foto",
                "comment",
                LocalDate.now()
        );

        when(sightingService.findById(sightingId)).thenReturn(sighting);

        mockMvc.perform(get("/sightings/" + sightingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reporterId").value(20L))
                .andExpect(jsonPath("$.petId").value(10L));
    }

    @Test
    void getSightingById_whenSightingDoesNotExist_returnNotFound() throws Exception {
        long sightingId = 999L;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Avistamiento no encontrada"))
                .when(sightingService).findById(sightingId);

        mockMvc.perform(get("/sightings/" + sightingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Avistamiento no encontrada"));
    }

    @Test
    void createSighting_whenValidDataAndToken_returnsCreated() throws Exception {
        long petId = 1L;
        long userId = 1L;
        String token = userId + "123456";

        SightingCreateDTO dto = new SightingCreateDTO(
                petId,
                -54f,
                -27f,
                "foto",
                LocalDate.now(),
                "comment"
        );

        doNothing().when(tokenValidator).validate(token);
        when(tokenValidator.extractUserId(userId + "123456")).thenReturn(userId);
        when(sightingService.createSighting(userId, dto))
                .thenReturn(createSightingResponse(1L, petId, userId));

        mockMvc.perform(post("/sightings")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.petId").value(petId))
                .andExpect(jsonPath("$.reporterId").value(userId));
    }

    @Test
    void createPet_whenInvalidToken_returnsUnauthorized() throws Exception {
        SightingCreateDTO dto = new SightingCreateDTO(
                1L,
                -54f,
                -27f,
                "foto",
                LocalDate.now(),
                "comment"
        );

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido"))
                .when(tokenValidator).validate(anyString());

        mockMvc.perform(post("/sightings")
                        .header("token", "INVALIDO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token invalido"));
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