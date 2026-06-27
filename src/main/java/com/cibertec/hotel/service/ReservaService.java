package com.cibertec.hotel.service;

import com.cibertec.hotel.dto.ReservaForm;
import com.cibertec.hotel.entity.Reserva;

import java.util.List;

public interface ReservaService {
    List<Reserva> listar(String busqueda);
    List<Reserva> listarTodos();
    Reserva buscarPorId(Integer id);
    Reserva guardar(ReservaForm form);
    ReservaForm convertirAForm(Integer id);
    void cancelar(Integer id);
    void finalizar(Integer id);
    long contar();
    long contarPendientes();
    long contarActivasHoy();
}
