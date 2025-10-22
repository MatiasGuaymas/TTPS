package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

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
    public List<Usuario> getByBarrio(String barrio) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e.barrio = :barrio";
            TypedQuery<Usuario> query = em.createQuery(jpql, getEntityClass());
            query.setParameter("barrio", barrio);
            return query.getResultList();
        }
    }

    @Override
    public List<Usuario> getByCiudad(String ciudad) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e.ciudad = :ciudad";
            TypedQuery<Usuario> query = em.createQuery(jpql, getEntityClass());
            query.setParameter("ciudad", ciudad);
            return query.getResultList();
        }
    }

    @Override
    public List<Usuario> getByRol(Usuario.Rol rol) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e.rol = :rol";
            TypedQuery<Usuario> query = em.createQuery(jpql, getEntityClass());
            query.setParameter("rol", rol);
            return query.getResultList();
        }
    }


}
