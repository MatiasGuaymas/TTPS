package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAOHibernate extends GenericDAOHibernate<Usuario> implements UsuarioDAO {

    @Autowired
    public UsuarioDAOHibernate(EntityManager entityManager) {
        super(Usuario.class, entityManager);
    }

    @Override
    public Usuario getByEmail(String mail) {
        try (EntityManager em = this.getEntityManager();) {
            String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e.email = :email";
            TypedQuery<Usuario> query = em.createQuery(jpql, getEntityClass());
            query.setParameter("email", mail);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public void disableUser(Usuario usuario) {
        if (usuario != null) {
            usuario.setHabilitado(false);
            this.update(usuario);
        }
    }

    @Override
    public void disableUser(Long id) {
        Usuario usuario = this.get(id);
        this.disableUser(usuario);
    }

    @Override
    public void enableUser(Usuario usuario) {
        if (usuario != null) {
            usuario.setHabilitado(true);
            this.update(usuario);
        }
    }

    @Override
    public void enableUser(Long id) {
        Usuario usuario = this.get(id);
        this.enableUser(usuario);
    }
}
