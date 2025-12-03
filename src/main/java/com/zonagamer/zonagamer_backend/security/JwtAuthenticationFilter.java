package com.zonagamer.zonagamer_backend.security;

import com.zonagamer.zonagamer_backend.model.User;
import com.zonagamer.zonagamer_backend.repository.UserRepository;
import com.zonagamer.zonagamer_backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtService.validateToken(token)) {
                log.warn("Token invalido o expirado");
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtService.getUserIdFromToken(token);
            
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("User ID extraído del token es nulo o vacío");
                filterChain.doFilter(request, response);
                return;
            }

            User user = userRepository.findById(userId)
                .orElse(null);
            
            if (user == null) {
                log.warn("Usuario no encontrado: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            if (!user.isActive()) {
                log.warn("Usuario inacvtivo: {}", user.getEmail());
                filterChain.doFilter(request, response);
                return;
            }

            UserPrincipal userPrincipal = UserPrincipal.create(user);

            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            
            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Usuario autenticado: {} (Admin: {})", user.getEmail(), user.isAdmin());

        } catch (ExecutionException | InterruptedException e) {
            log.error("Errro al autenticar usuario: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en filtro JWT: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
