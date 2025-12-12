package io.github.grupo01.volve_a_casa.controllers.interfaces;


import io.github.grupo01.volve_a_casa.controllers.dto.user.UserLoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Autenticación", description = "API para autenticación de usuarios")
public interface IAuthController {
    @Operation(summary = "Autenticar usuario", description = "Autentica un usuario mediante email y contraseña. Retorna un token en el header si la autenticación es exitosa. "
            +
            "El token generado tiene el formato: {userId}123456 y debe ser usado en los endpoints que requieren autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa - Token incluido en el header 'token'", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Autenticación exitosa\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado - El email no está registrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Usuario no encontrado\", \"message\": \"El correo ingresado no corresponde a ningún usuario registrado\"}"))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Credenciales incorrectas o usuario deshabilitado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Acceso denegado\", \"message\": \"credenciales incorrectas o usuario deshabilitado\"}")))
    })
    @RequestBody(
            description = "Cuerpo con email y contraseña para autenticación",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginDTO.class),
                    examples = @ExampleObject(value = "{\"email\":\"user@example.com\",\"password\":\"P@ssw0rd\"}")
            )
    )
    ResponseEntity<?> authenticateUser(
            UserLoginDTO userLoginDTO
    );
}
