package io.github.grupo01.volve_a_casa.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {
    @Mock
    PetRepository petRepository;

    @Mock
    UserService userService;

    @InjectMocks
    PetService petService;

    @Test
    public void createPet_success() {
        long userId = 1L;
        User user = mock(User.class);
        when(userService.findById(userId)).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        PetCreateDTO dto = samplePetCreateDTO();
        Pet pet = new Pet(
                dto.name(),
                dto.size(),
                dto.description(),
                dto.color(),
                dto.race(),
                dto.weight(),
                dto.latitude(),
                dto.longitude(),
                dto.type(),
                user,
                "foto_default_base64"
        );

        when(petRepository.save(any(Pet.class))).thenReturn(pet);
        PetResponseDTO response = petService.createPet(userId, dto);
        assertEquals(userId, response.creatorId());
        assertEquals(pet.getName(), response.name());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void updatePet_success() {
        long userId = 1L;
        User user = mock(User.class);
        when(userService.findById(userId)).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        Pet pet = createPet("NombreViejo", user);
        PetUpdateDTO dto = new PetUpdateDTO("NuevoNombre", null, null, null, null, null, null, null, null, null);

        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(petRepository.save(pet)).thenReturn(pet);

        PetResponseDTO response = petService.updatePet(10L, 1L, dto);

        assertEquals("NuevoNombre", pet.getName());
        assertEquals(response.name(), pet.getName());
        verify(petRepository).save(pet);
    }

    @Test
    void updatePet_petNotFound_throws() {
        when(petRepository.findById(10L)).thenReturn(Optional.empty());

        PetUpdateDTO dto = new PetUpdateDTO("NuevoNombre", null, null, null, null, null, null, null, null, null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> petService.updatePet(10L, 1L, dto));

        assertEquals("404 NOT_FOUND \"Pet with id 10 not found\"", ex.getMessage());
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void updatePet_userNotCreator_throws() {
        User user = mock(User.class);

        Pet pet = createPet("NombreViejo", user);
        User otherUser = mock(User.class);

        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(userService.findById(2L)).thenReturn(otherUser);

        PetUpdateDTO dto = new PetUpdateDTO("NuevoNombre", null, null, null, null, null, null, null, null, null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> petService.updatePet(10L, 2L, dto));

        assertTrue(ex.getMessage().contains("No tienes permiso para editar esta mascota"));
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void deletePet_success() {
        long petId = 1L;
        long creatorId = 2L;

        // Mock del usuario creador
        User creator = mock(User.class);

        // Mock de la mascota
        Pet pet = mock(Pet.class);
        when(pet.getCreator()).thenReturn(creator);

        // Mock de los métodos del service
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(userService.findById(creatorId)).thenReturn(creator);

        // Llamada al método
        assertDoesNotThrow(() -> petService.deletePet(petId, creatorId));

        // Verificar que se llamó al repositorio para borrar
        verify(petRepository).delete(pet);
    }

    @Test
    void deletePet_userNotCreator_throws() {
        User user = mock(User.class);

        Pet pet = createPet("Bigotes", user);
        User otherUser = mock(User.class);

        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(userService.findById(2L)).thenReturn(otherUser);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> petService.deletePet(10L, 2L));

        assertTrue(ex.getMessage().contains("No tienes permiso para editar esta mascota"));
        verify(petRepository, never()).delete(any(Pet.class));
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
                Pet.Type.PERRO,
                "photo_base64_placeholder"
        );
    }

    private Pet createPet(String nombre, User user) {
        return new Pet(
                nombre,
                "test",
                "test",
                "test",
                "test",
                10.0f,
                -54f,
                -27f,
                Pet.Type.PERRO,
                user,
                "foto"
        );
    }
}
