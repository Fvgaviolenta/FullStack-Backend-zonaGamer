package com.zonagamer.zonagamer_backend.exception;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * Estructura estándar para respuestas de error
 * 
 * Ejemplo de JSON:
 * {
 *   "timestamp": "2025-11-20T20:59:39",
 *   "status": 400,
 *   "error": "Errores de validación",
 *   "message": "Los datos enviados contienen errores",
 *   "details": {
 *     "email": "Email inválido",
 *     "password": "La contraseña debe tener al menos 6 caracteres"
 *   }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private String timestamp;  // Cuándo ocurrió el error
    
    private int status;  // Código HTTP (400, 404, 500, etc.)
    
    private String error;  // Tipo de error
    
    private String message;  // Mensaje para el usuario
    
    private Map<String, String> details;  // Detalles adicionales (opcional)
}