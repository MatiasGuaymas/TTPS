package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import io.github.vicen621.volveacasa.persistence.dao.filtros.QueryComponents;
import io.github.vicen621.volveacasa.persistence.dao.filtros.UsuarioFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<Usuario> getAllWithFilter(UsuarioFilter filter) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT e FROM " + getEntityClass().getSimpleName() + " e");
            QueryComponents components = filter.buildQueryComponents();

            if (!components.predicates().isEmpty()) {
                jpql.append(" WHERE ").append(String.join(" AND ", components.predicates()));
            }

            TypedQuery<Usuario> query = em.createQuery(jpql.toString(), Usuario.class);

            // Seteo todos los parametros
            for (Map.Entry<String, Object> entry : components.parameters().entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        }
    }
}
