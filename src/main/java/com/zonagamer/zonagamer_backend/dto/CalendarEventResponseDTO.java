package com.zonagamer.zonagamer_backend.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventResponseDTO {
    private String id;
    
    private String titulo;
    
    private String descripcion;
    
    private LocalDateTime fechaDeInicio;  
    
    private LocalDateTime fechaDeTermino; 
    
    private String type;  
    
    private boolean completed;
    
    private String creadoPor;  // ID del admin que lo creó
    
    private LocalDateTime fechaCreacion;
}
