package com.cibertec.hotel.repository;

import com.cibertec.hotel.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByRucDniAndIdClienteNot(String rucDni, Integer idCliente);
    boolean existsByRucDni(String rucDni);
    List<Cliente> findByNombreContainingIgnoreCaseOrRucDniContainingIgnoreCaseOrderByNombre(String nombre, String rucDni);
    List<Cliente> findAllByOrderByNombreAsc();
}
