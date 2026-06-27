package com.cibertec.hotel.service;

import com.cibertec.hotel.entity.Habitacion;

import java.time.LocalDate;
import java.util.List;

public interface HabitacionService {
    List<Habitacion> listarTodos();
    List<Habitacion> listarOperativas();
    List<Habitacion> buscarDisponibles(LocalDate ingreso, LocalDate salida, Integer reservaId);
    Habitacion buscarPorId(Integer id);
    Habitacion guardar(Habitacion habitacion);
    void eliminar(Integer id);
    long contar();
}
