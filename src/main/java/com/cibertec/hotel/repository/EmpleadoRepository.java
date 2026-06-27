package com.cibertec.hotel.repository;

import com.cibertec.hotel.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    boolean existsByDniAndIdEmpleadoNot(String dni, Integer idEmpleado);
    boolean existsByDni(String dni);
    List<Empleado> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrDniContainingIgnoreCaseOrderByApellido(String nombre, String apellido, String dni);
    List<Empleado> findAllByOrderByApellidoAscNombreAsc();
}
