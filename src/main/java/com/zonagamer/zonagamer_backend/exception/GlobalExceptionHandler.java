package com.zonagamer.zonagamer_backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones
 * 
 * Intercepta todas las excepciones de la aplicación y
 * las convierte en respuestas HTTP apropiadas.
 * 
 * Ventajas:
 * - Respuestas consistentes en toda la API
 * - No repites try-catch en cada controlador
 * - Logging centralizado de errores
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Maneja errores de validación (@Valid en DTOs)
     * 
     * Retorna: 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now().toString())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Errores de validación")
            .message("Los datos enviados contienen errores")
            .details(errors)
            .build();
        
        log.warn("Errores de validación: {}", errors);
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Maneja recursos no encontrados
     * 
     * Retorna: 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex
    ) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now().toString())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Recurso no encontrado")
            .message(ex.getMessage())
            .build();
        
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * Maneja stock insuficiente
     * 
     * Retorna: 409 Conflict
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException ex
    ) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now().toString())
            .status(HttpStatus.CONFLICT.value())
            .error("Stock insuficiente")
            .message(ex.getMessage())
            .build();
        
        log.warn("Stock insuficiente: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    /**
     * Maneja errores de autenticación
     * 
     * Retorna: 401 Unauthorized
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex
    ) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now().toString())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("No autorizado")
            .message(ex.getMessage())
            .build();
        
        log.warn("Acceso no autorizado: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    /**
     * Maneja argumentos ilegales (validaciones de negocio)
     * 
     * Retorna: 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now().toString())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Argumento inválido")
            .message(ex.getMessage())
            .build();
        
        log.warn("Argumento inválido: {}", ex.getMessage());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Maneja estados ilegales (operaciones no permitidas)
     * 
     * Retorna: 409 Conflict
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex
    ) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now().toString())
            .status(HttpStatus.CONFLICT.value())
            .error("Operación no permitida")
            .message(ex.getMessage())
            .build();
        
        log.warn("Estado ilegal: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    /**
     * Maneja cualquier otra excepción no capturada
     * 
     * Retorna: 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now().toString())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Error interno del servidor")
            .message("Ocurrió un error inesperado. Por favor contacta al soporte.")
            .build();
        
        log.error("❌ Error no controlado: ", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}