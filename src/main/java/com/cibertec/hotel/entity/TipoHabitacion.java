package com.cibertec.hotel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
@Table(name = "tipos_habitacion", uniqueConstraints = @UniqueConstraint(name = "uk_tipo_nombre", columnNames = "nombre_tipo"))
public class TipoHabitacion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Integer idTipo;

    @NotBlank(message = "El nombre del tipo es obligatorio")
    @Size(max = 50)
    @Column(name = "nombre_tipo", nullable = false, unique = true, length = 50)
    private String nombreTipo;

    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio por noche es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Column(name = "precio_noche", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNoche;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es 1")
    @Column(name = "capacidad_personas", nullable = false)
    private Integer capacidadPersonas;
}
