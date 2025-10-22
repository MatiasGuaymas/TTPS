package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
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
            StringBuilder jpql = new StringBuilder("SELECT u FROM " + getEntityClass().getSimpleName() + " u");
            List<String> predicates = new ArrayList<>();
            Map<String, Object> parameters = new HashMap<>();

            if (filter.getBarrio() != null) {
                predicates.add("u.barrio = :barrio");
                parameters.put("barrio", filter.getBarrio());
            }

            if (filter.getCiudad() != null) {
                predicates.add("u.ciudad = :ciudad");
                parameters.put("ciudad", filter.getCiudad());
            }

            if (filter.getRol() != null) {
                predicates.add("u.rol = :rol");
                parameters.put("rol", filter.getRol());
            }

            if (!predicates.isEmpty()) {
                jpql.append(" WHERE ").append(String.join(" AND ", predicates));
            }

            TypedQuery<Usuario> query = em.createQuery(jpql.toString(), Usuario.class);

            // Seteo todos los parametros
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        }
    }
}
