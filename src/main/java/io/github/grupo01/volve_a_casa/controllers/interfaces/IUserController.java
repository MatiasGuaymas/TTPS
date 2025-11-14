package io.github.grupo01.volve_a_casa.controllers.interfaces;

import io.github.grupo01.volve_a_casa.controllers.dto.user.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public interface IUserController {

    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios ordenados alfabéticamente por nombre. "
            +
            "Tests: UserControllerTest.listAllUsersOrderByName_whenEmpty_returnsNoContent(), " +
            "UserControllerTest.listAllUsersOrderByName_whenUsersExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No hay usuarios registrados")
    })
    ResponseEntity<?> listAllUsersOrderByName();

    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario en el sistema. El email debe ser único. "
            +
            "Tests: UserControllerTest.createUser_whenUserDoesNotExist_returnsCreated(), " +
            "UserControllerTest.createUser_whenUserExists_returnsConflict()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    ResponseEntity<?> createUser(
            @Parameter(description = "Datos del usuario a crear", required = true) UserCreateDTO userCreateDTO
    );

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles de un usuario específico (requiere token de autenticación en formato {userId}123456). "
            +
            "Tests: UserControllerTest.getUserById_whenUserExistsAndTokenValid_returnsOk(), " +
            "UserControllerTest.getUserById_whenTokenInvalid_returnsUnauthorized(), " +
            "UserControllerTest.getUserById_whenUserDoesNotExist_returnsNotFound()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o no proporcionado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    ResponseEntity<?> getUserById(
            @Parameter(description = "Token de autenticación (formato: {userId}123456)", required = true, example = "1123456") String token,
            @Parameter(description = "ID del usuario", required = true, example = "1") Long id
    );

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario. Solo puede actualizar el usuario autenticado mediante token. "
            +
            "Tests: UserControllerTest.updateUser_whenTokenValid_returnsOk()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o no proporcionado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    ResponseEntity<?> updateUser(
            @Parameter(description = "Token de autenticación (formato: {userId}123456)", required = true, example = "1123456") String token,
            @Parameter(description = "Datos actualizados del usuario", required = true) UserUpdateDTO updatedData
    );

    @Operation(summary = "Listar mis mascotas", description = "Obtiene todas las mascotas creadas por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    ResponseEntity<?> getMyPets(
            @Parameter(description = "Token de autenticación", required = true) String token
    );
}
