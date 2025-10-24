package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.entities.Avistamiento;
import io.github.vicen621.volveacasa.persistence.dao.AvistamientoDAO;

public class AvistamientoDAOHibernate extends GenericDAOHibernate<Avistamiento> implements AvistamientoDAO {
    public AvistamientoDAOHibernate() {
        super(Avistamiento.class);
    }
}
