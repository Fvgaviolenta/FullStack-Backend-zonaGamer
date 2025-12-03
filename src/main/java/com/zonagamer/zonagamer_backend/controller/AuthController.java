package com.zonagamer.zonagamer_backend.controller;


import com.zonagamer.zonagamer_backend.dto.UserLoginDTO;
import com.zonagamer.zonagamer_backend.dto.UserRegistrationDTO;
import com.zonagamer.zonagamer_backend.dto.AuthResponseDTO;
import com.zonagamer.zonagamer_backend.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.concurrent.ExecutionException;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(
        @Valid @RequestBody UserRegistrationDTO dto
    ) throws ExecutionException, InterruptedException {
        log.info("Peticion de registro: {}", dto.getEmail());

        AuthResponseDTO response = authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
        @Valid @RequestBody UserLoginDTO dto
    ) throws ExecutionException, InterruptedException {

        log.info("Intento de login: {}", dto.getEmail());

        AuthResponseDTO response = authService.login(dto);

        return ResponseEntity.ok(response);
    }

    //Endpoint de prueba para verificar que el server este funcionado
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running!");
    }


}
