package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.openstreet.GeorefResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SightingServiceTest {
    @Mock
    SightingRepository sightingRepository;

    @Mock
    UserService userService;

    @Mock
    PetService petService;

    @Mock
    GeorefService georefService;

    @Mock
    EmailService emailService;

    @Mock
    TelegramNotificationService telegramNotificationService;

    @InjectMocks
    SightingService sightingService;

    @Test
    public void createSighting_success() {
        long userId = 1L;
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn("test@gmail.com");

        long petId = 1L;
        Pet pet = mock(Pet.class);
        when(petService.findById(petId)).thenReturn(pet);
        when(pet.getId()).thenReturn(petId);
        when(pet.getCreator()).thenReturn(user);

        when(georefService.getUbication(-54f, -27f)).thenReturn(new GeorefResponse(
                new GeorefResponse.Ubicacion(
                        new GeorefResponse.Entidad(
                                "1",
                                "Partido de La Plata"
                        ),
                        new GeorefResponse.Entidad(
                                "1",
                                "Partido de La Plata"
                        ),
                        new GeorefResponse.Entidad(
                                "2",
                                "Buenos Aires"
                        ),
                        -54f,
                        -27f
                )
        ));

        doNothing().when(emailService).sendEmail(any(String.class), any(String.class), any(String.class));
        doNothing().when(telegramNotificationService).notificarAvistamiento(any(Sighting.class));

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
        SightingResponseDTO response = sightingService.createSighting(user, dto);
        assertEquals(petId, response.petId());
        assertEquals(userId, response.reporterId());
        verify(sightingRepository, times(1)).save(any(Sighting.class));
    }
}