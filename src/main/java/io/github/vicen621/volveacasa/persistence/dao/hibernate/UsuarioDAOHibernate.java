package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import jakarta.persistence.EntityManager;

public class UsuarioDAOHibernate extends GenericDAOHibernate<Usuario> implements UsuarioDAO {

    public UsuarioDAOHibernate() {
        super(Usuario.class);
    }

    @Override
    public Usuario getByEmail(String mail) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            return em.find(Usuario.class, mail);
        }
    }

    @Override
    public void disableUser(Usuario usuario) {
        // TODO: Implementar
    }

    @Override
    public void disableUser(Long id) {
        // TODO: Implementar
    }
}
