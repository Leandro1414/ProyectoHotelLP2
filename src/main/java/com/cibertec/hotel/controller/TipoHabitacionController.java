package com.cibertec.hotel.controller;

import com.cibertec.hotel.entity.TipoHabitacion;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.TipoHabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tipos-habitacion")
@RequiredArgsConstructor
public class TipoHabitacionController {
    private final TipoHabitacionService service;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tipos", service.listarTodos());
        return "tipohabitacion/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("tipoHabitacion", new TipoHabitacion());
        return "tipohabitacion/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("tipoHabitacion", service.buscarPorId(id));
        return "tipohabitacion/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("tipoHabitacion") TipoHabitacion tipo,
                       BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()) return "tipohabitacion/create";
        try {
            service.guardar(tipo);
            flash.addFlashAttribute("exito", "Categoría guardada correctamente");
            return "redirect:/tipos-habitacion";
        } catch (ReglaNegocioException ex) {
            result.rejectValue("nombreTipo", "tipo.duplicado", ex.getMessage());
            return "tipohabitacion/create";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            service.eliminar(id);
            flash.addFlashAttribute("exito", "Categoría eliminada correctamente");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tipos-habitacion";
    }
}
