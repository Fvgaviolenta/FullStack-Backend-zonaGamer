package com.zonagamer.zonagamer_backend.dto;


import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Data
public class AddToCartDTO {
    

    @NotBlank(message = "El ID del producto es obligatorio")
    private String productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;
}
