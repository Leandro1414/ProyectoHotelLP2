package com.cibertec.hotel.entity;

import com.cibertec.hotel.entity.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_correo", columnNames = "correo"))
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol = RolUsuario.RECEPCIONISTA;

    @Column(nullable = false)
    private boolean activo = true;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
