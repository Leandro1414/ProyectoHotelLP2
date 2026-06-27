package com.cibertec.hotel.service.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.cibertec.hotel.service.ReporteService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final DataSource dataSource;

    public ReporteServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public byte[] generarReporte(String jrxml, Map<String, Object> parametros) throws Exception {
        InputStream jrxmlStream = new ClassPathResource("reportes/" + jrxml).getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }
}
