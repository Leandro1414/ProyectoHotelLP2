package com.cibertec.hotel.controller;

import com.cibertec.hotel.dto.HabitacionDisponibleDto;
import com.cibertec.hotel.dto.ReservaForm;
import com.cibertec.hotel.entity.Habitacion;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.entity.enums.TipoComprobante;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.ClienteService;
import com.cibertec.hotel.service.EmpleadoService;
import com.cibertec.hotel.service.HabitacionService;
import com.cibertec.hotel.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;
    private final HabitacionService habitacionService;

    @ModelAttribute("estadosReserva")
    public List<EstadoReserva> estadosReserva() {
        return Arrays.stream(EstadoReserva.values())
                .filter(estado -> estado != EstadoReserva.CANCELADA && estado != EstadoReserva.FINALIZADA)
                .toList();
    }

    @ModelAttribute("tiposComprobante")
    public TipoComprobante[] tiposComprobante() {
        return TipoComprobante.values();
    }

    @GetMapping
    public String index(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("reservas", reservaService.listar(q));
        model.addAttribute("q", q);
        return "reserva/index";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        model.addAttribute("reserva", reservaService.buscarPorId(id));
        return "reserva/view";
    }

    @GetMapping("/create")
    public String create(Model model) {
        ReservaForm form = new ReservaForm();
        form.setFechaIngreso(LocalDate.now().plusDays(1));
        form.setFechaSalida(LocalDate.now().plusDays(2));
        model.addAttribute("reservaForm", form);
        cargarFormulario(model, form);
        return "reserva/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        ReservaForm form = reservaService.convertirAForm(id);
        model.addAttribute("reservaForm", form);
        cargarFormulario(model, form);
        return "reserva/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("reservaForm") ReservaForm form,
                       BindingResult result,
                       Model model,
                       RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarFormulario(model, form);
            return "reserva/create";
        }
        try {
            var guardada = reservaService.guardar(form);
            flash.addFlashAttribute("exito", "Reserva guardada y total calculado correctamente");
            return "redirect:/reservas/view/" + guardada.getIdReserva();
        } catch (ReglaNegocioException ex) {
            result.reject("reserva.regla", ex.getMessage());
            cargarFormulario(model, form);
            return "reserva/create";
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            reservaService.cancelar(id);
            flash.addFlashAttribute("exito", "Reserva anulada sin eliminar su historial");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservas/view/" + id;
    }

    @PostMapping("/finish/{id}")
    public String finish(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            reservaService.finalizar(id);
            flash.addFlashAttribute("exito", "Reserva finalizada correctamente");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservas/view/" + id;
    }

    @GetMapping("/api/habitaciones-disponibles")
    @ResponseBody
    public List<HabitacionDisponibleDto> disponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngreso,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam(required = false) Integer reservaId) {
        return habitacionService.buscarDisponibles(fechaIngreso, fechaSalida, reservaId).stream()
                .map(h -> new HabitacionDisponibleDto(
                        h.getIdHabitacion(),
                        h.getNumeroHabitacion(),
                        h.getTipoHabitacion().getNombreTipo(),
                        h.getTipoHabitacion().getCapacidadPersonas(),
                        h.getTipoHabitacion().getPrecioNoche()))
                .toList();
    }

    private void cargarFormulario(Model model, ReservaForm form) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("empleados", empleadoService.listarTodos());
        List<Habitacion> habitaciones;
        try {
            habitaciones = habitacionService.buscarDisponibles(
                    form.getFechaIngreso(), form.getFechaSalida(), form.getIdReserva());
        } catch (ReglaNegocioException ex) {
            habitaciones = habitacionService.listarOperativas();
        }
        model.addAttribute("habitaciones", habitaciones);
    }
}
