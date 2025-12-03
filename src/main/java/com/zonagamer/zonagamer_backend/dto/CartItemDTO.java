package com.zonagamer.zonagamer_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private String productId;

    private String productName;

    private String imageUrl;

    private Integer quantity;

    private Double precio;

    private Double subtotal;

    private boolean disponibilidad;
    
}
