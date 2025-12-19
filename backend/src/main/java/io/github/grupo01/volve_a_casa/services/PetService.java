package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.openstreet.GeorefResponse;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetDetailDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetSummaryDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.persistence.Specifications;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.filters.PetFilter;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// TODO: Testear
@Service
public class PetService {
    private final PetRepository petRepository;
    private final GeorefService georefService;

    public PetService(PetRepository petRepository, GeorefService georefService) {
        this.petRepository = petRepository;
        this.georefService = georefService;
    }

    // TODO: Test de integracion
    public Pet findById(long id) {
        return petRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet with id " + id + " not found"));
    }

    public List<PetResponseDTO> getPetByCreator(User creator) {
        return petRepository
                .findAllByCreator(creator)
                .stream()
                .map(PetResponseDTO::fromPet)
                .toList();
    }

    // TODO: Test de integracion
    public List<PetResponseDTO> findAllLostPets() {
        return petRepository.findAllByStateInOrderByLostDate(Pet.State.PERDIDO_PROPIO, Pet.State.PERDIDO_AJENO)
                .stream()
                .map(PetResponseDTO::fromPet)
                .toList();
    }

    // TODO: Test de integracion
    public List<PetResponseDTO> findAll(PetFilter filter, Pageable pageable) {
        Specification<Pet> specification = Specifications.getPetSpecification(filter);

        return petRepository.findAll(specification, pageable)
                .stream()
                .map(PetResponseDTO::fromPet)
                .toList();
    }

    public PetResponseDTO createPet(User creator, PetCreateDTO dto) {
        Pet newPet = new Pet(
                dto.name(),
                dto.size(),
                dto.description(),
                dto.color(),
                dto.race(),
                dto.weight(),
                dto.latitude(),
                dto.longitude(),
                dto.type(),
                dto.state(),
                creator,
                dto.photoBase64()
        );
        return PetResponseDTO.fromPet(petRepository.save(newPet));
    }

    public PetResponseDTO updatePet(long petId, User creator, PetUpdateDTO dto) {
        Pet pet = this.findById(petId);

        if (!pet.getCreator().equals(creator)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar esta mascota");
        }

        pet.updateFromDTO(dto);
        Pet savedPet = petRepository.save(pet);
        return PetResponseDTO.fromPet(savedPet);
    }

    public void deletePet(long petId, User creator) {
        Pet pet = this.findById(petId);
        if (!pet.getCreator().equals(creator)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar esta mascota");
        }

        petRepository.delete(pet);
    }

    // TODO: Test de integracion
    public List<SightingResponseDTO> getPetSightings(long petId) {
        Pet pet = this.findById(petId);
        return pet.getSightings().stream()
                .map(SightingResponseDTO::fromSighting)
                .toList();
    }

    public List<PetSummaryDTO> getAllLostPetsSummary() {
        return petRepository.findAllByStateInOrderByLostDate(Pet.State.PERDIDO_PROPIO, Pet.State.PERDIDO_AJENO)
                .stream()
                .map(PetSummaryDTO::fromPet)
                .toList();
    }

    @Transactional(readOnly = true)
    public PetDetailDTO getPetDetailForTelegram(Long petId) {
        Pet pet = this.findById(petId);
        
        // Obtener descripción de ubicación usando GeorefService
        String locationDescription = null;
        try {
            GeorefResponse georef = georefService.getUbication(
                pet.getCoordinates().getLatitude(),
                pet.getCoordinates().getLongitude()
            );
            
            if (georef != null && georef.ubicacion() != null) {
                var ubicacion = georef.ubicacion();
                StringBuilder location = new StringBuilder();
                
                if (ubicacion.municipio() != null && ubicacion.municipio().nombre() != null) {
                    location.append(ubicacion.municipio().nombre());
                }
                
                if (ubicacion.provincia() != null && ubicacion.provincia().nombre() != null) {
                    if (location.length() > 0) {
                        location.append(", ");
                    }
                    location.append(ubicacion.provincia().nombre());
                }
                
                locationDescription = location.toString();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener ubicación para mascota " + petId + ": " + e.getMessage());
        }
        
        return PetDetailDTO.fromPet(pet, locationDescription);
    }
}
