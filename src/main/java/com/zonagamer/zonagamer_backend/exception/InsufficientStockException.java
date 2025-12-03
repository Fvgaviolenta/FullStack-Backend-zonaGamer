package com.zonagamer.zonagamer_backend.exception;

public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String message) {
        super(message);
    }
}