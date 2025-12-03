package com.zonagamer.zonagamer_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.*;


@Data
public class ProductCreateDTO {
    

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombreProducto;

    @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Max(value = 10000000, message = "El precio no puede exceder 10 millones")
    private Double price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 1000, message = "El stock no puede exceder 10000 unidades")
    private Integer stock;

    @NotBlank(message = "La categoria es obligatoria")
    private String categoryId;

    private String imageUrl;

    private boolean isFeatured = false;

    // Método getter explícito para que Jackson lo reconozca correctamente
    public boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
}
