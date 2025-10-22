package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.entities.Mascota;
import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.MascotaDAO;
import io.github.vicen621.volveacasa.persistence.dao.filtros.MascotaFilter;
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
            StringBuilder jpql = new StringBuilder("SELECT u FROM " + getEntityClass().getSimpleName() + " u");
            List<String> predicates = new ArrayList<>();
            Map<String, Object> parameters = new HashMap<>();

            if (filter.getColor() != null) {
                predicates.add("u.color = :color");
                parameters.put("color", filter.getColor());
            }

            if (filter.getRaza() != null) {
                predicates.add("u.raza = :raza");
                parameters.put("raza", filter.getRaza());
            }

            if (filter.getEstado() != null) {
                predicates.add("u.estado = :estado");
                parameters.put("estado", filter.getEstado());
            }

            if (filter.getTipo() != null) {
                predicates.add("u.tipo = :tipo");
                parameters.put("tipo", filter.getTipo());
            }

            if (!predicates.isEmpty()) {
                jpql.append(" WHERE ").append(String.join(" AND ", predicates));
            }

            TypedQuery<Mascota> query = em.createQuery(jpql.toString(), Mascota.class);

            // Seteo todos los parametros
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        }
    }
}
