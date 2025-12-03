package com.zonagamer.zonagamer_backend.model;

import lombok.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String productId;

    private String productName;

    private String imageUrl;

    private Integer quantity;

    private Double precio;

    public Double getSubtotal(){
        return precio * quantity;
    }


}

