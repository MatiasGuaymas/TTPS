package io.github.vicen621.volveacasa.persistence.factory;

import io.github.vicen621.volveacasa.persistence.dao.*;
import io.github.vicen621.volveacasa.persistence.dao.hibernate.*;

public class DAOFactory {
    public static UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOHibernate();
    }

    public static MascotaDAO getMascotaDAO() {
        return new MascotaDAOHibernate();
    }

    public static MedallaDAO getMedallaDAO() {
        return new MedallaDAOHibernate();
    }

    public static AvistamientoDAO getAvistamientoDAO() {
        return new AvistamientoDAOHibernate();
    }

    public static MensajeDAO getMensajeDAO() {
        return new MensajeDAOHibernate();
    }
}
