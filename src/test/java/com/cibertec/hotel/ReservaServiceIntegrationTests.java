package com.cibertec.hotel;

import com.cibertec.hotel.dto.ReservaForm;
import com.cibertec.hotel.entity.Cliente;
import com.cibertec.hotel.entity.Empleado;
import com.cibertec.hotel.entity.Habitacion;
import com.cibertec.hotel.entity.TipoHabitacion;
import com.cibertec.hotel.entity.enums.CargoEmpleado;
import com.cibertec.hotel.entity.enums.EstadoHabitacion;
import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.exception.ReglaNegocioException;
import com.cibertec.hotel.repository.ClienteRepository;
import com.cibertec.hotel.repository.EmpleadoRepository;
import com.cibertec.hotel.repository.HabitacionRepository;
import com.cibertec.hotel.repository.TipoHabitacionRepository;
import com.cibertec.hotel.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservaServiceIntegrationTests {

    @Autowired private ReservaService reservaService;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private TipoHabitacionRepository tipoHabitacionRepository;
    @Autowired private HabitacionRepository habitacionRepository;

    private Cliente cliente;
    private Empleado empleado;
    private Habitacion habitacion;

    @BeforeEach
    void prepararDatos() {
        cliente = new Cliente();
        cliente.setNombre("Cliente de Prueba");
        cliente.setRucDni("12345678");
        cliente = clienteRepository.save(cliente);

        empleado = new Empleado();
        empleado.setNombre("Ana");
        empleado.setApellido("Recepción");
        empleado.setDni("87654321");
        empleado.setCargo(CargoEmpleado.RECEPCIONISTA);
        empleado = empleadoRepository.save(empleado);

        TipoHabitacion tipo = new TipoHabitacion();
        tipo.setNombreTipo("Simple Test");
        tipo.setDescripcion("Habitación para pruebas");
        tipo.setPrecioNoche(new BigDecimal("100.00"));
        tipo.setCapacidadPersonas(2);
        tipo = tipoHabitacionRepository.save(tipo);

        habitacion = new Habitacion();
        habitacion.setNumeroHabitacion("T101");
        habitacion.setPiso(1);
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion.setTipoHabitacion(tipo);
        habitacion = habitacionRepository.save(habitacion);
    }

    @Test
    void creaReservaYCalculaMontoEnServidor() {
        ReservaForm form = crearForm(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 4));

        var reserva = reservaService.guardar(form);

        assertThat(reserva.getIdReserva()).isNotNull();
        assertThat(reserva.getDetalles()).hasSize(1);
        assertThat(reserva.getMontoTotal()).isEqualByComparingTo("300.00");
    }

    @Test
    void rechazaReservaSuperpuestaParaLaMismaHabitacion() {
        reservaService.guardar(crearForm(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 4)));

        ReservaForm superpuesta = crearForm(LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 6));

        assertThatThrownBy(() -> reservaService.guardar(superpuesta))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no están disponibles");
    }

    private ReservaForm crearForm(LocalDate ingreso, LocalDate salida) {
        ReservaForm form = new ReservaForm();
        form.setClienteId(cliente.getIdCliente());
        form.setEmpleadoId(empleado.getIdEmpleado());
        form.setFechaIngreso(ingreso);
        form.setFechaSalida(salida);
        form.setCantidadHuespedes(2);
        form.setEstadoReserva(EstadoReserva.CONFIRMADA);
        form.setHabitacionIds(List.of(habitacion.getIdHabitacion()));
        return form;
    }
}
