package com.cibertec.hotel.entity;

import com.cibertec.hotel.entity.enums.EstadoReserva;
import com.cibertec.hotel.entity.enums.TipoComprobante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reservas", uniqueConstraints = @UniqueConstraint(name = "uk_reserva_comprobante", columnNames = "numero_comprobante"))
public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @CreationTimestamp
    @Column(name = "fecha_reserva", nullable = false, updatable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reserva", nullable = false, length = 20)
    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", length = 20)
    private TipoComprobante tipoComprobante;

    @Column(name = "numero_comprobante", unique = true, length = 50)
    private String numeroComprobante;

    @Column(name = "cantidad_huespedes", nullable = false)
    private Integer cantidadHuespedes = 1;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idDetalle ASC")
    private Set<DetalleReserva> detalles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaConsumo DESC")
    private Set<ServicioReserva> servicios = new LinkedHashSet<>();
}
