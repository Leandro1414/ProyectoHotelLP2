package com.cibertec.hotel.service;

import com.cibertec.hotel.entity.TipoHabitacion;

import java.util.List;

public interface TipoHabitacionService {
    List<TipoHabitacion> listarTodos();
    TipoHabitacion buscarPorId(Integer id);
    TipoHabitacion guardar(TipoHabitacion tipoHabitacion);
    void eliminar(Integer id);
}
