package io.github.vicen621.volveacasa.persistence.dao;

import io.github.vicen621.volveacasa.entities.Usuario;

import java.util.List;

public interface UsuarioDAO extends GenericDAO<Usuario> {
    Usuario getByEmail(String mail);
    void disableUser(Usuario usuario);
    void disableUser(Long id);
    void enableUser(Usuario usuario);
    void enableUser(Long id);
    List<Usuario> getByBarrio(String barrio);
    List<Usuario> getByCiudad(String ciudad);
    List<Usuario> getByRol(Usuario.Rol rol);
}
