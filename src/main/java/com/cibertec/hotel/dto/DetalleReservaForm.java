package com.cibertec.hotel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleReservaForm {
    @NotNull(message = "Seleccione una reserva")
    private Integer reservaId;

    @NotNull(message = "Seleccione una habitación")
    private Integer habitacionId;
}
