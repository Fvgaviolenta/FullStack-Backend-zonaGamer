package com.zonagamer.zonagamer_backend.config;

import com.zonagamer.zonagamer_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Bean de PasswordEncoder para encriptar contraseñas
     * 
     * BCrypt es el estándar de la industria para hash de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad
     * 
     * Define:
     * - Qué endpoints son públicos
     * - Qué endpoints requieren autenticación
     * - Qué endpoints requieren rol específico
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (Cross-Site Request Forgery)
                // No es necesario con JWT (el token va en el header, no en cookies)
                .csrf(csrf -> csrf.disable())

                // Configurar CORS (permite peticiones desde React)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    // Usar allowedOriginPatterns en lugar de allowedOrigins para permitir cualquier origen
                    corsConfig.setAllowedOriginPatterns(java.util.List.of("*"));
                    corsConfig.setAllowedMethods(java.util.List.of("*"));
                    corsConfig.setAllowedHeaders(java.util.List.of("*"));
                    corsConfig.setAllowCredentials(true); // Permitir credenciales (Authorization header)
                    return corsConfig;
                }))

                // Configurar manejo de sesiones
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin sesiones HTTP

                // Configurar reglas de autorización
                .authorizeHttpRequests(auth -> auth

                        // ===== ENDPOINTS PÚBLICOS (sin autenticación) =====

                        // Autenticación
                        .requestMatchers("/api/auth/**").permitAll()

                        // Productos (solo lectura)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // Categorías (solo lectura)
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        // Health check
                        .requestMatchers("/api/health").permitAll()

                        // ===== ENDPOINTS PROTEGIDOS (requieren autenticación) =====

                        // Carrito (requiere estar logueado)
                        .requestMatchers("/api/cart/**").authenticated()

                        // Órdenes (usuario puede ver sus propias órdenes)
                        .requestMatchers(HttpMethod.GET, "/api/orders/my-orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/orders/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/orders/checkout").authenticated()

                        // Perfil de usuario
                        .requestMatchers("/api/users/me/**").authenticated()

                        // ===== ENDPOINTS DE ADMIN (requieren rol ADMIN) =====

                        // Gestión de productos (crear, actualizar, eliminar)
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ROLE_ADMIN")

                        // Gestión de categorías
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAuthority("ROLE_ADMIN")

                        // Gestión de usuarios
                        .requestMatchers("/api/users", "/api/users/**").hasAuthority("ROLE_ADMIN")

                        // Gestión de órdenes (admin puede ver todas)
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/status/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAuthority("ROLE_ADMIN")

                        // Calendario (solo admin)
                        .requestMatchers("/api/calendar/**").hasAuthority("ROLE_ADMIN")

                        // Todas las demás peticiones requieren autenticación
                        .anyRequest().authenticated()
                )

                // Agregar filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
