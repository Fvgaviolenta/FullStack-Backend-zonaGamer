package com.zonagamer.zonagamer_backend.service;

import com.zonagamer.zonagamer_backend.dto.UserResponseDTO;
import com.zonagamer.zonagamer_backend.exception.ResourceNotFoundException;
import com.zonagamer.zonagamer_backend.model.User;
import com.zonagamer.zonagamer_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    

    public List<UserResponseDTO> getAllUsers() 
            throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo todos los usuarios");
        
        List<User> users = userRepository.findAll();
        
        return users.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(String userId) 
            throws ExecutionException, InterruptedException {
        
        log.debug("Buscando usuario: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado: " + userId
            ));
        
        return mapToResponseDTO(user);
    }

    public UserResponseDTO updateProfile(
            String userId, 
            String nombre, 
            String apellido, 
            String numeroDeTelefono
    ) throws ExecutionException, InterruptedException {
        
        log.info("Actualizando perfil de usuario: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado: " + userId
            ));
        
        // Actualizar campos
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setNumeroDeTelefono(numeroDeTelefono);
        user.setFechaActualizacion(new Date());
        
        // Guardar cambios
        userRepository.update(userId, user);
        
        log.info("✅ Perfil actualizado para usuario: {}", userId);
        
        return mapToResponseDTO(user);
    }
    

    public void changePassword(String userId, String currentPassword, String newPassword) 
            throws ExecutionException, InterruptedException {
        
        log.info("Cambiando contraseña de usuario: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado: " + userId
            ));
        
        // 1. Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("⚠️ Contraseña actual incorrecta para usuario: {}", userId);
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }
        
        // 2. Validar nueva contraseña
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException(
                "La nueva contraseña debe tener al menos 6 caracteres"
            );
        }
        
        // 3. Encriptar y guardar
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFechaActualizacion(new Date());
        
        userRepository.update(userId, user);
        
        log.info("✅ Contraseña actualizada para usuario: {}", userId);
    }

    public void promoteToAdmin(String userId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Promoviendo usuario a admin: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado: " + userId
            ));
        
        if (user.isAdmin()) {
            throw new IllegalStateException("El usuario ya es administrador");
        }
        
        user.setAdmin(true);
        user.setFechaActualizacion(new Date());
        
        userRepository.update(userId, user);
        
        log.info("✅ Usuario promovido a admin: {}", userId);
    }
    

    public void revokeAdmin(String userId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Quitando privilegios de admin a usuario: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado: " + userId
            ));
        
        if (!user.isAdmin()) {
            throw new IllegalStateException("El usuario no es administrador");
        }
        
        user.setAdmin(false);
        user.setFechaActualizacion(new Date());
        
        userRepository.update(userId, user);
        
        log.info("✅ Privilegios de admin revocados para usuario: {}", userId);
    }
    

    public void deactivateUser(String userId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Desactivando usuario: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado: " + userId
            ));
        
        user.setActive(false);
        user.setFechaActualizacion(new Date());
        
        userRepository.update(userId, user);
        
        log.info("✅ Usuario desactivado: {}", userId);
    }
    
    public void activateUser(String userId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Activando usuario: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado: " + userId
            ));
        
        user.setActive(true);
        user.setFechaActualizacion(new Date());
        
        userRepository.update(userId, user);
        
        log.info("✅ Usuario activado: {}", userId);
    }
    

    public long countUsers() throws ExecutionException, InterruptedException {
        return userRepository.count();
    }
    
  
    public long countActiveUsers() throws ExecutionException, InterruptedException {
        List<User> users = userRepository.findAll();
        return users.stream().filter(User::isActive).count();
    }
    

    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nombre(user.getNombre())
            .apellido(user.getApellido())
            .nombreCompleto(user.obtenerNombreCompleto())
            .numeroDeTelefono(user.getNumeroDeTelefono())
            .admin(user.isAdmin())
            .active(user.isActive())
            .fechaCreacion(user.getFechaCreacion() != null ? 
                user.getFechaCreacion().toString() : null)
            .build();
    }
}