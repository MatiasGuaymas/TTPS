package io.github.vicen621.volveacasa.persistence.dao;

import io.github.vicen621.volveacasa.persistence.entities.Medalla;

public interface MedallaDAO extends GenericDAO<Medalla> {
    Medalla getByName(String name);
}
