package com.cibertec.hotel.controller;

import com.cibertec.hotel.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {
    private final UsuarioRepository usuarioRepository;

    @ModelAttribute("usuarioActual")
    public String usuarioActual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return usuarioRepository.findByCorreoIgnoreCase(authentication.getName())
                .map(usuario -> usuario.getNombreCompleto())
                .orElse(authentication.getName());
    }

    @ModelAttribute("esAdmin")
    public boolean esAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
