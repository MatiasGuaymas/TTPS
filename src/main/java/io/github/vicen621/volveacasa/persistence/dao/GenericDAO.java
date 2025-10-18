package io.github.vicen621.volveacasa.persistence.dao;

import java.util.List;

public interface GenericDAO<T> {
    void delete(T entity);
    void delete(Long id);
    T get(Long id);
    List<T> getAll(String orderBy);
    T persist(T entity);
    T update(T entity);
}
