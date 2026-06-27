package com.cibertec.hotel.entity.enums;

public enum EstadoReserva {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    EN_CURSO("En curso"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoReserva(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
