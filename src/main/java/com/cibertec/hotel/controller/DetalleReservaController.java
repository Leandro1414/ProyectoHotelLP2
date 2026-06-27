package com.cibertec.hotel.controller;

import com.cibertec.hotel.dto.DetalleReservaForm;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.DetalleReservaService;
import com.cibertec.hotel.service.HabitacionService;
import com.cibertec.hotel.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/detalles-reserva")
@RequiredArgsConstructor
public class DetalleReservaController {
    private final DetalleReservaService detalleService;
    private final ReservaService reservaService;
    private final HabitacionService habitacionService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("detalles", detalleService.listarTodos());
        return "detallereserva/index";
    }

    @GetMapping("/create")
    public String create(@RequestParam(name = "reservaId", required = false) Integer reservaId, Model model) {
        DetalleReservaForm form = new DetalleReservaForm();
        form.setReservaId(reservaId);
        model.addAttribute("detalleForm", form);
        cargarListas(model);
        return "detallereserva/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("detalleForm") DetalleReservaForm form,
                       BindingResult result,
                       Model model,
                       RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarListas(model);
            return "detallereserva/create";
        }
        try {
            detalleService.agregar(form);
            flash.addFlashAttribute("exito", "Habitación agregada a la reserva");
            return "redirect:/reservas/view/" + form.getReservaId();
        } catch (ReglaNegocioException ex) {
            result.reject("detalle.regla", ex.getMessage());
            cargarListas(model);
            return "detallereserva/create";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id,
                         @RequestParam(name = "reservaId", required = false) Integer reservaId,
                         RedirectAttributes flash) {
        try {
            detalleService.eliminar(id);
            flash.addFlashAttribute("exito", "Habitación retirada de la reserva");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return reservaId == null ? "redirect:/detalles-reserva" : "redirect:/reservas/view/" + reservaId;
    }

    private void cargarListas(Model model) {
        model.addAttribute("reservas", reservaService.listarTodos().stream()
                .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA
                        && r.getEstadoReserva() != EstadoReserva.FINALIZADA)
                .toList());
        model.addAttribute("habitaciones", habitacionService.listarOperativas());
    }
}
