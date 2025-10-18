package io.github.vicen621.volveacasa.persistence.dao.hibernate;

import io.github.vicen621.volveacasa.persistence.EntityManagerSingleton;
import io.github.vicen621.volveacasa.persistence.dao.GenericDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GenericDAOHibernate<T> implements GenericDAO<T> {
    private Class<T> entityClass;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GenericDAOHibernate.class);

    public GenericDAOHibernate(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void delete(T entity) {
        EntityTransaction transaction = null;
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();
            em.remove(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.error("Error al ejecutar el borrado de {}", getEntityClass().getName(), e);
        }
    }

    @Override
    public void delete(Long id) {
        EntityTransaction transaction = null;
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();
            T entity = em.find(entityClass, id);
            em.remove(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.error("Error al ejecutar el borrado de {} con id: {}", getEntityClass().getName(), id, e);
        }
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
            TypedQuery<T> query = em.createNamedQuery(
                    "SELECT e FROM " + getEntityClass().getSimpleName() +
                            " e order by e." + orderBy,
                    entityClass
            );
            return query.getResultList();
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
