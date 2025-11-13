package io.github.grupo01.volve_a_casa.persistence.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    String token = tokenFor(userId);

    User user = buildUser(userId);
    PetCreateDTO dto = samplePetCreateDTO();

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
    PetCreateDTO dto = samplePetCreateDTO();

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
        String token = tokenFor(userId);

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
        String token = tokenFor(userId);
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