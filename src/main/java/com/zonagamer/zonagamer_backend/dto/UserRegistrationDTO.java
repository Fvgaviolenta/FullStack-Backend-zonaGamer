package com.zonagamer.zonagamer_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UserRegistrationDTO {
    

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email invalido")
    private String email;

    @NotBlank(message = "Contraseña debe ser obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 a 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 a 50 caracteres")
    private String apellido;

    @Pattern(
        regexp = "^\\+?56?[0-9]{9}$",
        message = "Numero de telefono chileno invalido (ej: +56912345678 o 912345678)"
    )
    private String numeroDeTelefono;


}
