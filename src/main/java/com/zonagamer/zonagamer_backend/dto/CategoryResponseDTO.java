package com.zonagamer.zonagamer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    
    private String id;
    
    private String nombreCategoria;
    
    private String parentId;
    
    private boolean active;
}
