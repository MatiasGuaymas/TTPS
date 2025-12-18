package io.github.grupo01.volve_a_casa.controllers.interfaces;

import io.github.grupo01.volve_a_casa.controllers.dto.user.UserResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.filters.UserFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public interface IUserController {

    @Operation(
            summary = "Listar usuarios con filtros y paginación",
            description = "Obtiene una página de usuarios filtrados dinámicamente y ordenados. " +
                    "Soporta paginación (page, size) y ordenamiento múltiple (sort). " +
                    "Tests: UserControllerTest.listAllUsers_..."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de usuarios obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No se encontraron usuarios con los filtros proporcionados"
            )
    })
    ResponseEntity<?> listAllUsers(
            @ParameterObject UserFilter filter,
            @ParameterObject Pageable pageable
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
            User requester,
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
            User requester,
            @Parameter(description = "Datos actualizados del usuario", required = true) UserUpdateDTO updatedData
    );
}
