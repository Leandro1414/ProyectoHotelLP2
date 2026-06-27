package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.entity.Empleado;
import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.EmpleadoRepository;
import com.cibertec.hotel.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmpleadoServiceImpl implements EmpleadoService {
    private final EmpleadoRepository empleadoRepository;

    @Override
    public List<Empleado> listar(String busqueda) {
        if (!StringUtils.hasText(busqueda)) return listarTodos();
        String q = busqueda.trim();
        return empleadoRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrDniContainingIgnoreCaseOrderByApellido(q, q, q);
    }

    @Override
    public List<Empleado> listarTodos() {
        return empleadoRepository.findAllByOrderByApellidoAscNombreAsc();
    }

    @Override
    public Empleado buscarPorId(Integer id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado"));
    }

    @Override
    @Transactional
    public Empleado guardar(Empleado empleado) {
        empleado.setNombre(empleado.getNombre().trim());
        empleado.setApellido(empleado.getApellido().trim());
        empleado.setDni(empleado.getDni().trim());
        empleado.setTelefono(StringUtils.hasText(empleado.getTelefono()) ? empleado.getTelefono().trim() : null);

        boolean duplicado = empleado.getIdEmpleado() == null
                ? empleadoRepository.existsByDni(empleado.getDni())
                : empleadoRepository.existsByDniAndIdEmpleadoNot(empleado.getDni(), empleado.getIdEmpleado());
        if (duplicado) throw new ReglaNegocioException("Ya existe un empleado con ese DNI");
        return empleadoRepository.save(empleado);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Empleado empleado = buscarPorId(id);
        try {
            empleadoRepository.delete(empleado);
            empleadoRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se puede eliminar el empleado porque tiene reservas registradas");
        }
    }

    @Override
    public long contar() {
        return empleadoRepository.count();
    }
}
