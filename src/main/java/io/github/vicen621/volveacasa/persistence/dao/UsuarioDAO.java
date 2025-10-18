package io.github.vicen621.volveacasa.persistence.dao;

import io.github.vicen621.volveacasa.entities.Usuario;

public interface UsuarioDAO extends GenericDAO<Usuario> {
    Usuario getByEmail(String mail);
    void disableUser(Usuario usuario);
    void disableUser(Long id);
}
