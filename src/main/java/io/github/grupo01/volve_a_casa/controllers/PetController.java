package io.github.grupo01.volve_a_casa.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.grupo01.volve_a_casa.controllers.dto.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;

@RestController
@RequestMapping(value="/pets", produces=MediaType.APPLICATION_JSON_VALUE, name="PetRestController")
public class PetController{

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Autowired
    public PetController(PetRepository petRepository, UserRepository userRepository){
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPet(@RequestHeader("token")String token, @RequestBody PetCreateDTO dto) {
        Map<String, Object> response = new HashMap<>();

        if (!dto.isValid()) {
            response.put("error", "Datos invalidos");
            response.put("message", "Faltan campos obligatorios.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<User> optionalUser = getUserFromToken(token);
        if (optionalUser.isEmpty()) {
            response.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User creator = optionalUser.get();

        try {
            Pet newPet = Pet.builder()
                    .nombre(dto.name())
                    .tamano(dto.size())
                    .descripcion(dto.description())
                    .color(dto.color())
                    .raza(dto.race())
                    .peso(dto.weight())
                    .latitud(dto.latitude())
                    .longitud(dto.longitude())
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(dto.type())
                    .creador(creator)
                    .agregarFoto("foto_default_base64")
                    .build();
            petRepository.save(newPet);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPet);

        } catch (Exception e) {
            response.put("error", "Error al crear mascota");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody PetUpdateDTO dto) {
        Map<String, String> response = new HashMap<>();
        Optional<User> optionalUser = getUserFromToken(token);
        if (optionalUser.isEmpty()) {
            response.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Pet pet = optionalPet.get();
        if (!pet.getCreator().equals(optionalUser.get())) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        pet.updateFromDTO(dto);

        petRepository.save(pet);
        return ResponseEntity.ok(pet);
    }


    @GetMapping("/mine")
    public ResponseEntity<?> getMyPets(@RequestHeader("token") String token) {
        Map<String, String> response = new HashMap<>();
        Optional<User> optionalUser = getUserFromToken(token);
        if (optionalUser.isEmpty()) {
            response.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        User user = optionalUser.get();
        return ResponseEntity.ok(user.getCreatedPets());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@RequestHeader("token") String token, @PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        Optional<User> optionalUser = getUserFromToken(token);
        if (optionalUser.isEmpty()) {
            response.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Pet pet = optionalPet.get();
        if (!pet.getCreator().equals(optionalUser.get())) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        petRepository.delete(pet);
        response.put("message", "Mascota eliminada correctamente");
        return ResponseEntity.ok(response);
    }

    private Optional<User> getUserFromToken(String token) {
        if (token == null || !token.endsWith("123456"))
            return Optional.empty();
        try {
            Long id = Long.valueOf(token.replace("123456", ""));
            return userRepository.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


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



