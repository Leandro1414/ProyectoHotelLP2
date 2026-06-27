package com.cibertec.hotel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "servicios_reserva")
public class ServicioReserva implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio_reserva")
    private Integer idServicioReserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private ServicioAdicional servicioAdicional;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAplicado;

    @CreationTimestamp
    @Column(name = "fecha_consumo", nullable = false, updatable = false)
    private LocalDateTime fechaConsumo;

    public BigDecimal getSubtotal() {
        if (precioAplicado == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioAplicado.multiply(BigDecimal.valueOf(cantidad));
    }
}
