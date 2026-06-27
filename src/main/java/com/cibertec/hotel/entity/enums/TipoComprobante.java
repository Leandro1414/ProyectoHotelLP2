package com.cibertec.hotel.entity.enums;

public enum TipoComprobante {
    BOLETA("Boleta", "B001"),
    FACTURA("Factura", "F001");

    private final String etiqueta;
    private final String serie;

    TipoComprobante(String etiqueta, String serie) {
        this.etiqueta = etiqueta;
        this.serie = serie;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getSerie() {
        return serie;
    }
}
