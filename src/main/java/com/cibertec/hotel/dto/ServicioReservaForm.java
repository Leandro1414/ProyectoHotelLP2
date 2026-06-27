package com.cibertec.hotel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicioReservaForm {
    private Integer idServicioReserva;

    @NotNull(message = "Seleccione una reserva")
    private Integer reservaId;

    @NotNull(message = "Seleccione un servicio")
    private Integer servicioId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad = 1;
}
