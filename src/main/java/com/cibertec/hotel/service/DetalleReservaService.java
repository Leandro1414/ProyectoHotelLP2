package com.cibertec.hotel.service;

import com.cibertec.hotel.dto.DetalleReservaForm;
import com.cibertec.hotel.entity.DetalleReserva;

import java.util.List;

public interface DetalleReservaService {
    List<DetalleReserva> listarTodos();
    void agregar(DetalleReservaForm form);
    void eliminar(Integer id);
}
