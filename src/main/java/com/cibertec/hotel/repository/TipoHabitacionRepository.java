package com.cibertec.hotel.repository;

import com.cibertec.hotel.entity.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Integer> {
    boolean existsByNombreTipoIgnoreCaseAndIdTipoNot(String nombreTipo, Integer idTipo);
    boolean existsByNombreTipoIgnoreCase(String nombreTipo);
    List<TipoHabitacion> findAllByOrderByPrecioNocheAsc();
}
