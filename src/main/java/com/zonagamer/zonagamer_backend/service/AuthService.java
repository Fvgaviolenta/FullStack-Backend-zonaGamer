package com.zonagamer.zonagamer_backend.service;

import com.zonagamer.zonagamer_backend.dto.*;
import com.zonagamer.zonagamer_backend.model.User;
import com.zonagamer.zonagamer_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthResponseDTO register(UserRegistrationDTO dto) 
            throws ExecutionException, InterruptedException {
        
        // Verificar si el usuario ya existe
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        User user = User.builder()
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .nombre(dto.getNombre())
            .apellido(dto.getApellido())
            .numeroDeTelefono(dto.getNumeroDeTelefono())
            .admin(false)
            .active(true)
            .build();
            
        String userId = userRepository.save(user);
        user.setId(userId);
        
        String token = jwtService.generateToken(user);
        
        return buildAuthResponse(user, token);
    }
    
    public AuthResponseDTO login(UserLoginDTO dto) 
            throws ExecutionException, InterruptedException {
        
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
            
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        String token = jwtService.generateToken(user);
        
        return buildAuthResponse(user, token);
    }
    
    private AuthResponseDTO buildAuthResponse(User user, String token) {
        return AuthResponseDTO.builder()
            .token(token)
            .userId(user.getId())
            .email(user.getEmail())
            .nombreCompleto(user.obtenerNombreCompleto())
            .admin(user.isAdmin())
            .build();
    }
}