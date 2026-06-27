package com.cibertec.hotel.service;

import com.cibertec.hotel.entity.Reserva;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Component
public class ReservaCalculoService {

    public BigDecimal calcularTotal(Reserva reserva) {
        if (reserva.getFechaIngreso() == null || reserva.getFechaSalida() == null) {
            return BigDecimal.ZERO;
        }

        long noches = ChronoUnit.DAYS.between(reserva.getFechaIngreso(), reserva.getFechaSalida());
        if (noches <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal hospedaje = reserva.getDetalles().stream()
                .map(detalle -> detalle.getPrecioAplicado().multiply(BigDecimal.valueOf(noches)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal servicios = reserva.getServicios().stream()
                .map(servicio -> servicio.getPrecioAplicado().multiply(BigDecimal.valueOf(servicio.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return hospedaje.add(servicios);
    }

    public void recalcular(Reserva reserva) {
        reserva.setMontoTotal(calcularTotal(reserva));
    }
}
