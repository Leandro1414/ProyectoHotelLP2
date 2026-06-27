package com.cibertec.hotel.service;

import com.cibertec.hotel.entity.Empleado;

import java.util.List;

public interface EmpleadoService {
    List<Empleado> listar(String busqueda);
    List<Empleado> listarTodos();
    Empleado buscarPorId(Integer id);
    Empleado guardar(Empleado empleado);
    void eliminar(Integer id);
    long contar();
}
