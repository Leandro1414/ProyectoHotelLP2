package com.cibertec.hotel.service;

import java.util.Map;

public interface ReporteService {
    byte[] generarReporte(String jrxml, Map<String, Object> parametros) throws Exception;
}
