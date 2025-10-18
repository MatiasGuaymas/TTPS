package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Mascota;
import io.github.vicen621.volveacasa.persistence.dao.MascotaDAO;

public class MascotaDAOHibernate extends GenericDAOHibernate<Mascota> implements MascotaDAO {

    public MascotaDAOHibernate() {
        super(Mascota.class);
    }
}
