package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.entities.Medalla;
import io.github.vicen621.volveacasa.persistence.dao.MedallaDAO;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MedallaDAOHibernate extends GenericDAOHibernate<Medalla> implements MedallaDAO {

    @Autowired
    public MedallaDAOHibernate(EntityManager entityManager) {
        super(Medalla.class, entityManager);
    }

    @Override
    public Medalla getByName(String name) {
        try (EntityManager em = this.getEntityManager();) {
            return em.find(Medalla.class, name);
        }
    }
}
