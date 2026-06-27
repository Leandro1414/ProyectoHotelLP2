package com.cibertec.hotel.entity.enums;

public enum EstadoHabitacion {
    DISPONIBLE("Disponible"),
    MANTENIMIENTO("Mantenimiento"),
    LIMPIEZA("Limpieza"),
    FUERA_DE_SERVICIO("Fuera de servicio");

    private final String etiqueta;

    EstadoHabitacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
