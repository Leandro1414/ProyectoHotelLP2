package com.cibertec.hotel.entity;

import com.cibertec.hotel.entity.enums.EstadoHabitacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "habitaciones", uniqueConstraints = @UniqueConstraint(name = "uk_habitacion_numero", columnNames = "numero_habitacion"))
public class Habitacion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @NotNull(message = "Seleccione un tipo de habitación")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @NotBlank(message = "El número de habitación es obligatorio")
    @Size(max = 10)
    @Column(name = "numero_habitacion", nullable = false, unique = true, length = 10)
    private String numeroHabitacion;

    @NotNull(message = "El piso es obligatorio")
    @Min(value = 1, message = "El piso debe ser mayor o igual a 1")
    @Column(nullable = false)
    private Integer piso;

    @NotNull(message = "Seleccione un estado")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoHabitacion estado = EstadoHabitacion.DISPONIBLE;
}
