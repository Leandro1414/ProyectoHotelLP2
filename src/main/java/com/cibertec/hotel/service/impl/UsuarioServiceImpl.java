package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.dto.RegistroForm;
import com.cibertec.hotel.entity.Usuario;
import com.cibertec.hotel.entity.enums.RolUsuario;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.UsuarioRepository;
import com.cibertec.hotel.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario registrar(RegistroForm form) {
        String correo = form.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(form.getNombre().trim());
        usuario.setApellido(form.getApellido().trim());
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(form.getPassword()));
        usuario.setRol(RolUsuario.RECEPCIONISTA);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }
}
