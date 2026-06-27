package com.cibertec.hotel;

import net.sf.jasperreports.engine.JasperCompileManager;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ReporteJrxmlTests {

    @Test
    void compilaLosTresReportesJasper() throws Exception {
        String[] reportes = {
            "Reporte_Reservas_Hoy.jrxml",
            "Reporte_Consumos_Hoy.jrxml",
            "Comprobante_Reserva.jrxml"
        };

        for (String reporte : reportes) {
            try (InputStream stream = new ClassPathResource("reportes/" + reporte).getInputStream()) {
                assertThat(JasperCompileManager.compileReport(stream)).isNotNull();
            }
        }
    }
}
