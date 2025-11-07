package io.github.vicen621.volveacasa.controllers;

import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import io.github.vicen621.volveacasa.persistence.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="/authentication", produces=MediaType.APPLICATION_JSON_VALUE, name="AuthController")
public class AuthController {
    private UsuarioDAO userDAO;

    @Autowired
    public AuthController(UsuarioDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PostMapping
    public ResponseEntity<Map<String,String>> authenticateUser(@RequestHeader("usuario")String mail, @RequestHeader("clave")String password) {
        Usuario user = userDAO.getByEmail(mail);
        Map<String, String> response = new HashMap<>();
        if (user == null) {
            response.put("error", "Usuario no encontrado");
            response.put("message", "El correo ingresado no corresponde a ningún usuario registrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if (!user.checkPassword(password) || !user.isHabilitado()) {
            response.put("error", "Acceso denegado");
            response.put("message", "credenciales incorrectas o usuario deshabilitado");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        response.put("message", "Autenticación exitosa");
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", user.getId() + "1233456");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
