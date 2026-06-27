package com.cibertec.hotel.repository;

import com.cibertec.hotel.entity.ServicioReserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioReservaRepository extends JpaRepository<ServicioReserva, Integer> {
    @EntityGraph(attributePaths = {"reserva", "reserva.cliente", "servicioAdicional"})
    List<ServicioReserva> findAllByOrderByFechaConsumoDesc();
}
