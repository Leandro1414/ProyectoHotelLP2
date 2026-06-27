package com.cibertec.hotel.controller;

import com.cibertec.hotel.service.ClienteService;
import com.cibertec.hotel.service.HabitacionService;
import com.cibertec.hotel.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final ClienteService clienteService;
    private final HabitacionService habitacionService;
    private final ReservaService reservaService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalClientes", clienteService.contar());
        model.addAttribute("totalHabitaciones", habitacionService.contar());
        model.addAttribute("totalReservas", reservaService.contar());
        model.addAttribute("reservasPendientes", reservaService.contarPendientes());
        model.addAttribute("reservasActivas", reservaService.contarActivasHoy());
        model.addAttribute("ultimasReservas", reservaService.listarTodos().stream().limit(5).toList());
        return "dashboard";
    }
}
