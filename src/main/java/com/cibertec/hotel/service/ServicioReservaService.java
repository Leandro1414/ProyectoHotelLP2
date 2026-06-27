package com.cibertec.hotel.service;

import com.cibertec.hotel.dto.ServicioReservaForm;
import com.cibertec.hotel.entity.ServicioReserva;

import java.util.List;

public interface ServicioReservaService {
    List<ServicioReserva> listarTodos();
    ServicioReservaForm convertirAForm(Integer id);
    ServicioReserva guardar(ServicioReservaForm form);
    void eliminar(Integer id);
}
