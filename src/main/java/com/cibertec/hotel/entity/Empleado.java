package com.cibertec.hotel.entity;

import com.cibertec.hotel.entity.enums.CargoEmpleado;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "empleados", uniqueConstraints = @UniqueConstraint(name = "uk_empleado_dni", columnNames = "dni"))
public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe contener 8 dígitos")
    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @NotNull(message = "Seleccione un cargo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CargoEmpleado cargo;

    @Pattern(regexp = "^$|\\d{7,15}$", message = "El teléfono debe contener entre 7 y 15 dígitos")
    @Column(length = 15)
    private String telefono;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
