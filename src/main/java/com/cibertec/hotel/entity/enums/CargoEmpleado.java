package com.cibertec.hotel.entity.enums;

public enum CargoEmpleado {
    RECEPCIONISTA("Recepcionista"),
    ADMINISTRADOR("Administrador"),
    SUPERVISOR("Supervisor"),
    GERENTE("Gerente");

    private final String etiqueta;

    CargoEmpleado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
