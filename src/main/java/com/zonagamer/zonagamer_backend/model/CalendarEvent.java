package com.zonagamer.zonagamer_backend.model;

import java.util.Date;

import com.google.cloud.firestore.annotation.ServerTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {
    private String id;

    private String titulo;

    private String descripcion;

    private Date fechaDeInicio;

    private Date fechaDeTermino;

    private EventType type;

    private boolean completed;

    private String creadoPor;

    @ServerTimestamp
    private Date FechaDeCreacion;

    public enum EventType{
        REUNION,
        TAREA,
        MANTENIMIENTO,
        FECHA_LIMITE,
        OTROS
    }
}
