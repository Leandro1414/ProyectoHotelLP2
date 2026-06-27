package com.cibertec.hotel.controller;

import com.cibertec.hotel.entity.ServicioAdicional;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.ServicioAdicionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicios-adicionales")
@RequiredArgsConstructor
public class ServicioAdicionalController {
    private final ServicioAdicionalService service;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("servicios", service.listarTodos());
        return "servicioadicional/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("servicioAdicional", new ServicioAdicional());
        return "servicioadicional/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("servicioAdicional", service.buscarPorId(id));
        return "servicioadicional/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("servicioAdicional") ServicioAdicional servicio,
                       BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()) return "servicioadicional/create";
        try {
            service.guardar(servicio);
            flash.addFlashAttribute("exito", "Servicio guardado correctamente");
            return "redirect:/servicios-adicionales";
        } catch (ReglaNegocioException ex) {
            result.rejectValue("nombreServicio", "servicio.duplicado", ex.getMessage());
            return "servicioadicional/create";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            service.eliminar(id);
            flash.addFlashAttribute("exito", "Servicio eliminado correctamente");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/servicios-adicionales";
    }
}
