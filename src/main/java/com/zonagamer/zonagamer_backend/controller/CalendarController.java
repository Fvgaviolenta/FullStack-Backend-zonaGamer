package com.zonagamer.zonagamer_backend.controller;

import com.zonagamer.zonagamer_backend.dto.CalendarEventDTO;
import com.zonagamer.zonagamer_backend.dto.CalendarEventResponseDTO;
import com.zonagamer.zonagamer_backend.security.UserPrincipal;
import com.zonagamer.zonagamer_backend.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CalendarController {
    

    private final CalendarService calendarService;


    @PostMapping("/eventos")
    public ResponseEntity<CalendarEventResponseDTO> crearEvento(
        @Valid @RequestBody CalendarEventDTO dto,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} creando evento: {}", currentUser.getUsername(), dto.getTitulo());

        CalendarEventResponseDTO event = calendarService.createEvent(dto, currentUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }


    @GetMapping("/eventos")
    public ResponseEntity<List<CalendarEventResponseDTO>> obtenerTodosLosEventos() throws ExecutionException, InterruptedException {
        log.debug("Admin obteniendo todos los eventos");

        List<CalendarEventResponseDTO> events = calendarService.getAllEvents();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/eventos/pendientes")
    public ResponseEntity<List<CalendarEventResponseDTO>> obtenerEventosPendientes() throws ExecutionException, InterruptedException {

        log.debug("Admin obteniendo eventos pendientes");

        List<CalendarEventResponseDTO> events = calendarService.getPendingEvents();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/eventos/rango")
    public ResponseEntity<List<CalendarEventResponseDTO>> obtenerEventosPorRangoDeFecha(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
    ) throws ExecutionException, InterruptedException {

        log.debug("Admin obteniendo eventos entre {} y {}", inicio, fin);

        List<CalendarEventResponseDTO> events = calendarService.getEventsByDateRange(inicio, fin);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/eventos/proximos")
    public ResponseEntity<List<CalendarEventResponseDTO>> obtenerProximosEventos(
        @RequestParam(defaultValue = "7") int days
    ) throws ExecutionException, InterruptedException {

        log.debug("Admin obteniendo eventos de los proximos {} dias", days);

        List<CalendarEventResponseDTO> events = calendarService.getUpcomingEvents(days);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/evento/{id}")
    public ResponseEntity<CalendarEventResponseDTO> obtenerEventoPorId(@PathVariable String id) throws ExecutionException, InterruptedException {

        log.debug("Admin buscando evento: {}", id);

        CalendarEventResponseDTO event = calendarService.getEventById(id);

        return ResponseEntity.ok(event);
    }

    @PutMapping("/evento/{id}")
    public ResponseEntity<CalendarEventResponseDTO> actualizarEvento(
        @PathVariable String id,
        @Valid @RequestBody CalendarEventDTO dto,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        CalendarEventResponseDTO event = calendarService.updateEvent(id, dto);

        return ResponseEntity.ok(event);
    }

    @PutMapping("/events/{id}/complete")
    public ResponseEntity<CalendarEventResponseDTO> marcarComoCompleto(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} marcando evento {} como completado", currentUser.getId(), id);

        CalendarEventResponseDTO event = calendarService.markAsCompleted(id);

        return ResponseEntity.ok(event);
    }

    @PutMapping("/events/{id}/pending")
    public ResponseEntity<CalendarEventResponseDTO> marcarComoPendiente(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} marcando evento {} como pendiente", currentUser.getUsername(), id);

        CalendarEventResponseDTO event = calendarService.markAsPending(id);

        return ResponseEntity.ok(event);
    }

    @DeleteMapping("events/{id}")
    public ResponseEntity<Void> eliminarEvento(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} eliminando evento: {}", currentUser.getUsername(), id);

        calendarService.deleteEvent(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticasDelCalendario() throws ExecutionException, InterruptedException {

        log.debug("Admin obteniendo estadisticas del calendario");

        long totalEventos = calendarService.getAllEvents().size();
        long eventosPendientes = calendarService.countPendingEvents();
        long eventosCompletados = totalEventos - eventosPendientes;

        Map<String, Long> estadistica = Map.of(
            "totalEventos", totalEventos,
            "eventosPendientes", eventosPendientes,
            "eventosCompletados", eventosCompletados
        );

        return ResponseEntity.ok(estadistica);
    }
}
