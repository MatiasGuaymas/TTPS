package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.entities.Mascota;
import io.github.vicen621.volveacasa.persistence.dao.MascotaDAO;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MascotaDAOHibernate extends GenericDAOHibernate<Mascota> implements MascotaDAO {

    @Autowired
    public MascotaDAOHibernate(EntityManager entityManager) {
        super(Mascota.class, entityManager);
    }
}
