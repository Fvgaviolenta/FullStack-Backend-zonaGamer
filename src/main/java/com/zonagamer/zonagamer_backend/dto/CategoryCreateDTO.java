package com.zonagamer.zonagamer_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Data
public class CategoryCreateDTO {
        
    @NotBlank(message = "El ID es obligatorio")
    @Pattern(
        regexp = "^[a-z0-9-]+$",
        message = "El ID solo puede contener letras minúsculas, números y guiones"
    )
    @Size(min = 2, max = 50, message = "El ID debe tener entre 2 y 50 caracteres")
    private String id;  // ej: "gpu", "teclados-mecanicos"
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreCategoria;  // ej: "Tarjetas Gráficas"
    
    private String parentId;  // null = categoría raíz
}
