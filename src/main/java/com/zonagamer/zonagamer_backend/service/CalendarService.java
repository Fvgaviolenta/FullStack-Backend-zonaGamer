package com.zonagamer.zonagamer_backend.service;

import com.google.cloud.Timestamp;
import com.zonagamer.zonagamer_backend.dto.CalendarEventDTO;
import com.zonagamer.zonagamer_backend.dto.CalendarEventResponseDTO;
import com.zonagamer.zonagamer_backend.exception.ResourceNotFoundException;
import com.zonagamer.zonagamer_backend.model.CalendarEvent;
import com.zonagamer.zonagamer_backend.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {
    
    private final CalendarEventRepository calendarEventRepository;
    
    public CalendarEventResponseDTO createEvent(CalendarEventDTO dto, String createdBy) 
            throws ExecutionException, InterruptedException {
        
        log.info("Creando evento en calendario: {}", dto.getTitulo());
        
        // Validar fechas
        if (dto.getFechaDeTermino() != null && dto.getFechaDeTermino().isBefore(dto.getFechaDeInicio())) {
            throw new IllegalArgumentException(
                "La fecha de fin no puede ser anterior a la fecha de inicio"
            );
        }
        
        // Convertir LocalDateTime a Timestamp
        // Si el usuario solo proporciona fecha, agregar hora por defecto (9:00 AM)
        LocalDateTime startDateTime = dto.getFechaDeInicio();
        if (startDateTime.getHour() == 0 && startDateTime.getMinute() == 0) {
            startDateTime = startDateTime.withHour(9).withMinute(0);
        }
        
        LocalDateTime endDateTime = dto.getFechaDeTermino();
        if (endDateTime != null && endDateTime.getHour() == 0 && endDateTime.getMinute() == 0) {
            endDateTime = endDateTime.withHour(18).withMinute(0); // 6:00 PM por defecto
        }
        
        // Crear evento
        CalendarEvent event = CalendarEvent.builder()
            .titulo(dto.getTitulo())
            .descripcion(dto.getDescripcion())
            .fechaDeInicio(convertToTimestamp(startDateTime))
            .fechaDeTermino(endDateTime != null ? convertToTimestamp(endDateTime) : null)
            .type(dto.getType())
            .completed(false)
            .creadoPor(createdBy)
            .build();
        
        // Guardar en Firestore
        String eventId = calendarEventRepository.save(event);
        event.setId(eventId);
        
        log.info("✅ Evento creado: {} ({})", event.getTitulo(), eventId);
        
        return mapToResponseDTO(event);
    }
    
    public List<CalendarEventResponseDTO> getAllEvents() 
            throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo todos los eventos del calendario");
        
        List<CalendarEvent> events = calendarEventRepository.findAll();
        
        return events.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<CalendarEventResponseDTO> getPendingEvents() 
            throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo eventos pendientes");
        
        List<CalendarEvent> events = calendarEventRepository.findAll();
        
        return events.stream()
            .filter(event -> !event.isCompleted())
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<CalendarEventResponseDTO> getEventsByDateRange(
            LocalDateTime startDate, 
            LocalDateTime endDate
    ) throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo eventos entre {} y {}", startDate, endDate);
        
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                "La fecha de fin no puede ser anterior a la fecha de inicio"
            );
        }
        
        Timestamp start = convertToTimestamp(startDate);
        Timestamp end = convertToTimestamp(endDate);
        
        List<CalendarEvent> events = calendarEventRepository.findByDateRange(start, end);
        
        return events.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public CalendarEventResponseDTO getEventById(String eventId) 
            throws ExecutionException, InterruptedException {
        
        log.debug("Buscando evento: {}", eventId);
        
        CalendarEvent event = calendarEventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento no encontrado: " + eventId
            ));
        
        return mapToResponseDTO(event);
    }
    
    public CalendarEventResponseDTO updateEvent(String eventId, CalendarEventDTO dto) 
            throws ExecutionException, InterruptedException {
        
        log.info("Actualizando evento: {}", eventId);
        
        CalendarEvent event = calendarEventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento no encontrado: " + eventId
            ));
        
        // Validar fechas
        if (dto.getFechaDeTermino() != null && dto.getFechaDeTermino().isBefore(dto.getFechaDeInicio())) {
            throw new IllegalArgumentException(
                "La fecha de fin no puede ser anterior a la fecha de inicio"
            );
        }
        
        // Convertir fechas con hora por defecto si es necesario
        LocalDateTime startDateTime = dto.getFechaDeInicio();
        if (startDateTime.getHour() == 0 && startDateTime.getMinute() == 0) {
            startDateTime = startDateTime.withHour(9).withMinute(0);
        }
        
        LocalDateTime endDateTime = dto.getFechaDeTermino();
        if (endDateTime != null && endDateTime.getHour() == 0 && endDateTime.getMinute() == 0) {
            endDateTime = endDateTime.withHour(18).withMinute(0);
        }
        
        // Actualizar campos
        event.setTitulo(dto.getTitulo());
        event.setDescripcion(dto.getDescripcion());
        event.setFechaDeInicio(convertToTimestamp(startDateTime));
        event.setFechaDeTermino(endDateTime != null ? convertToTimestamp(endDateTime) : null);
        event.setType(dto.getType());
        event.setCompleted(dto.isCompleted());
        
        // Guardar cambios
        calendarEventRepository.update(eventId, event);
        
        log.info("✅ Evento actualizado: {}", eventId);
        
        return mapToResponseDTO(event);
    }
    
    public CalendarEventResponseDTO markAsCompleted(String eventId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Marcando evento como completado: {}", eventId);
        
        CalendarEvent event = calendarEventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento no encontrado: " + eventId
            ));
        
        event.setCompleted(true);
        
        calendarEventRepository.update(eventId, event);
        
        log.info("✅ Evento marcado como completado: {}", eventId);
        
        return mapToResponseDTO(event);
    }
    
    public CalendarEventResponseDTO markAsPending(String eventId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Marcando evento como pendiente: {}", eventId);
        
        CalendarEvent event = calendarEventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento no encontrado: " + eventId
            ));
        
        event.setCompleted(false);
        
        calendarEventRepository.update(eventId, event);
        
        log.info("✅ Evento marcado como pendiente: {}", eventId);
        
        return mapToResponseDTO(event);
    }
    
    public void deleteEvent(String eventId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Eliminando evento: {}", eventId);
        
        calendarEventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento no encontrado: " + eventId
            ));
        
        calendarEventRepository.delete(eventId);
        
        log.info("✅ Evento eliminado: {}", eventId);
    }
    
    public long countPendingEvents() throws ExecutionException, InterruptedException {
        List<CalendarEvent> events = calendarEventRepository.findAll();
        return events.stream().filter(event -> !event.isCompleted()).count();
    }
    
    public List<CalendarEventResponseDTO> getUpcomingEvents(int days) 
            throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo eventos de los próximos {} días", days);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        
        return getEventsByDateRange(now, future);
    }
    
    // Métodos de conversión entre LocalDateTime y Timestamp
    private Timestamp convertToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        java.util.Date date = java.util.Date.from(
            localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );
        return Timestamp.of(date);
    }
    
    private LocalDateTime convertFromTimestamp(Timestamp timestamp) {
        if (timestamp == null) return null;
        return LocalDateTime.ofInstant(
            timestamp.toDate().toInstant(),
            ZoneId.systemDefault()
        );
    }
    
    private CalendarEventResponseDTO mapToResponseDTO(CalendarEvent event) {
        return CalendarEventResponseDTO.builder()
            .id(event.getId())
            .titulo(event.getTitulo())
            .descripcion(event.getDescripcion())
            .fechaDeInicio(convertFromTimestamp(event.getFechaDeInicio()))
            .fechaDeTermino(event.getFechaDeTermino() != null ? 
                convertFromTimestamp(event.getFechaDeTermino()) : null)
            .type(event.getType().name())
            .completed(event.isCompleted())
            .creadoPor(event.getCreadoPor())
            .fechaCreacion(event.getFechaDeCreacion() != null ? 
                convertFromTimestamp(event.getFechaDeCreacion()) : null)
            .build();
    }
}
