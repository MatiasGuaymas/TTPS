package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Mascota;
import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.MascotaDAO;
import io.github.vicen621.volveacasa.persistence.dao.filtros.MascotaFilter;
import io.github.vicen621.volveacasa.persistence.dao.filtros.QueryComponents;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MascotaDAOHibernate extends GenericDAOHibernate<Mascota> implements MascotaDAO {

    public MascotaDAOHibernate() {
        super(Mascota.class);
    }

    @Override
    public List<Mascota> getAllWithFilter(MascotaFilter filter) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT e FROM " + getEntityClass().getSimpleName() + " e");
            QueryComponents components = filter.buildQueryComponents();

            if (!components.predicates().isEmpty()) {
                jpql.append(" WHERE ").append(String.join(" AND ", components.predicates()));
            }

            TypedQuery<Mascota> query = em.createQuery(jpql.toString(), Mascota.class);

            // Seteo todos los parametros
            for (Map.Entry<String, Object> entry : components.parameters().entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        }
    }
}
