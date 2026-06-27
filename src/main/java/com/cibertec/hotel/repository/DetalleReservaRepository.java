package com.cibertec.hotel.repository;

import com.cibertec.hotel.entity.DetalleReserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleReservaRepository extends JpaRepository<DetalleReserva, Integer> {
    @EntityGraph(attributePaths = {"reserva", "reserva.cliente", "habitacion", "habitacion.tipoHabitacion"})
    List<DetalleReserva> findAllByOrderByIdDetalleDesc();

    long countByReservaIdReserva(Integer reservaId);
    boolean existsByReservaIdReservaAndHabitacionIdHabitacion(Integer reservaId, Integer habitacionId);
}
