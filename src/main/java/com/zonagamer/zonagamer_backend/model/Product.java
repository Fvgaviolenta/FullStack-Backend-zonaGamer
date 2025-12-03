package com.zonagamer.zonagamer_backend.model;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Product {
    
    private String id;

    private String nombreProducto;

    private String descripcionProducto;

    private Double precio;

    private Integer stock;

    private String imageUrl;

    private String categoryId;

    private boolean featured;

    private boolean active;

    @ServerTimestamp
    private Date fechaCreacion;

    @ServerTimestamp
    private Date fechaActualizacion;

    // Métodos explícitos para compatibilidad con Firestore
    // Firestore almacena el campo como "isFeatured" en la base de datos
    @PropertyName("isFeatured")
    public boolean getIsFeatured() {
        return featured;
    }
    
    @PropertyName("isFeatured")
    public void setIsFeatured(boolean isFeatured) {
        this.featured = isFeatured;
    }
    
    // Método estándar Java para código (Lombok lo generaría pero necesitamos ser explícitos)
    @JsonProperty("featured")
    public boolean isFeatured() {
        return featured;
    }

    public boolean hasStock(int quantity){
        return stock >= quantity && active;
    }

    public void reduceStock(int quantity){
        if (quantity > stock){
            throw new IllegalArgumentException(
                "No hay suficiente stock. Disponible: " + stock
            );
        }
        this.stock -= quantity;
    }
}
