package com.zonagamer.zonagamer_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Data
public class UserLoginDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email es invalido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
}
