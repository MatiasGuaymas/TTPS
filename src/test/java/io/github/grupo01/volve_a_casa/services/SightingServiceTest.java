package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.SightingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SightingServiceTest {
    @Mock
    SightingRepository sightingRepository;

    @Mock
    UserService userService;

    @Mock
    PetService petService;

    @InjectMocks
    SightingService sightingService;

    @Test
    public void createSighting_success() {
        long userId = 1L;
        User user = mock(User.class);
        when(userService.findById(userId)).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        long petId = 1L;
        Pet pet = mock(Pet.class);
        when(petService.findById(petId)).thenReturn(pet);
        when(pet.getId()).thenReturn(petId);

        SightingCreateDTO dto = new SightingCreateDTO(
                petId,
                -54f,
                -27f,
                "foto",
                LocalDate.now(),
                "comment"
        );
        Sighting sighting = new Sighting(
                user,
                pet,
                -54f,
                -27f,
                "foto",
                "comment",
                LocalDate.now()
        );

        when(sightingRepository.save(any(Sighting.class))).thenReturn(sighting);
        SightingResponseDTO response = sightingService.createSighting(userId, dto);
        assertEquals(petId, response.petId());
        assertEquals(userId, response.reporterId());
        verify(sightingRepository, times(1)).save(any(Sighting.class));
    }
}