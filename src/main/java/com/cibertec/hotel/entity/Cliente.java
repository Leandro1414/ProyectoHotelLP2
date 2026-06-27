package com.cibertec.hotel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "clientes", uniqueConstraints = @UniqueConstraint(name = "uk_cliente_ruc_dni", columnNames = "ruc_dni"))
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "El DNI o RUC es obligatorio")
    @Pattern(regexp = "\\d{8}|\\d{11}", message = "Ingrese un DNI de 8 dígitos o un RUC de 11 dígitos")
    @Column(name = "ruc_dni", nullable = false, unique = true, length = 11)
    private String rucDni;

    @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
    @Column(length = 255)
    private String direccion;

    @Pattern(regexp = "^$|\\d{7,15}$", message = "El teléfono debe contener entre 7 y 15 dígitos")
    @Column(length = 15)
    private String telefono;
}
