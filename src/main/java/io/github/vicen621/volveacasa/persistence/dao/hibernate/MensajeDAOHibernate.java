package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Mensaje;
import io.github.vicen621.volveacasa.persistence.dao.MensajeDAO;

public class MensajeDAOHibernate extends GenericDAOHibernate<Mensaje> implements MensajeDAO {
    public MensajeDAOHibernate() {
        super(Mensaje.class);
    }
}
