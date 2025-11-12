package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping(value="/pets", produces= MediaType.APPLICATION_JSON_VALUE, name="PetRestController")
public class PetController {
    private final PetRepository petRepository;

    @Autowired
    public PetController(PetRepository petRepository) { this.petRepository = petRepository; }

    @GetMapping("/lost")
    public ResponseEntity<List<Pet>> listAllLostPets() {
        List<Pet> lostPets = petRepository.findAllLostPets();

        if (lostPets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(lostPets, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();

        Optional<Pet> petOptional = petRepository.findById(id);
        if (petOptional.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            response.put("message", "No se encontró una mascota con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(petOptional.get());
    }

    @GetMapping
    public ResponseEntity<List<Pet>> listAllPets() {
        List<Pet> pets = petRepository.findAll();
        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }
}
