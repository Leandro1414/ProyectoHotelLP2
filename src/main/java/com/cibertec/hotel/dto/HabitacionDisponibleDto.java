package com.cibertec.hotel.dto;

import java.math.BigDecimal;

public record HabitacionDisponibleDto(
        Integer id,
        String numero,
        String tipo,
        Integer capacidad,
        BigDecimal precioNoche
) { }
