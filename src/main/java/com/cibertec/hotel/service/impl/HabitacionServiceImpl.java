package com.cibertec.hotel.service.impl;

import com.cibertec.hotel.entity.Habitacion;
import com.cibertec.hotel.entity.TipoHabitacion;
import com.cibertec.hotel.entity.enums.EstadoHabitacion;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.exception.RecursoNoEncontradoException;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.HabitacionRepository;
import com.cibertec.hotel.repository.TipoHabitacionRepository;
import com.cibertec.hotel.service.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitacionServiceImpl implements HabitacionService {
    private final HabitacionRepository habitacionRepository;
    private final TipoHabitacionRepository tipoRepository;

    @Override
    public List<Habitacion> listarTodos() {
        return habitacionRepository.findAllByOrderByNumeroHabitacionAsc();
    }

    @Override
    public List<Habitacion> listarOperativas() {
        return habitacionRepository.findByEstadoOrderByNumeroHabitacionAsc(EstadoHabitacion.DISPONIBLE);
    }

    @Override
    public List<Habitacion> buscarDisponibles(LocalDate ingreso, LocalDate salida, Integer reservaId) {
        validarFechas(ingreso, salida);
        return habitacionRepository.buscarDisponibles(ingreso, salida, reservaId,
                EstadoHabitacion.DISPONIBLE, EstadoReserva.CANCELADA);
    }

    @Override
    public Habitacion buscarPorId(Integer id) {
        return habitacionRepository.buscarPorIdConTipo(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Habitación no encontrada"));
    }

    @Override
    @Transactional
    public Habitacion guardar(Habitacion habitacion) {
        habitacion.setNumeroHabitacion(habitacion.getNumeroHabitacion().trim().toUpperCase());
        Integer tipoId = habitacion.getTipoHabitacion() == null ? null : habitacion.getTipoHabitacion().getIdTipo();
        if (tipoId == null) {
            throw new ReglaNegocioException("Seleccione un tipo de habitación válido");
        }
        TipoHabitacion tipo = tipoRepository.findById(tipoId)
                .orElseThrow(() -> new ReglaNegocioException("Seleccione un tipo de habitación válido"));
        habitacion.setTipoHabitacion(tipo);

        boolean duplicado = habitacion.getIdHabitacion() == null
                ? habitacionRepository.existsByNumeroHabitacion(habitacion.getNumeroHabitacion())
                : habitacionRepository.existsByNumeroHabitacionAndIdHabitacionNot(habitacion.getNumeroHabitacion(), habitacion.getIdHabitacion());
        if (duplicado) throw new ReglaNegocioException("Ya existe una habitación con ese número");
        return habitacionRepository.save(habitacion);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Habitacion habitacion = buscarPorId(id);
        try {
            habitacionRepository.delete(habitacion);
            habitacionRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se puede eliminar la habitación porque forma parte del historial de reservas");
        }
    }

    @Override
    public long contar() {
        return habitacionRepository.count();
    }

    private void validarFechas(LocalDate ingreso, LocalDate salida) {
        if (ingreso == null || salida == null || !salida.isAfter(ingreso)) {
            throw new ReglaNegocioException("La fecha de salida debe ser posterior a la fecha de ingreso");
        }
    }
}
