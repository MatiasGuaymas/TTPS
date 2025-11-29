package io.github.grupo01.volve_a_casa.controllers.interfaces;

import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.sighting.SightingResponseDTO;
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

@Tag(name = "Avistamientos", description = "API para gestión de avistamientos de mascotas. Permite reportar y consultar avistamientos.")
public interface ISightingController {

    @Operation(summary = "Listar todos los avistamientos", description = "Obtiene todos los avistamientos ordenados por fecha descendente (más recientes primero). "
            +
            "Tests: SightingControllerTest.listAllSightings_whenEmpty_returnsNoContent(), " +
            "SightingControllerTest.listAllSightings_whenSightingsExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avistamientos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SightingResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No hay avistamientos registrados")
    })
    ResponseEntity<?> listAllSightings();

    @Operation(summary = "Crear un avistamiento", description = "Registra un nuevo avistamiento de una mascota. Requiere token de autenticación. "
            +
            "Tests: SightingControllerTest.createSighting_whenTokenDoesNotEndWith123456_returnsUnauthorized(), " +
            "SightingControllerTest.createSighting_whenUserDoesNotExist_returnsUnauthorized(), " +
            "SightingControllerTest.createSighting_whenDataInvalid_returnsBadRequest(), " +
            "SightingControllerTest.createSighting_whenUserNotFound_returnsNotFound(), " +
            "SightingControllerTest.createSighting_whenPetNotFound_returnsNotFound(), " +
            "SightingControllerTest.createSighting_whenValidData_returnsCreated()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avistamiento creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SightingResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Datos inválidos\", \"message\": \"Faltan campos obligatorios para crear el avistamiento\"}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido o no proporcionado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Token inválido\", \"message\": \"El token proporcionado no es válido\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuario reportador o mascota no encontrados", content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<?> createSighting(
            UserAuthentication requester,
            @Parameter(description = "Datos del avistamiento a crear", required = true) SightingCreateDTO sightingDTO
    );

    @Operation(summary = "Obtener avistamiento por ID", description = "Obtiene los detalles de un avistamiento específico mediante su ID. "
            +
            "Tests: SightingControllerTest.getSightingById_whenSightingDoesNotExist_returnsNotFound(), " +
            "SightingControllerTest.getSightingById_whenSightingExists_returnsOk()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avistamiento encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SightingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Avistamiento no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Avistamiento no encontrado\", \"message\": \"No existe un avistamiento con el ID proporcionado\"}")))
    })
    ResponseEntity<?> getSightingById(
            @Parameter(description = "ID del avistamiento", required = true, example = "1") Long id
    );
}
