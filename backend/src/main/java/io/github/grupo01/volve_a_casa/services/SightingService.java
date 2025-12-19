package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.openstreet.GeorefResponse;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.SightingRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SightingService {

    private final SightingRepository sightingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PetService petService;
    private final EmailService emailService;
    private final GeorefService georefService;
    private final TelegramNotificationService telegramNotificationService;

    public SightingService(SightingRepository sightingRepository, UserRepository userRepository, 
                           UserService userService, PetService petService, 
                           EmailService emailService, GeorefService georefService, 
                           TelegramNotificationService telegramNotificationService) {
        this.sightingRepository = sightingRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.petService = petService;
        this.emailService = emailService;
        this.georefService = georefService;
        this.telegramNotificationService = telegramNotificationService;
    }

    // TODO: Test de integracion
    public Sighting findById(long id) {
        return sightingRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sighting with id " + id + " not found"));
    }

    // TODO: Test de integracion
    public List<SightingResponseDTO> findAll(Sort sort) {
        return sightingRepository.findAll(sort).stream()
                .map(SightingResponseDTO::fromSighting)
                .toList();
    }

    public List<SightingResponseDTO> findByPetId(Long petId) {
        return sightingRepository.findByPetIdOrderByDateDesc(petId).stream()
                .map(SightingResponseDTO::fromSighting)
                .toList();
    }

    public SightingResponseDTO createSighting(User creator, SightingCreateDTO dto) {
        Pet pet = petService.findById(dto.petId());

        Sighting newSighting = new Sighting(
                creator,
                pet,
                dto.latitude(),
                dto.longitude(),
                dto.photoBase64(),
                dto.comment(),
                dto.date()
        );
        Sighting savedSighting = sightingRepository.save(newSighting);
        
        // Incrementar puntos del usuario que reportó el avistamiento
        creator.setPoints(creator.getPoints() + 10);
        userRepository.save(creator);
        
        // Enviar email al dueño de la mascota
        sendSightingNotificationEmail(savedSighting, creator);
        
        // Enviar notificaciones de Telegram a suscriptores
        telegramNotificationService.notificarAvistamiento(savedSighting);
        
        return SightingResponseDTO.fromSighting(savedSighting);
    }
    
    private void sendSightingNotificationEmail(Sighting sighting, User reporter) {
        User owner = sighting.getPet().getCreator();
        
        // Obtener ubicación legible
        GeorefResponse georef = georefService.getUbication(
                sighting.getCoordinates().getLatitude(),
                sighting.getCoordinates().getLongitude()
        );
        
        String city = georef != null && georef.ubicacion() != null && georef.ubicacion().municipio() != null
                ? georef.ubicacion().municipio().nombre()
                : "Desconocida";
        
        String neighborhood = georef != null && georef.ubicacion() != null && georef.ubicacion().departamento() != null
                ? georef.ubicacion().departamento().nombre()
                : "Desconocido";
        
        // Construir el email
        String subject = "¡Avistamiento de " + sighting.getPet().getName() + "!";
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = sighting.getDate().format(formatter);
        
        String body = String.format(
                "Hola %s,\n\n" +
                "Te informamos que %s %s ha reportado un avistamiento de tu mascota %s.\n\n" +
                "Detalles del avistamiento:\n" +
                "- Fecha: %s\n" +
                "- Ubicación: %s, %s\n" +
                "- Coordenadas: %.6f, %.6f\n" +
                "- Comentario: %s\n\n" +
                "Contacto del reportero:\n" +
                "- Teléfono: %s\n" +
                "- Email: %s\n\n" +
                "¡Esperamos que puedas encontrar a tu mascota pronto!\n\n" +
                "Saludos,\n" +
                "Equipo Volvé a Casa",
                owner.getName(),
                reporter.getName(),
                reporter.getLastName(),
                sighting.getPet().getName(),
                formattedDate,
                city,
                neighborhood,
                sighting.getCoordinates().getLatitude(),
                sighting.getCoordinates().getLongitude(),
                sighting.getComment() != null ? sighting.getComment() : "Sin comentarios",
                reporter.getPhone(),
                reporter.getEmail()
        );
        
        try {
            emailService.sendEmail(owner.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Error enviando email de notificación: " + e.getMessage());
        }
    }
}
