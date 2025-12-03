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
    
    private Double subtotal;

    // Método helper para calcular subtotal si no existe
    public Double getSubtotal(){
        if (subtotal != null) {
            return subtotal;
        }
        return precioEnCompra != null && quantity != null 
            ? precioEnCompra * quantity 
            : 0.0;
    }
}
