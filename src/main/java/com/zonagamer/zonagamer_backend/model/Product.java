package com.zonagamer.zonagamer_backend.model;
import java.util.Date;

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

    private boolean isFeatured;

    private boolean active;

    @ServerTimestamp
    private Date fechaCreacion;

    @ServerTimestamp
    private Date fechaActualizacion;

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
