package com.zonagamer.zonagamer_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry
            .addMapping("/api/**")  //APLICA A TODAS LAS RUTAS QUE EMPIECEN CON /API
            .allowedOriginPatterns(allowedOrigins.split(","))  //ORIGENES PERMITIDOS (usando patterns para compatibilidad con credentials)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // METODOS HTTP
            .allowedHeaders("*")    // TODOS LOS HEADERS
            .allowCredentials(true) // PERMITE COOKIES Y AUTHORIZATION HEADER
            .maxAge(3600);  // CACHE DE PREFLIGHT POR 1 HORA
    }
}
