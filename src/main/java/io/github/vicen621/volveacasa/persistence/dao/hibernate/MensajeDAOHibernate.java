package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.entities.Mensaje;
import io.github.vicen621.volveacasa.persistence.dao.MensajeDAO;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MensajeDAOHibernate extends GenericDAOHibernate<Mensaje> implements MensajeDAO {
    @Autowired
    public MensajeDAOHibernate(EntityManager entityManager) {
        super(Mensaje.class, entityManager);
    }
}
