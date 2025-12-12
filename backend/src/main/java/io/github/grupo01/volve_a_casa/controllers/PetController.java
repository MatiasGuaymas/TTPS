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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.interfaces.IPetController;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.security.TokenValidator;
import io.github.grupo01.volve_a_casa.services.PetService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/pets", produces = MediaType.APPLICATION_JSON_VALUE, name = "PetRestController")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE},allowedHeaders = {"Content-Type", "token"})
public class PetController implements IPetController {

    private final TokenValidator tokenValidator;
    private final PetService petService;

    @Autowired
    public PetController(TokenValidator tokenValidator, PetService petService) {
        this.tokenValidator = tokenValidator;
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
    public ResponseEntity<?> createPet(@RequestHeader("token") String token, @Valid @RequestBody PetCreateDTO dto) {
        //lo comento porque todavia no hay tal token
        //tokenValidator.validate(token);
        //PetResponseDTO response = petService.createPet(tokenValidator.extractUserId(token), dto);
        Long fixedCreatorId = 1L;
        PetResponseDTO response = petService.createPet(fixedCreatorId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@RequestHeader("token") String token, @PathVariable Long id, @Valid @RequestBody PetUpdateDTO updatedData) {
        tokenValidator.validate(token);
        PetResponseDTO user = petService.updatePet(id, tokenValidator.extractUserId(token), updatedData);
        return ResponseEntity.ok(user);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@RequestHeader("token") String token, @PathVariable Long id) {
        tokenValidator.validate(token);
        petService.deletePet(id, tokenValidator.extractUserId(token));
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



