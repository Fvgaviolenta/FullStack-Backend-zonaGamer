package com.zonagamer.zonagamer_backend.dto;

import lombok.Data;
import com.zonagamer.zonagamer_backend.model.CalendarEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
public class CalendarEventDTO {
    
    @NotBlank(message = "El titulo es obligatorio")
    @Size(min = 3, max = 100, message = "El titulo debe tener entre 3 y 100 caracteres")
    private String titulo;

    @Size(max = 500, message = "La descripcion no puede exceder los 500 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaDeInicio;

    private LocalDateTime fechaDeTermino;

    @NotNull(message = "El tipo de evento es obligatorio")
    private CalendarEvent.EventType type;

    private String creadoPor;
}
