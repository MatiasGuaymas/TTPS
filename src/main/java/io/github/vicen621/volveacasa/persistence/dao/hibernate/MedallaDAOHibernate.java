package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.entities.Medalla;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.MedallaDAO;
import jakarta.persistence.EntityManager;

public class MedallaDAOHibernate extends GenericDAOHibernate<Medalla> implements MedallaDAO {
    public MedallaDAOHibernate() {
        super(Medalla.class);
    }

    @Override
    public Medalla getByName(String name) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            return em.find(Medalla.class, name);
        }
    }
}
