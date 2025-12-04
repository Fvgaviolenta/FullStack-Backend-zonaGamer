package com.zonagamer.zonagamer_backend.model;

import com.google.cloud.Timestamp;
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

    private Timestamp fechaDeInicio;

    private Timestamp fechaDeTermino;

    private EventType type;

    private boolean completed;

    private String creadoPor;

    @ServerTimestamp
    private Timestamp fechaDeCreacion;

    public enum EventType{
        REUNION,
        TAREA,
        MANTENIMIENTO,
        FECHA_LIMITE,
        OTROS
    }
}
