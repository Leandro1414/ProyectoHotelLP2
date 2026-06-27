package com.cibertec.hotel.service;

import com.cibertec.hotel.dto.RegistroForm;
import com.cibertec.hotel.entity.Usuario;

public interface UsuarioService {
    Usuario registrar(RegistroForm form);
}
