package com.zonagamer.zonagamer_backend.controller;

import com.zonagamer.zonagamer_backend.dto.UserResponseDTO;
import com.zonagamer.zonagamer_backend.security.UserPrincipal;
import com.zonagamer.zonagamer_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioActual(
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.debug("Usuario {} obteniedo su perfil", currentUser.getUsername());

        UserResponseDTO user = userService.getUserById(currentUser.getId());

        return ResponseEntity.ok(user);
    }


    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> actualizarPerfil(
        @RequestBody Map<String, String> actualizaciones,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {


        log.info("Usuario {} actualizando su perfil", currentUser.getUsername());

        String nombre = actualizaciones.getOrDefault("nombre", "");
        String apellido = actualizaciones.getOrDefault("apellido", "");
        String numeroDeTelefono = actualizaciones.getOrDefault("numeroDeTelefono", "");

        if (nombre.isEmpty() || apellido.isEmpty()) {
            throw new IllegalArgumentException("Nombre y apellido son obligatorios");
        }

        UserResponseDTO user = userService.updateProfile(
            currentUser.getId(),
            nombre,
            apellido,
            numeroDeTelefono
        );

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> cambiarContraseña(
        @RequestBody Map<String, String> contraseña,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {


        log.info("Usuario {} cambiando contraseña", currentUser.getUsername());

        String contraseñaActual = contraseña.get("contraseñaActual");
        String nuevaContraseña = contraseña.get("nuevaContraseña");

        if (contraseñaActual == null || nuevaContraseña == null) {
            throw new IllegalArgumentException(
                "Debe proporcionar contraseña actual y nueva contraseña"
            );
        }

        userService.changePassword(currentUser.getId(), contraseñaActual, nuevaContraseña);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> obtenerTodosLosUsuarios() throws ExecutionException, InterruptedException {
        log.debug("Admin obteniendo todos los usuarios");

        List<UserResponseDTO> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioPorId(
        @PathVariable String id
    ) throws ExecutionException, InterruptedException {

        log.debug("Admin buscando usuario: {}", id);

        UserResponseDTO user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/promote")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> promoteToAdmin(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} promoviendo usuario {} a admin", currentUser.getUsername(), id);

        userService.promoteToAdmin(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/revoke")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> revokeAdmin(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} quitando privilegios de admin a usuario {}", currentUser.getUsername());

        userService.revokeAdmin(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/desactivarUser")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> desactivarUser(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} desactivando usuario: {}", currentUser.getUsername(), id);

        userService.deactivateUser(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activarUser")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> activarUsuario(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} activando usuario {}", currentUser.getUsername(), id);

        userService.activateUser(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Long>> obtenerStatsUsuario() throws ExecutionException, InterruptedException {

        log.debug("Admin obteniendo estadisticas de usuarios");

        long totalUsuarios = userService.countUsers();
        long usuariosActivos = userService.countActiveUsers();
        long usuariosInactivos = totalUsuarios - usuariosActivos;

        Map<String, Long> estadisticas = Map.of(
            "totalUsuarios", totalUsuarios,
            "usuariosActivos", usuariosActivos,
            "usuariosInactivos", usuariosInactivos
        );

        return ResponseEntity.ok(estadisticas);
    }
}
