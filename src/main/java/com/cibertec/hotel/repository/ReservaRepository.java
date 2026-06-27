package com.cibertec.hotel.repository;

import com.cibertec.hotel.entity.Reserva;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    @EntityGraph(attributePaths = {"cliente", "empleado"})
    List<Reserva> findAllByOrderByFechaReservaDesc();

    @EntityGraph(attributePaths = {"cliente", "empleado"})
    List<Reserva> findByClienteNombreContainingIgnoreCaseOrNumeroComprobanteContainingIgnoreCaseOrderByFechaReservaDesc(String cliente, String comprobante);

    @EntityGraph(attributePaths = {"cliente", "empleado", "detalles", "detalles.habitacion", "detalles.habitacion.tipoHabitacion", "servicios", "servicios.servicioAdicional"})
    @Query("select distinct r from Reserva r where r.idReserva = :id")
    Optional<Reserva> buscarCompletaPorId(@Param("id") Integer id);

    long countByEstadoReserva(EstadoReserva estado);

    @Query("""
            select count(r) from Reserva r
            where r.estadoReserva <> :cancelada
              and :fecha >= r.fechaIngreso
              and :fecha < r.fechaSalida
            """)
    long contarActivasEnFecha(@Param("fecha") LocalDate fecha, @Param("cancelada") EstadoReserva cancelada);
}
