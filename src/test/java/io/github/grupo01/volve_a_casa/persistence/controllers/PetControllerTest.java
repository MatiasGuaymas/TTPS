package io.github.grupo01.volve_a_casa.persistence.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.grupo01.volve_a_casa.controllers.PetController;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PetControllerTest extends BaseControllerTest {
    @Mock
    private PetRepository petRepository;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeAll
    static void init() {
        createContext();
    }

    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        this.mockMvc = MockMvcBuilders.standaloneSetup(new PetController(petRepository)).build();
    }

    @Test
    void listAllLostPets_whenEmpty_returnsNoContent() throws Exception {
        when(petRepository.findAllLostPets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets/lost")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllLostPets_whenPetsExist_returnsOkAndList() throws Exception {
        User owner = buildUser("Diego", "Torres", "torres@info.unlp.edu.ar");

        Pet pet1 = buildPet("Canela", owner, Pet.State.PERDIDO_PROPIO);
        Pet pet2 = buildPet("Mishi", owner, Pet.State.PERDIDO_AJENO);

        List<Pet> lostPets = Arrays.asList(pet1, pet2);

        when(petRepository.findAllLostPets()).thenReturn(lostPets);

        mockMvc.perform(get("/pets/lost")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Canela"))
                .andExpect(jsonPath("$[1].name").value("Mishi"));
    }

    @Test
    void getPetById_whenPetExists_returnsOk() throws Exception {
        Long petId = 1L;
        User owner = buildUser("Franco", "Chichizola", "chichizola@info.unlp.edu.ar");
        Pet pet = buildPet("Bigotes", owner, Pet.State.PERDIDO_PROPIO);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

        mockMvc.perform(get("/pets/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bigotes"));
    }

    @Test
    void getPetById_whenPetDoesNotExist_returnsNotFound() throws Exception {
        Long petId = 999L;

        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pets/" + petId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mascota no encontrada"));
    }

    @Test
    void listAllPets_whenEmpty_returnsNoContent() throws Exception {
        when(petRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllPets_whenPetsExist_returnsOkAndList() throws Exception {
        User owner = buildUser("Rodolfo Alfredo", "Bertone", "bertone@info.unlp.edu.ar");

        Pet pet1 = buildPet("Orejas", owner, Pet.State.RECUPERADO);
        Pet pet2 = buildPet("Negrito", owner, Pet.State.PERDIDO_PROPIO);
        Pet pet3 = buildPet("Rocky", owner, Pet.State.ADOPTADO);

        List<Pet> pets = Arrays.asList(pet1, pet2, pet3);

        when(petRepository.findAll()).thenReturn(pets);

        mockMvc.perform(get("/pets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Orejas"))
                .andExpect(jsonPath("$[1].name").value("Negrito"))
                .andExpect(jsonPath("$[2].name").value("Rocky"));
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

    private Pet buildPet(String nombre, User creador, Pet.State estado) {
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
                .estado(estado)
                .tipo(Pet.Type.PERRO)
                .agregarFoto("fotoBase64Test")
                .creador(creador)
                .build();
    }
}