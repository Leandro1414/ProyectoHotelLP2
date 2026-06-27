package com.cibertec.hotel.controller;

import com.cibertec.hotel.entity.Empleado;
import com.cibertec.hotel.entity.enums.CargoEmpleado;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empleados")
@RequiredArgsConstructor
public class EmpleadoController {
    private final EmpleadoService empleadoService;

    @ModelAttribute("cargos")
    public CargoEmpleado[] cargos() {
        return CargoEmpleado.values();
    }

    @GetMapping
    public String index(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("empleados", empleadoService.listar(q));
        model.addAttribute("q", q);
        return "empleado/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("empleado", new Empleado());
        return "empleado/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("empleado", empleadoService.buscarPorId(id));
        return "empleado/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("empleado") Empleado empleado,
                       BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()) return "empleado/create";
        try {
            empleadoService.guardar(empleado);
            flash.addFlashAttribute("exito", "Empleado guardado correctamente");
            return "redirect:/empleados";
        } catch (ReglaNegocioException ex) {
            result.rejectValue("dni", "empleado.duplicado", ex.getMessage());
            return "empleado/create";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            empleadoService.eliminar(id);
            flash.addFlashAttribute("exito", "Empleado eliminado correctamente");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/empleados";
    }
}
