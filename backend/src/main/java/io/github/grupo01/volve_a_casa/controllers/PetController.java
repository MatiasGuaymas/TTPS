package io.github.grupo01.volve_a_casa.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.interfaces.IPetController;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.services.PetService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/pets", produces = MediaType.APPLICATION_JSON_VALUE, name = "PetRestController")
public class PetController implements IPetController {
    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable("id") Long id) {
        Pet pet = petService.findById(id);
        return ResponseEntity.ok(PetResponseDTO.fromPet(pet));
    }

    @Override
    @PostMapping
    public ResponseEntity<?> createPet(@AuthenticationPrincipal User requester, @Valid @RequestBody PetCreateDTO dto) {
        PetResponseDTO response = petService.createPet(requester, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@AuthenticationPrincipal User requester, @PathVariable Long id, @Valid @RequestBody PetUpdateDTO updatedData) {
        PetResponseDTO user = petService.updatePet(id, requester, updatedData);
        return ResponseEntity.ok(user);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@AuthenticationPrincipal User requester, @PathVariable Long id) {
        petService.deletePet(id, requester);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/lost")
    public ResponseEntity<?> listAllLostPets() {
        List<PetResponseDTO> lostPets = petService.findAllLostPets();

        if (lostPets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lostPets);
    }

    @Override
    @GetMapping
    public ResponseEntity<?> listAllPets() {
        List<PetResponseDTO> pets = petService.findAll(Sort.by(Sort.Direction.DESC, "lostDate"));

        if (pets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(pets);
    }

    @Override
    @GetMapping("/{id}/sightings")
    public ResponseEntity<?> listAllSightings(@PathVariable Long id) {
        List<SightingResponseDTO> sightings = petService.getPetSightings(id);

        if (sightings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(sightings, HttpStatus.OK);
    }

    @Override
    @GetMapping("/my_pets")
    public ResponseEntity<?> getMyPets(@AuthenticationPrincipal User requester) {
        List<PetResponseDTO> pets = petService.getPetByCreator(requester);

        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(pets);
    }
}

