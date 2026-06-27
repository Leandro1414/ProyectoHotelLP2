package com.cibertec.hotel.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroForm {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo válido")
    @Size(max = 150)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$", message = "Debe incluir mayúscula, minúscula y número")
    private String password;

    @NotBlank(message = "Confirme la contraseña")
    private String confirmarPassword;

    @AssertTrue(message = "Las contraseñas no coinciden")
    public boolean isPasswordCoincide() {
        return password != null && password.equals(confirmarPassword);
    }
}
