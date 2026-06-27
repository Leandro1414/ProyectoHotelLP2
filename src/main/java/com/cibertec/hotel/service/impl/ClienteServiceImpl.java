package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.entity.Cliente;
import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.ClienteRepository;
import com.cibertec.hotel.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;

    @Override
    public List<Cliente> listar(String busqueda) {
        if (!StringUtils.hasText(busqueda)) {
            return listarTodos();
        }
        String q = busqueda.trim();
        return clienteRepository.findByNombreContainingIgnoreCaseOrRucDniContainingIgnoreCaseOrderByNombre(q, q);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAllByOrderByNombreAsc();
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        cliente.setNombre(cliente.getNombre().trim());
        cliente.setRucDni(cliente.getRucDni().trim());
        cliente.setDireccion(normalizar(cliente.getDireccion()));
        cliente.setTelefono(normalizar(cliente.getTelefono()));

        boolean duplicado = cliente.getIdCliente() == null
                ? clienteRepository.existsByRucDni(cliente.getRucDni())
                : clienteRepository.existsByRucDniAndIdClienteNot(cliente.getRucDni(), cliente.getIdCliente());
        if (duplicado) {
            throw new ReglaNegocioException("Ya existe un cliente con ese DNI o RUC");
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Cliente cliente = buscarPorId(id);
        try {
            clienteRepository.delete(cliente);
            clienteRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se puede eliminar el cliente porque tiene reservas registradas");
        }
    }

    @Override
    public long contar() {
        return clienteRepository.count();
    }

    private String normalizar(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : null;
    }
}
