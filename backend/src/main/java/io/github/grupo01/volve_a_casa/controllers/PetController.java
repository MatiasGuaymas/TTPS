package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.interfaces.IPetController;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.security.UserAuthentication;
import io.github.grupo01.volve_a_casa.services.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> createPet(UserAuthentication requester, @Valid @RequestBody PetCreateDTO dto) {
        PetResponseDTO response = petService.createPet(requester.getPrincipal(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(UserAuthentication requester, @PathVariable Long id, @Valid @RequestBody PetUpdateDTO updatedData) {
        PetResponseDTO user = petService.updatePet(id, requester.getPrincipal(), updatedData);
        return ResponseEntity.ok(user);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(UserAuthentication requester, @PathVariable Long id) {
        petService.deletePet(id, requester.getPrincipal());
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
}



