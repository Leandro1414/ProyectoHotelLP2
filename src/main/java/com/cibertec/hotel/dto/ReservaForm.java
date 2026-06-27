package com.cibertec.hotel.dto;

import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.entity.enums.TipoComprobante;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReservaForm {
    private Integer idReserva;

    @NotNull(message = "Seleccione un cliente")
    private Integer clienteId;

    @NotNull(message = "Seleccione un empleado")
    private Integer empleadoId;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaIngreso;

    @NotNull(message = "La fecha de salida es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaSalida;

    @NotNull(message = "Indique la cantidad de huéspedes")
    @Min(value = 1, message = "Debe registrarse al menos un huésped")
    private Integer cantidadHuespedes = 1;

    @NotEmpty(message = "Seleccione por lo menos una habitación")
    private List<Integer> habitacionIds = new ArrayList<>();

    @NotNull(message = "Seleccione el estado de la reserva")
    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE;

    private TipoComprobante tipoComprobante;
}
