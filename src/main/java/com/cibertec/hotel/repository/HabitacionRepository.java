package com.cibertec.hotel.repository;

import com.cibertec.hotel.entity.Habitacion;
import com.cibertec.hotel.entity.enums.EstadoHabitacion;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
    boolean existsByNumeroHabitacionAndIdHabitacionNot(String numeroHabitacion, Integer idHabitacion);
    boolean existsByNumeroHabitacion(String numeroHabitacion);

    @EntityGraph(attributePaths = "tipoHabitacion")
    List<Habitacion> findAllByOrderByNumeroHabitacionAsc();

    @EntityGraph(attributePaths = "tipoHabitacion")
    @Query("select h from Habitacion h where h.idHabitacion = :id")
    Optional<Habitacion> buscarPorIdConTipo(@Param("id") Integer id);

    @EntityGraph(attributePaths = "tipoHabitacion")
    List<Habitacion> findByEstadoOrderByNumeroHabitacionAsc(EstadoHabitacion estado);

    @EntityGraph(attributePaths = "tipoHabitacion")
    @Query("""
            select h from Habitacion h
            where h.estado = :estadoDisponible
              and not exists (
                  select d.idDetalle from DetalleReserva d
                  where d.habitacion = h
                    and d.reserva.estadoReserva <> :estadoCancelada
                    and (:reservaId is null or d.reserva.idReserva <> :reservaId)
                    and :fechaIngreso < d.reserva.fechaSalida
                    and :fechaSalida > d.reserva.fechaIngreso
              )
            order by h.numeroHabitacion
            """)
    List<Habitacion> buscarDisponibles(
            @Param("fechaIngreso") LocalDate fechaIngreso,
            @Param("fechaSalida") LocalDate fechaSalida,
            @Param("reservaId") Integer reservaId,
            @Param("estadoDisponible") EstadoHabitacion estadoDisponible,
            @Param("estadoCancelada") EstadoReserva estadoCancelada);
}
