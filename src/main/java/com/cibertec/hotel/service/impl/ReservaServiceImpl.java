package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.dto.ReservaForm;
import com.cibertec.hotel.entity.*;
import com.cibertec.hotel.entity.enums.EstadoHabitacion;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.*;
import com.cibertec.hotel.service.ReservaCalculoService;
import com.cibertec.hotel.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservaServiceImpl implements ReservaService {
    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final HabitacionRepository habitacionRepository;
    private final ReservaCalculoService calculoService;

    @Override
    public List<Reserva> listar(String busqueda) {
        if (!StringUtils.hasText(busqueda)) return listarTodos();
        String q = busqueda.trim();
        return reservaRepository.findByClienteNombreContainingIgnoreCaseOrNumeroComprobanteContainingIgnoreCaseOrderByFechaReservaDesc(q, q);
    }

    @Override
    public List<Reserva> listarTodos() {
        return reservaRepository.findAllByOrderByFechaReservaDesc();
    }

    @Override
    public Reserva buscarPorId(Integer id) {
        return reservaRepository.buscarCompletaPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
    }

    @Override
    @Transactional
    public Reserva guardar(ReservaForm form) {
        validarFormulario(form);

        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new ReglaNegocioException("Seleccione un cliente válido"));
        Empleado empleado = empleadoRepository.findById(form.getEmpleadoId())
                .orElseThrow(() -> new ReglaNegocioException("Seleccione un empleado válido"));

        Reserva reserva;
        if (form.getIdReserva() == null) {
            reserva = new Reserva();
        } else {
            reserva = buscarPorId(form.getIdReserva());
            if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA
                    || reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
                throw new ReglaNegocioException("Una reserva cerrada no puede modificarse");
            }
        }

        List<Habitacion> disponibles = habitacionRepository.buscarDisponibles(
                form.getFechaIngreso(), form.getFechaSalida(), form.getIdReserva(),
                EstadoHabitacion.DISPONIBLE, EstadoReserva.CANCELADA);

        Set<Integer> seleccionadas = new LinkedHashSet<>(form.getHabitacionIds());
        if (seleccionadas.size() != form.getHabitacionIds().size()) {
            throw new ReglaNegocioException("No repita una misma habitación en la reserva");
        }

        List<Habitacion> habitaciones = disponibles.stream()
                .filter(h -> seleccionadas.contains(h.getIdHabitacion()))
                .toList();
        if (habitaciones.size() != seleccionadas.size()) {
            throw new ReglaNegocioException("Una o más habitaciones ya no están disponibles para las fechas seleccionadas");
        }

        int capacidadTotal = habitaciones.stream()
                .mapToInt(h -> h.getTipoHabitacion().getCapacidadPersonas())
                .sum();
        if (form.getCantidadHuespedes() > capacidadTotal) {
            throw new ReglaNegocioException("La capacidad de las habitaciones seleccionadas es de " + capacidadTotal + " huésped(es)");
        }

        reserva.setCliente(cliente);
        reserva.setEmpleado(empleado);
        reserva.setFechaIngreso(form.getFechaIngreso());
        reserva.setFechaSalida(form.getFechaSalida());
        reserva.setCantidadHuespedes(form.getCantidadHuespedes());
        reserva.setEstadoReserva(form.getEstadoReserva());
        reserva.setTipoComprobante(form.getTipoComprobante());

        reserva.getDetalles().clear();
        if (form.getIdReserva() != null) {
            // Fuerza la eliminación de los detalles anteriores antes de insertar
            // los nuevos y evita colisiones con la restricción única.
            reservaRepository.saveAndFlush(reserva);
        }
        for (Habitacion habitacion : habitaciones) {
            DetalleReserva detalle = new DetalleReserva();
            detalle.setReserva(reserva);
            detalle.setHabitacion(habitacion);
            detalle.setPrecioAplicado(habitacion.getTipoHabitacion().getPrecioNoche());
            reserva.getDetalles().add(detalle);
        }

        calculoService.recalcular(reserva);
        Reserva guardada = reservaRepository.saveAndFlush(reserva);

        if (guardada.getTipoComprobante() == null) {
            guardada.setNumeroComprobante(null);
        } else {
            guardada.setNumeroComprobante(guardada.getTipoComprobante().getSerie()
                    + "-" + String.format("%06d", guardada.getIdReserva()));
        }
        return reservaRepository.save(guardada);
    }

    @Override
    public ReservaForm convertirAForm(Integer id) {
        Reserva reserva = buscarPorId(id);
        ReservaForm form = new ReservaForm();
        form.setIdReserva(reserva.getIdReserva());
        form.setClienteId(reserva.getCliente().getIdCliente());
        form.setEmpleadoId(reserva.getEmpleado().getIdEmpleado());
        form.setFechaIngreso(reserva.getFechaIngreso());
        form.setFechaSalida(reserva.getFechaSalida());
        form.setCantidadHuespedes(reserva.getCantidadHuespedes());
        form.setEstadoReserva(reserva.getEstadoReserva());
        form.setTipoComprobante(reserva.getTipoComprobante());
        form.setHabitacionIds(reserva.getDetalles().stream()
                .map(d -> d.getHabitacion().getIdHabitacion())
                .toList());
        return form;
    }

    @Override
    @Transactional
    public void cancelar(Integer id) {
        Reserva reserva = buscarPorId(id);
        if (reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("Una reserva finalizada no puede cancelarse");
        }
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("La reserva ya se encuentra cancelada");
        }
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public void finalizar(Integer id) {
        Reserva reserva = buscarPorId(id);
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("Una reserva cancelada no puede finalizarse");
        }
        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        reservaRepository.save(reserva);
    }

    @Override
    public long contar() {
        return reservaRepository.count();
    }

    @Override
    public long contarPendientes() {
        return reservaRepository.countByEstadoReserva(EstadoReserva.PENDIENTE);
    }

    @Override
    public long contarActivasHoy() {
        return reservaRepository.contarActivasEnFecha(LocalDate.now(), EstadoReserva.CANCELADA);
    }

    private void validarFormulario(ReservaForm form) {
        if (form.getFechaIngreso() == null || form.getFechaSalida() == null
                || !form.getFechaSalida().isAfter(form.getFechaIngreso())) {
            throw new ReglaNegocioException("La fecha de salida debe ser posterior a la fecha de ingreso");
        }
        if (form.getHabitacionIds() == null || form.getHabitacionIds().isEmpty()) {
            throw new ReglaNegocioException("Seleccione por lo menos una habitación");
        }
        if (form.getCantidadHuespedes() == null || form.getCantidadHuespedes() < 1) {
            throw new ReglaNegocioException("La cantidad de huéspedes debe ser mayor que cero");
        }
        if (form.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("Use la opción Anular para cancelar una reserva");
        }
        if (form.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("Use la opción Finalizar para cerrar una reserva");
        }
    }
}
