package com.zonagamer.zonagamer_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import lombok.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private String id;

    private String email;
    
    private String password;

    private String nombre;

    private String apellido;

    private boolean admin;

    private boolean active;

    private String numeroDeTelefono;

    private Integer puntajeCliente;

    @ServerTimestamp
    private Timestamp fechaCreacion;

    @ServerTimestamp
    private Timestamp fechaActualizacion;

    public String obtenerNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    // Métodos explícitos para Firestore
    @PropertyName("isAdmin")
    public boolean getIsAdmin() {
        return admin;
    }
    
    @PropertyName("isAdmin")
    public void setIsAdmin(boolean isAdmin) {
        this.admin = isAdmin;
    }
    
    // Método estándar para código Java
    @JsonProperty("admin")
    public boolean isAdmin() {
        return admin;
    }
}
