package com.zonagamer.zonagamer_backend.model;

import lombok.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    private String id;

    private String nombreCategoria;

    private String parentId;

    private boolean active;

    public boolean isRoot(){
        return parentId == null || parentId.isEmpty();
    }
}
