package io.github.vicen621.volveacasa.persistence.dao;

import io.github.vicen621.volveacasa.entities.Mascota;
import io.github.vicen621.volveacasa.persistence.dao.filtros.MascotaFilter;

import java.util.List;

public interface MascotaDAO extends GenericDAO<Mascota> {
    List<Mascota> getAllWithFilter(MascotaFilter filter);
}
