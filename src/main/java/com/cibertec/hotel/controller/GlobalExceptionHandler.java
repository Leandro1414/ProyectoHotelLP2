package com.cibertec.hotel.controller;

import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String recursoNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("titulo", "Recurso no encontrado");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/personalizado";
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public String reglaNegocio(ReglaNegocioException ex, Model model) {
        model.addAttribute("titulo", "Operación no permitida");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/personalizado";
    }
}
