package com.zonagamer.zonagamer_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String productId;

    private String productName;

    private Integer quantity;

    private Double precioEnCompra;

    public Double getSubtotal(){
        return precioEnCompra * quantity;
    }
}
