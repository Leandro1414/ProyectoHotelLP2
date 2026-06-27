package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.dto.DetalleReservaForm;
import com.cibertec.hotel.entity.DetalleReserva;
import com.cibertec.hotel.entity.Habitacion;
import com.cibertec.hotel.entity.Reserva;
import com.cibertec.hotel.entity.enums.EstadoHabitacion;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.DetalleReservaRepository;
import com.cibertec.hotel.repository.HabitacionRepository;
import com.cibertec.hotel.repository.ReservaRepository;
import com.cibertec.hotel.service.DetalleReservaService;
import com.cibertec.hotel.service.ReservaCalculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetalleReservaServiceImpl implements DetalleReservaService {
    private final DetalleReservaRepository detalleRepository;
    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;
    private final ReservaCalculoService calculoService;

    @Override
    public List<DetalleReserva> listarTodos() {
        return detalleRepository.findAllByOrderByIdDetalleDesc();
    }

    @Override
    @Transactional
    public void agregar(DetalleReservaForm form) {
        Reserva reserva = reservaRepository.buscarCompletaPorId(form.getReservaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA || reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("No se pueden agregar habitaciones a una reserva cerrada");
        }
        if (detalleRepository.existsByReservaIdReservaAndHabitacionIdHabitacion(form.getReservaId(), form.getHabitacionId())) {
            throw new ReglaNegocioException("La habitación ya está asignada a esta reserva");
        }

        boolean disponible = habitacionRepository.buscarDisponibles(
                        reserva.getFechaIngreso(), reserva.getFechaSalida(), reserva.getIdReserva(),
                        EstadoHabitacion.DISPONIBLE, EstadoReserva.CANCELADA)
                .stream().anyMatch(h -> h.getIdHabitacion().equals(form.getHabitacionId()));
        if (!disponible) {
            throw new ReglaNegocioException("La habitación no está disponible para las fechas de la reserva");
        }

        Habitacion habitacion = habitacionRepository.findById(form.getHabitacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Habitación no encontrada"));
        DetalleReserva detalle = new DetalleReserva();
        detalle.setReserva(reserva);
        detalle.setHabitacion(habitacion);
        detalle.setPrecioAplicado(habitacion.getTipoHabitacion().getPrecioNoche());
        reserva.getDetalles().add(detalle);
        calculoService.recalcular(reserva);
        reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        DetalleReserva detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle de reserva no encontrado"));
        Reserva reserva = reservaRepository.buscarCompletaPorId(detalle.getReserva().getIdReserva())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA || reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("No se puede modificar una reserva cerrada");
        }
        if (reserva.getDetalles().size() <= 1) {
            throw new ReglaNegocioException("La reserva debe conservar por lo menos una habitación");
        }
        int capacidadRestante = reserva.getDetalles().stream()
                .filter(d -> !d.getIdDetalle().equals(id))
                .mapToInt(d -> d.getHabitacion().getTipoHabitacion().getCapacidadPersonas())
                .sum();
        if (capacidadRestante < reserva.getCantidadHuespedes()) {
            throw new ReglaNegocioException("No puede retirar la habitación: la capacidad restante sería insuficiente");
        }
        reserva.getDetalles().removeIf(d -> d.getIdDetalle().equals(id));
        calculoService.recalcular(reserva);
        reservaRepository.save(reserva);
    }
}
