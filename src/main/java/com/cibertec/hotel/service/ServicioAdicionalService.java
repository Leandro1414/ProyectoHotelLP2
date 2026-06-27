package com.cibertec.hotel.service;

import com.cibertec.hotel.entity.ServicioAdicional;

import java.util.List;

public interface ServicioAdicionalService {
    List<ServicioAdicional> listarTodos();
    ServicioAdicional buscarPorId(Integer id);
    ServicioAdicional guardar(ServicioAdicional servicio);
    void eliminar(Integer id);
}
