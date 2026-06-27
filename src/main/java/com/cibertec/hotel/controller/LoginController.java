package com.cibertec.hotel.controller;

import com.cibertec.hotel.dto.RegistroForm;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String inicio(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        if (!model.containsAttribute("registroForm")) {
            model.addAttribute("registroForm", new RegistroForm());
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registroForm") RegistroForm form,
                            BindingResult result,
                            RedirectAttributes flash) {
        if (result.hasErrors()) return "registro";
        try {
            usuarioService.registrar(form);
            flash.addFlashAttribute("exito", "Cuenta creada correctamente. Ya puede iniciar sesión.");
            return "redirect:/login";
        } catch (ReglaNegocioException ex) {
            result.rejectValue("correo", "correo.duplicado", ex.getMessage());
            return "registro";
        }
    }
}
