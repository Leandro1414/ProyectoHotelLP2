package com.cibertec.hotel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "detalle_reservas", uniqueConstraints = @UniqueConstraint(name = "uk_detalle_reserva_habitacion", columnNames = {"id_reserva", "id_habitacion"}))
public class DetalleReserva implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAplicado;
}
