package io.github.grupo01.volve_a_casa.controllers.interfaces;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.security.UserAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Mascotas", description = "API para gestión de mascotas")
public interface IPetController {

    @Operation(summary = "Crear mascota", description = "Permite crear una nueva mascota asociada al usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mascota creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    ResponseEntity<?> createPet(
            User requester,
            PetCreateDTO dto
    );

    @Operation(summary = "Actualizar mascota", description = "Actualiza los datos de una mascota creada por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    ResponseEntity<?> updatePet(
            User requester,
            @Parameter(description = "ID de la mascota", required = true) Long id,
            PetUpdateDTO dto
    );

    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota creada por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    ResponseEntity<?> deletePet(
            User requester,
            @Parameter(description = "ID de la mascota", required = true) Long id
    );

    @Operation(summary = "Listar mascotas perdidas", description = "Obtiene todas las mascotas marcadas como perdidas (estados: PERDIDO_PROPIO o PERDIDO_AJENO). "
            +
            "Tests: PetControllerTest.listAllLostPets_whenEmpty_returnsNoContent(), " +
            "PetControllerTest.listAllLostPets_whenPetsExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas perdidas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No hay mascotas perdidas registradas")
    })
    ResponseEntity<?> listAllLostPets();

    @Operation(summary = "Obtener mascota por ID", description = "Obtiene los detalles de una mascota específica mediante su ID. "
            +
            "Tests: PetControllerTest.getPetById_whenPetExists_returnsOk(), " +
            "PetControllerTest.getPetById_whenPetDoesNotExist_returnsNotFound()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    ResponseEntity<?> getPetById(
            @Parameter(description = "ID de la mascota", required = true, example = "1") Long id
    );

    @Operation(summary = "Listar todas las mascotas", description = "Obtiene todas las mascotas registradas en el sistema, sin importar su estado. "
            +
            "Tests: PetControllerTest.listAllPets_whenEmpty_returnsNoContent(), " +
            "PetControllerTest.listAllPets_whenPetsExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No hay mascotas registradas")
    })
    ResponseEntity<?> listAllPets();

    @Operation(summary = "Obtener avistamientos por mascota", description = "Obtiene todos los avistamientos de una mascota específica. "
            +
            "Tests: PetControllerTest.listAllSightings_WhenEmpty_returnsNoContent(), " +
            "PetControllerTest.listAllSightings_whenSightingExists_returnsOkAndList(), ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avistamientos de la mascota obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SightingResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "La mascota no tiene avistamientos registrados"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Mascota no encontrada\", \"message\": \"No existe una mascota con el ID proporcionado\"}")))
    })
    ResponseEntity<?> listAllSightings(
            @Parameter(description = "ID de la mascota", required = true, example = "1") Long id
    );

    @Operation(summary = "Listar mis mascotas", description = "Obtiene todas las mascotas creadas por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    ResponseEntity<?> getMyPets(
            User requester
    );
}
