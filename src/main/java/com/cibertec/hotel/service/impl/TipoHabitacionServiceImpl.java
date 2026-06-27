package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.entity.TipoHabitacion;
import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.TipoHabitacionRepository;
import com.cibertec.hotel.service.TipoHabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoHabitacionServiceImpl implements TipoHabitacionService {
    private final TipoHabitacionRepository repository;

    @Override
    public List<TipoHabitacion> listarTodos() {
        return repository.findAllByOrderByPrecioNocheAsc();
    }

    @Override
    public TipoHabitacion buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de habitación no encontrado"));
    }

    @Override
    @Transactional
    public TipoHabitacion guardar(TipoHabitacion tipo) {
        tipo.setNombreTipo(tipo.getNombreTipo().trim());
        boolean duplicado = tipo.getIdTipo() == null
                ? repository.existsByNombreTipoIgnoreCase(tipo.getNombreTipo())
                : repository.existsByNombreTipoIgnoreCaseAndIdTipoNot(tipo.getNombreTipo(), tipo.getIdTipo());
        if (duplicado) throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        return repository.save(tipo);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        TipoHabitacion tipo = buscarPorId(id);
        try {
            repository.delete(tipo);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se puede eliminar la categoría porque tiene habitaciones asociadas");
        }
    }
}
