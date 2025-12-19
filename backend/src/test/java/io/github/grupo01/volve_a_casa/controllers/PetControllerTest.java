package io.github.grupo01.volve_a_casa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.exceptions.GlobalExceptionHandler;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.filters.PetFilter;
import io.github.grupo01.volve_a_casa.services.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PetControllerTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User dummyUser;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        dummyUser = mock(User.class);

        mockMvc = MockMvcBuilders.standaloneSetup(petController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        putPrincipalResolver
                )
                .build();
    }

    private final HandlerMethodArgumentResolver putPrincipalResolver = new HandlerMethodArgumentResolver() {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            // If the controller argument is of type User, this resolver is activated
            return parameter.getParameterType().isAssignableFrom(User.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            // Always returns our dummy user
            return dummyUser;
        }
    };

    @Test
    void createPet_whenValidData_returnsCreated() throws Exception {
        long petId = 1L;

        PetCreateDTO dto = samplePetCreateDTO();

        when(petService.createPet(dummyUser, dto)).thenReturn(createPetResponse(petId, 1L, "Tobby"));

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(petId))
                .andExpect(jsonPath("$.name").value("Tobby"))
                .andExpect(jsonPath("$.creatorId").value(1L));
    }

    @Test
    void updatePet_returnsOk() throws Exception {
        long petId = 1L;

        PetUpdateDTO dto = new PetUpdateDTO(
                "Pepe",
                "Nueva descripción",
                "Nuevo color",
                Pet.Size.PEQUENO,
                "Poodle",
                10.0f,
                Pet.Type.PERRO,
                Pet.State.PERDIDO_PROPIO,
                -34.6f,
                -58.4f
        );

        PetResponseDTO petResponseDTO = createPetResponse(1L, 1L, "Pepe");

        when(petService.updatePet(petId, dummyUser, dto)).thenReturn(petResponseDTO);

        mockMvc.perform(put("/api/pets/" + petId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pepe"));
    }

    @Test
    void deletePet_returnsOk() throws Exception {
        long petId = 1L;

        doNothing().when(petService).deletePet(petId, dummyUser);

        mockMvc.perform(delete("/api/pets/" + petId))
                .andExpect(status().isNoContent());
    }


    @Test
    void listAllLostPets_whenEmpty_returnsNoContent() throws Exception {
        when(petService.findAllLostPets()).thenReturn(List.of());

        mockMvc.perform(get("/api/pets/lost")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllLostPets_whenPetsExist_returnsOkAndList() throws Exception {
        PetResponseDTO pet1 = createPetResponse(1L, 1L, "Canela");
        PetResponseDTO pet2 = createPetResponse(2L, 1L, "Mishi");

        when(petService.findAllLostPets()).thenReturn(List.of(pet1, pet2));

        mockMvc.perform(get("/api/pets/lost")
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
                Pet.Size.PEQUENO,
                "test",
                "test",
                "test",
                10.0f,
                -54.23f,
                -23.12f,
                Pet.Type.PERRO,
                Pet.State.PERDIDO_PROPIO,
                user,
                "test"
        );

        when(petService.findById(petId)).thenReturn(pet);

        mockMvc.perform(get("/api/pets/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bigotes"));
    }

    @Test
    void getPetById_whenPetDoesNotExist_returnsNotFound() throws Exception {
        long petId = 999L;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada")).when(petService).findById(petId);

        mockMvc.perform(get("/api/pets/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mascota no encontrada"));
    }

    @Test
    void listAllPets_whenEmpty_returnsNoContent() throws Exception {
        when(petService.findAll(any(PetFilter.class), any(Pageable.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllPets_whenPetsExist_returnsOkAndList() throws Exception {
        PetResponseDTO pet1 = createPetResponse(1L, 1L, "Orejas");
        PetResponseDTO pet2 = createPetResponse(2L, 1L, "Negrito");
        PetResponseDTO pet3 = createPetResponse(3L, 2L, "Rocky");

        when(petService.findAll(any(PetFilter.class), any(Pageable.class))).thenReturn(List.of(pet1, pet2, pet3));

        mockMvc.perform(get("/api/pets")
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

        mockMvc.perform(get("/api/pets/" + petId + "/sightings")
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

        mockMvc.perform(get("/api/pets/1/sightings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[2].id").value(3L));
    }

    @Test
    void getMyPets_whenPetExists_returnsOkAndList() throws Exception {
        PetResponseDTO pet1 = createPetResponse(1L, 1L, "Bigotes");
        PetResponseDTO pet2 = createPetResponse(3L, 1L, "Firulais");
        PetResponseDTO pet3 = createPetResponse(8L, 1L, "Misha");

        when(petService.getPetByCreator(dummyUser)).thenReturn(List.of(pet1, pet2, pet3));

        mockMvc.perform(get("/api/pets/my_pets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bigotes"))
                .andExpect(jsonPath("$[1].name").value("Firulais"))
                .andExpect(jsonPath("$[2].name").value("Misha"));
    }

    private PetCreateDTO samplePetCreateDTO() {
        return new PetCreateDTO(
                "Tobby",
                Pet.Size.MEDIANO,
                "Perro marrón con manchas",
                "Marrón",
                "Labrador",
                12.5f,
                -34.6f,
                -58.4f,
                Pet.State.PERDIDO_PROPIO,
                Pet.Type.PERRO,
                "photo_base64_placeholder"
        );
    }

    private PetResponseDTO createPetResponse(long id, long creatorId, String name) {
        return new PetResponseDTO(
                id,
                name,
                Pet.Size.MEDIANO,
                "perro mediano",
                "color",
                "race",
                10.0f,
                -54.21f,
                -23.128f,
                LocalDate.now(),
                Pet.State.PERDIDO_PROPIO,
                Pet.Type.PERRO,
                creatorId,
                List.of("photo_url")
        );
    }

    private SightingResponseDTO createSightingResponse(long id, long petId, long reporterId) {
        return new SightingResponseDTO(
                id,
                petId,
                reporterId,
                "Juan",
                "Pérez",
                -54f,
                -21f,
                LocalDate.now(),
                "comentario",
                "photo"
        );
    }
}
