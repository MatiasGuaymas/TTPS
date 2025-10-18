package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class UsuarioDAOHibernate extends GenericDAOHibernate<Usuario> implements UsuarioDAO {

    public UsuarioDAOHibernate() {
        super(Usuario.class);
    }

    @Override
    public Usuario getByEmail(String mail) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e.email = :email";
            TypedQuery<Usuario> query = em.createQuery(jpql, getEntityClass());
            query.setParameter("email", mail);
            return query.getSingleResult();
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
