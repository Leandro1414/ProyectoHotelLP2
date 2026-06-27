package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.dto.ServicioReservaForm;
import com.cibertec.hotel.entity.Reserva;
import com.cibertec.hotel.entity.ServicioAdicional;
import com.cibertec.hotel.entity.ServicioReserva;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.ReservaRepository;
import com.cibertec.hotel.repository.ServicioAdicionalRepository;
import com.cibertec.hotel.repository.ServicioReservaRepository;
import com.cibertec.hotel.service.ReservaCalculoService;
import com.cibertec.hotel.service.ServicioReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServicioReservaServiceImpl implements ServicioReservaService {
    private final ServicioReservaRepository servicioReservaRepository;
    private final ReservaRepository reservaRepository;
    private final ServicioAdicionalRepository servicioAdicionalRepository;
    private final ReservaCalculoService calculoService;

    @Override
    public List<ServicioReserva> listarTodos() {
        return servicioReservaRepository.findAllByOrderByFechaConsumoDesc();
    }

    @Override
    public ServicioReservaForm convertirAForm(Integer id) {
        ServicioReserva consumo = servicioReservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Consumo no encontrado"));
        ServicioReservaForm form = new ServicioReservaForm();
        form.setIdServicioReserva(consumo.getIdServicioReserva());
        form.setReservaId(consumo.getReserva().getIdReserva());
        form.setServicioId(consumo.getServicioAdicional().getIdServicio());
        form.setCantidad(consumo.getCantidad());
        return form;
    }

    @Override
    @Transactional
    public ServicioReserva guardar(ServicioReservaForm form) {
        if (form.getCantidad() == null || form.getCantidad() < 1) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        Reserva reserva = reservaRepository.buscarCompletaPorId(form.getReservaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA || reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("No se pueden registrar consumos en una reserva cerrada");
        }
        ServicioAdicional servicio = servicioAdicionalRepository.findById(form.getServicioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado"));

        ServicioReserva consumo;
        if (form.getIdServicioReserva() == null) {
            consumo = new ServicioReserva();
            consumo.setReserva(reserva);
            reserva.getServicios().add(consumo);
        } else {
            consumo = servicioReservaRepository.findById(form.getIdServicioReserva())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Consumo no encontrado"));
            if (!consumo.getReserva().getIdReserva().equals(form.getReservaId())) {
                throw new ReglaNegocioException("No se puede mover un consumo a otra reserva");
            }
        }

        consumo.setServicioAdicional(servicio);
        consumo.setCantidad(form.getCantidad());
        consumo.setPrecioAplicado(servicio.getPrecio());
        ServicioReserva guardado = servicioReservaRepository.save(consumo);

        Reserva actualizada = reservaRepository.buscarCompletaPorId(reserva.getIdReserva())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
        calculoService.recalcular(actualizada);
        reservaRepository.save(actualizada);
        return guardado;
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        ServicioReserva consumo = servicioReservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Consumo no encontrado"));
        Integer reservaId = consumo.getReserva().getIdReserva();
        Reserva reserva = reservaRepository.buscarCompletaPorId(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA || reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("No se puede modificar una reserva cerrada");
        }
        reserva.getServicios().removeIf(s -> s.getIdServicioReserva().equals(id));
        calculoService.recalcular(reserva);
        reservaRepository.save(reserva);
    }
}
