package com.zonagamer.zonagamer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private String id;

    private String userId;

    private List<CartItemDTO> items;

    private Double subtotal;

    private Double iva;

    private Double total;

    private int totalItems;
}
