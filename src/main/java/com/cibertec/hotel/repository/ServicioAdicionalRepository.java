package com.cibertec.hotel.repository;

import com.cibertec.hotel.entity.ServicioAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Integer> {
    boolean existsByNombreServicioIgnoreCaseAndIdServicioNot(String nombre, Integer id);
    boolean existsByNombreServicioIgnoreCase(String nombre);
    List<ServicioAdicional> findAllByOrderByNombreServicioAsc();
}
