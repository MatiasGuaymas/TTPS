package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Mascota;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.MascotaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MascotaDAOHibernate extends GenericDAOHibernate<Mascota> implements MascotaDAO {

    public MascotaDAOHibernate() {
        super(Mascota.class);
    }

    @Override
    public List<Mascota> getByTipo(Mascota.Tipo tipo) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String queryStr = "SELECT m FROM " + getEntityClass().getSimpleName() + " m WHERE m.tipo = :tipo";
            TypedQuery<Mascota> query = em.createQuery(queryStr, Mascota.class);
            query.setParameter("tipo", tipo);
            return query.getResultList();
        }
    }

    @Override
    public List<Mascota> getByEstado(Mascota.Estado usuario) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String queryStr = "SELECT m FROM " + getEntityClass().getSimpleName() + " m WHERE m.estado = :estado";
            TypedQuery<Mascota> query = em.createQuery(queryStr, Mascota.class);
            query.setParameter("estado", usuario);
            return query.getResultList();
        }
    }

    @Override
    public List<Mascota> getByRaza(String raza) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String queryStr = "SELECT m FROM " + getEntityClass().getSimpleName() + " m WHERE m.raza = :raza";
            TypedQuery<Mascota> query = em.createQuery(queryStr, Mascota.class);
            query.setParameter("raza", raza);
            return query.getResultList();
        }
    }

    @Override
    public List<Mascota> getByColor(String color) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String queryStr = "SELECT m FROM " + getEntityClass().getSimpleName() + " m WHERE m.color = :color";
            TypedQuery<Mascota> query = em.createQuery(queryStr, Mascota.class);
            query.setParameter("color", color);
            return query.getResultList();
        }
    }
}
