package io.github.vicen621.volveacasa.persistence.dao;

import io.github.vicen621.volveacasa.entities.Mascota;

import java.util.List;

public interface MascotaDAO extends GenericDAO<Mascota> {
    List<Mascota> getByTipo(Mascota.Tipo tipo);
    List<Mascota> getByEstado(Mascota.Estado usuario);
    List<Mascota> getByRaza(String raza);
    List<Mascota> getByColor(String color);

}
