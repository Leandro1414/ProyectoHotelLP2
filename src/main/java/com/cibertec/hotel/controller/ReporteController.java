package com.cibertec.hotel.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cibertec.hotel.service.ReporteService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /* Mapa de reportes disponibles: nombre amigable en URL -> archivo JRXML. */
    private static final Map<String, String> REPORTES = Map.of(
        "reservas-hoy", "Reporte_Reservas_Hoy.jrxml",
        "consumos-hoy", "Reporte_Consumos_Hoy.jrxml"
    );

    /* Pantalla principal con los dos casos de uso de reportes. */
    @GetMapping
    public String index() {
        return "reporte/index";
    }

    /* Reportes generales: /reportes/reservas-hoy y /reportes/consumos-hoy. */
    @GetMapping("/{tipo}")
    public ResponseEntity<byte[]> generarReporte(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "ver") String modo) throws Exception {

        String jrxml = REPORTES.get(tipo.toLowerCase());
        if (jrxml == null) {
            return ResponseEntity.badRequest().body("Reporte no valido".getBytes());
        }

        Map<String, Object> parametros = new HashMap<>();
        byte[] pdf = reporteService.generarReporte(jrxml, parametros);

        String disposition = modo.equalsIgnoreCase("descargar") ? "attachment" : "inline";
        String nombrePDF = jrxml.replace(".jrxml", ".pdf");

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=" + nombrePDF)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    /* Comprobante de una reserva especifica: /reportes/comprobante/{idReserva}. */
    @GetMapping("/comprobante/{idReserva}")
    public ResponseEntity<byte[]> generarComprobante(
            @PathVariable Integer idReserva,
            @RequestParam(defaultValue = "ver") String modo) throws Exception {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("idReserva", idReserva);

        byte[] pdf = reporteService.generarReporte("Comprobante_Reserva.jrxml", parametros);
        String disposition = modo.equalsIgnoreCase("descargar") ? "attachment" : "inline";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    disposition + "; filename=Comprobante_Reserva_" + idReserva + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
