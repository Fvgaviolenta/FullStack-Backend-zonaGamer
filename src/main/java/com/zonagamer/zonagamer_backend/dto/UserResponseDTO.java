package com.zonagamer.zonagamer_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private String id;
    
    private String email;
    
    private String nombre;
    
    private String apellido;
    
    private String nombreCompleto;
    
    private String numeroDeTelefono;

    @JsonProperty("isAdmin")
    private boolean admin;
    
    private boolean active;
    
    private String fechaCreacion;
}
