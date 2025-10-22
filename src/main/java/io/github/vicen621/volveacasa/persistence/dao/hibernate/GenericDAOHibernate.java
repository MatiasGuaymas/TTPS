package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.GenericDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GenericDAOHibernate<T> implements GenericDAO<T> {
    private Class<T> entityClass;
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDAOHibernate.class);

    public GenericDAOHibernate(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void delete(T entity) {
        EntityTransaction transaction = null;
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            // Asegurarse de que la entidad está gestionada antes de eliminarla
            if (!em.contains(entity)) {
                entity = em.merge(entity);
            }

            em.remove(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            LOGGER.error("Error al ejecutar el borrado de {}", getEntityClass().getName(), e);

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void delete(Long id) {
        T entity = this.get(id);
        this.delete(entity);
    }

    @Override
    public T get(Long id) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    @Override
    public List<T> getAll(String orderBy) {
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            String hql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e";
            if (orderBy != null && !orderBy.trim().isEmpty()) {
                hql += " ORDER BY e." + orderBy;
            }
            return em.createQuery(hql, entityClass).getResultList();
        }
    }

    @Override
    public T persist(T entity) {
        EntityTransaction transaction = null;
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.error("Error al ejecutar el alta de {}", getEntityClass().getName(), e);
        }
        return entity;
    }

    @Override
    public T update(T entity) {
        EntityTransaction transaction = null;
        T ret = null;
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();
            ret = em.merge(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.error("Error al ejecutar el update de {}", getEntityClass().getName(), e);
        }
        return ret;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
