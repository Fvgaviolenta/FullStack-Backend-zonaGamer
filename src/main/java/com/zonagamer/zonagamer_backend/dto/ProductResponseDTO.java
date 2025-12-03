package com.zonagamer.zonagamer_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private String id;

    private String nombre;

    private String descripcion;

    private Double precio;

    private Integer stock;

    private String imageUrl;

    private String categoryId;

    private boolean isFeatured;

    private boolean disponibilidad;

    private String fechaCreacion;
}
