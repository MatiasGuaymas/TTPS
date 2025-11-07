package io.github.vicen621.volveacasa.controllers;

import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import io.github.vicen621.volveacasa.persistence.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/users", produces= MediaType.APPLICATION_JSON_VALUE, name="UserRestController")
public class UserController {
    private final UsuarioDAO userDAO;

    @Autowired
    public UserController(UsuarioDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listAllUsersOrderByName() {
        List<Usuario> users = userDAO.getAll("nombre");
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Usuario> createUser(@RequestBody Usuario user) {
        if (userDAO.get(user.getId()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        userDAO.persist(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/:{id}")
    public ResponseEntity<Map<String, String>> getUserById(@RequestHeader("token") String token, @RequestParam("id") Long id) {
        Map<String, String> response = new HashMap<>();

        if (!checkToken(token)) {
            response.put("error", "Token inválido");
            response.put("message", "El token proporcionado no es válido");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Usuario user = userDAO.get(id);
        if (user == null) {
            response.put("error", "Usuario no encontrado");
            response.put("message", "No existe un usuario con el ID proporcionado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("message", "Usuario encontrado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/:{id}")
    public ResponseEntity<Map<String, String>> updateUser(@RequestHeader("token") String token, @RequestParam("id") Long id, @RequestBody Usuario newUser) {
        Map<String, String> response = new HashMap<>();

        if (!checkToken(token)) {
            response.put("error", "Token inválido");
            response.put("message", "El token proporcionado no es válido");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Usuario existingUser = userDAO.get(id);
        if (existingUser == null) {
            response.put("error", "Usuario no encontrado");
            response.put("message", "No existe un usuario con el ID proporcionado");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        existingUser = newUser;
        userDAO.update(existingUser);
        response.put("message", "Usuario actualizado correctamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private boolean checkToken (String token){
        return token != null && token.endsWith("123456") && userDAO.get(Long.valueOf(token.replace("123456",""))) != null;
    }

}
