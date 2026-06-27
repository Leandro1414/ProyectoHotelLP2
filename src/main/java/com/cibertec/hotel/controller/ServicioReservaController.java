package com.cibertec.hotel.controller;

import com.cibertec.hotel.dto.ServicioReservaForm;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.ReservaService;
import com.cibertec.hotel.service.ServicioAdicionalService;
import com.cibertec.hotel.service.ServicioReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicios-reserva")
@RequiredArgsConstructor
public class ServicioReservaController {
    private final ServicioReservaService servicioReservaService;
    private final ReservaService reservaService;
    private final ServicioAdicionalService servicioAdicionalService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("serviciosReserva", servicioReservaService.listarTodos());
        return "servicioreserva/index";
    }

    @GetMapping("/create")
    public String create(@RequestParam(name = "reservaId", required = false) Integer reservaId, Model model) {
        ServicioReservaForm form = new ServicioReservaForm();
        form.setReservaId(reservaId);
        model.addAttribute("servicioReservaForm", form);
        cargarListas(model);
        return "servicioreserva/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("servicioReservaForm", servicioReservaService.convertirAForm(id));
        cargarListas(model);
        return "servicioreserva/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("servicioReservaForm") ServicioReservaForm form,
                       BindingResult result,
                       Model model,
                       RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarListas(model);
            return "servicioreserva/create";
        }
        try {
            servicioReservaService.guardar(form);
            flash.addFlashAttribute("exito", "Consumo guardado y total de la reserva actualizado");
            return "redirect:/reservas/view/" + form.getReservaId();
        } catch (ReglaNegocioException ex) {
            result.reject("consumo.regla", ex.getMessage());
            cargarListas(model);
            return "servicioreserva/create";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id,
                         @RequestParam(name = "reservaId", required = false) Integer reservaId,
                         RedirectAttributes flash) {
        try {
            servicioReservaService.eliminar(id);
            flash.addFlashAttribute("exito", "Consumo eliminado y total actualizado");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return reservaId == null ? "redirect:/servicios-reserva" : "redirect:/reservas/view/" + reservaId;
    }

    private void cargarListas(Model model) {
        model.addAttribute("reservas", reservaService.listarTodos().stream()
                .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA
                        && r.getEstadoReserva() != EstadoReserva.FINALIZADA)
                .toList());
        model.addAttribute("serviciosAdicionales", servicioAdicionalService.listarTodos());
    }
}
