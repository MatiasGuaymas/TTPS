package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.entities.Avistamiento;
import io.github.vicen621.volveacasa.persistence.dao.AvistamientoDAO;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AvistamientoDAOHibernate extends GenericDAOHibernate<Avistamiento> implements AvistamientoDAO {
    @Autowired
    public AvistamientoDAOHibernate(EntityManager entityManager) {
        super(Avistamiento.class, entityManager);
    }
}
