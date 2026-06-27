package com.cibertec.hotel.controller;

import com.cibertec.hotel.entity.Cliente;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @GetMapping
    public String index(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("clientes", clienteService.listar(q));
        model.addAttribute("q", q);
        return "cliente/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        return "cliente/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("cliente") Cliente cliente,
                       BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()) return "cliente/create";
        try {
            clienteService.guardar(cliente);
            flash.addFlashAttribute("exito", "Cliente guardado correctamente");
            return "redirect:/clientes";
        } catch (ReglaNegocioException ex) {
            result.rejectValue("rucDni", "cliente.duplicado", ex.getMessage());
            return "cliente/create";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            clienteService.eliminar(id);
            flash.addFlashAttribute("exito", "Cliente eliminado correctamente");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/clientes";
    }
}
