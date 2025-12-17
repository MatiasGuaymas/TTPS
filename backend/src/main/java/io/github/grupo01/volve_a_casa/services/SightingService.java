package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.SightingRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SightingService {

    private final SightingRepository sightingRepository;
    private final UserService userService;
    private final PetService petService;

    public SightingService(SightingRepository sightingRepository, UserService userService, PetService petService) {
        this.sightingRepository = sightingRepository;
        this.userService = userService;
        this.petService = petService;
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
        return SightingResponseDTO.fromSighting(sightingRepository.save(newSighting));
    }
}
