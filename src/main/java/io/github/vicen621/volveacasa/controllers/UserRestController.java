package io.github.vicen621.volveacasa.controllers;

import io.github.vicen621.volveacasa.persistence.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.github.vicen621.volveacasa.persistence.dao.hibernate.UsuarioDAOHibernate;

import java.util.List;

@RestController
@RequestMapping(name="/users", produces= MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {
    @Autowired
    private UsuarioDAOHibernate userDAO;

    @GetMapping
    public ResponseEntity<List<Usuario>> listAllUsersOrderByName() {
        List<Usuario> users = userDAO.getAll("nombre");
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
