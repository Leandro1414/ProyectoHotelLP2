package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.entity.ServicioAdicional;
import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.ServicioAdicionalRepository;
import com.cibertec.hotel.service.ServicioAdicionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServicioAdicionalServiceImpl implements ServicioAdicionalService {
    private final ServicioAdicionalRepository repository;

    @Override
    public List<ServicioAdicional> listarTodos() {
        return repository.findAllByOrderByNombreServicioAsc();
    }

    @Override
    public ServicioAdicional buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado"));
    }

    @Override
    @Transactional
    public ServicioAdicional guardar(ServicioAdicional servicio) {
        servicio.setNombreServicio(servicio.getNombreServicio().trim());
        boolean duplicado = servicio.getIdServicio() == null
                ? repository.existsByNombreServicioIgnoreCase(servicio.getNombreServicio())
                : repository.existsByNombreServicioIgnoreCaseAndIdServicioNot(servicio.getNombreServicio(), servicio.getIdServicio());
        if (duplicado) throw new ReglaNegocioException("Ya existe un servicio con ese nombre");
        return repository.save(servicio);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        ServicioAdicional servicio = buscarPorId(id);
        try {
            repository.delete(servicio);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se puede eliminar el servicio porque aparece en consumos registrados");
        }
    }
}
