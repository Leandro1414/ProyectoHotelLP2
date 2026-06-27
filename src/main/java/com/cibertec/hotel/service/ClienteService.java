package com.cibertec.hotel.service;

import com.cibertec.hotel.entity.Cliente;

import java.util.List;

public interface ClienteService {
    List<Cliente> listar(String busqueda);
    List<Cliente> listarTodos();
    Cliente buscarPorId(Integer id);
    Cliente guardar(Cliente cliente);
    void eliminar(Integer id);
    long contar();
}
