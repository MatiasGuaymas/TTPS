package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.interfaces.ISightingController;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.security.TokenValidator;
import io.github.grupo01.volve_a_casa.services.SightingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sightings", produces = MediaType.APPLICATION_JSON_VALUE, name = "SightingRestController")
public class SightingController implements ISightingController {
    private final TokenValidator tokenValidator;
    private final SightingService sightingService;

    @Autowired
    public SightingController(TokenValidator tokenValidator, SightingService sightingService) {
        this.tokenValidator = tokenValidator;
        this.sightingService = sightingService;
    }

    @Override
    @GetMapping
    public ResponseEntity<?> listAllSightings() {
        List<SightingResponseDTO> sightings = sightingService.findAll(Sort.by(Sort.Direction.DESC, "date"));

        if (sightings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(sightings, HttpStatus.OK);
    }

    @Override
    @PostMapping
    public ResponseEntity<?> createSighting(@RequestHeader("token") String token, @Valid @RequestBody SightingCreateDTO sightingDTO) {
        tokenValidator.validate(token);
        SightingResponseDTO sighting = sightingService.createSighting(tokenValidator.extractUserId(token), sightingDTO);
        return new ResponseEntity<>(sighting, HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getSightingById(@PathVariable("id") Long id) {
        Sighting sighting = sightingService.findById(id);
        return ResponseEntity.ok(SightingResponseDTO.fromSighting(sighting));
    }
}
