package com.cibertec.hotel.controller;

import com.cibertec.hotel.entity.Habitacion;
import com.cibertec.hotel.entity.TipoHabitacion;
import com.cibertec.hotel.entity.enums.EstadoHabitacion;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.HabitacionService;
import com.cibertec.hotel.service.TipoHabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {
    private final HabitacionService habitacionService;
    private final TipoHabitacionService tipoService;

    @ModelAttribute("tipos")
    public java.util.List<TipoHabitacion> tipos() {
        return tipoService.listarTodos();
    }

    @ModelAttribute("estadosHabitacion")
    public EstadoHabitacion[] estados() {
        return EstadoHabitacion.values();
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("habitaciones", habitacionService.listarTodos());
        return "habitacion/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Habitacion habitacion = new Habitacion();
        habitacion.setTipoHabitacion(new TipoHabitacion());
        model.addAttribute("habitacion", habitacion);
        return "habitacion/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("habitacion", habitacionService.buscarPorId(id));
        return "habitacion/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("habitacion") Habitacion habitacion,
                       BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()) return "habitacion/create";
        try {
            habitacionService.guardar(habitacion);
            flash.addFlashAttribute("exito", "Habitación guardada correctamente");
            return "redirect:/habitaciones";
        } catch (ReglaNegocioException ex) {
            result.reject("habitacion.regla", ex.getMessage());
            return "habitacion/create";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            habitacionService.eliminar(id);
            flash.addFlashAttribute("exito", "Habitación eliminada correctamente");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/habitaciones";
    }
}
