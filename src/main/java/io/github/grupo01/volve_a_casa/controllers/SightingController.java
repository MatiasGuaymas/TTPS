package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.SightingRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/sightings", produces= MediaType.APPLICATION_JSON_VALUE, name="SightingRestController")
public class SightingController {
    private final SightingRepository sightingRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Autowired
    public SightingController(SightingRepository sightingRepository, PetRepository petRepository, UserRepository userRepository) {
        this.sightingRepository = sightingRepository; this.petRepository = petRepository; this.userRepository = userRepository; }

    @GetMapping
    public ResponseEntity<List<SightingResponseDTO>> listAllSightings() {
        List<Sighting> sightings = sightingRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));

        if (sightings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<SightingResponseDTO> response = sightings.stream()
                .map(SightingResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getSightingsByPetId(@PathVariable("petId") Long petId) {
        Map<String, String> response = new HashMap<>();

        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            response.put("message", "No existe una mascota con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Pet pet = petOptional.get();
        List<Sighting> sightings = pet.getSightings();

        if (sightings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<SightingResponseDTO> responseDTO = sightings.stream()
                .map(SightingResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createSighting(@RequestHeader("token") String token, @RequestBody SightingCreateDTO sightingDTO) {

        Map<String, String> response = new HashMap<>();
        if (!checkToken(token)) {
            response.put("error", "Token inválido");
            response.put("message", "El token proporcionado no es válido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (!sightingDTO.isValid()) {
            response.put("error", "Datos inválidos");
            response.put("message", "Faltan campos obligatorios para crear el avistamiento");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Long userId = Long.valueOf(token.replace("123456", ""));
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            response.put("error", "Usuario no encontrado");
            response.put("message", "El usuario reportador no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<Pet> petOptional = petRepository.findById(sightingDTO.petId());
        if (petOptional.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            response.put("message", "No existe una mascota con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User reporter = userOptional.get();
        Pet pet = petOptional.get();

        Sighting sighting = Sighting.builder()
                .mascota(pet)
                .reportador(reporter)
                .latitud(sightingDTO.latitude())
                .longitud(sightingDTO.longitude())
                .fotoBase64(sightingDTO.photoBase64())
                .fecha(sightingDTO.date())
                .comentario(sightingDTO.comment() != null ? sightingDTO.comment() : "")
                .build();

        Sighting savedSighting = sightingRepository.save(sighting);
        SightingResponseDTO responseDTO = SightingResponseDTO.fromEntity(savedSighting);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSightingById(@PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();

        Optional<Sighting> sightingOptional = sightingRepository.findById(id);
        if (sightingOptional.isEmpty()) {
            response.put("error", "Avistamiento no encontrado");
            response.put("message", "No existe un avistamiento con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        SightingResponseDTO responseDTO = SightingResponseDTO.fromEntity(sightingOptional.get());
        return ResponseEntity.ok(responseDTO);
    }

    private boolean checkToken(String token) {
        return token != null &&
                token.endsWith("123456") &&
                userRepository.existsById(Long.valueOf(token.replace("123456", "")));
    }
}
