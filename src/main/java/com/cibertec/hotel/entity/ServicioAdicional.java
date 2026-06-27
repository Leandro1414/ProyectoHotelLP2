package com.cibertec.hotel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "servicios_adicionales", uniqueConstraints = @UniqueConstraint(name = "uk_servicio_nombre", columnNames = "nombre_servicio"))
public class ServicioAdicional implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 100)
    @Column(name = "nombre_servicio", nullable = false, unique = true, length = 100)
    private String nombreServicio;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
}
