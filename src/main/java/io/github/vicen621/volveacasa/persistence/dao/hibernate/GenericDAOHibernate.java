package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.dao.GenericDAO;
import io.github.vicen621.volveacasa.persistence.dao.filtros.Filter;
import io.github.vicen621.volveacasa.persistence.dao.filtros.QueryComponents;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Transactional
public class GenericDAOHibernate<T> implements GenericDAO<T> {
    private Class<T> entityClass;
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDAOHibernate.class);

    // @PersistenceContext
    private EntityManager entityManager;

    public GenericDAOHibernate(Class<T> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    // public void setEntityManager(EntityManager em){
    //     this.entityManager = em;
    // }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void delete(Long id) {
        T entity = this.get(id);
        this.entityManager.remove(entity);
    }

    @Override
    public T get(Long id) {
        return this.getEntityManager().find(entityClass, id);
    }

    @Override
    public List<T> getAll(String orderBy) {
        try (EntityManager em = this.getEntityManager()) {
            String hql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e";
            if (orderBy != null && !orderBy.trim().isEmpty()) {
                hql += " ORDER BY e." + orderBy;
            }
            return em.createQuery(hql, entityClass).getResultList();
        }
    }

    @Override
    public T persist(T entity) {
        this.getEntityManager().persist(entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        return this.getEntityManager().merge(entity);
    }

    @Override
    public List<T> getFiltered(Filter filter) {
        try (EntityManager em = this.getEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT e FROM " + getEntityClass().getSimpleName() + " e");
            QueryComponents components = filter.buildQueryComponents();

            if (!components.predicates().isEmpty()) {
                jpql.append(" WHERE ").append(String.join(" AND ", components.predicates()));
            }

            TypedQuery<T> query = em.createQuery(jpql.toString(), getEntityClass());

            // Seteo todos los parametros
            for (Map.Entry<String, Object> entry : components.parameters().entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        }
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
