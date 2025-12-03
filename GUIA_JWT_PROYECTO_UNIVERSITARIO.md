# 🔐 Guía Completa de JWT para Proyecto Universitario ZonaGamer

## 📚 Tabla de Contenidos
1. [¿Qué es JWT?](#qué-es-jwt)
2. [Cómo Funciona JWT en Tu Proyecto](#cómo-funciona-jwt-en-tu-proyecto)
3. [Arquitectura del Sistema de Autenticación](#arquitectura-del-sistema-de-autenticación)
4. [Configuración Actual](#configuración-actual)
5. [Roles: Admin vs Usuario Normal](#roles-admin-vs-usuario-normal)
6. [Flujo Completo de Autenticación](#flujo-completo-de-autenticación)
7. [Demostración Práctica con Postman](#demostración-práctica-con-postman)
8. [Seguridad y Buenas Prácticas](#seguridad-y-buenas-prácticas)
9. [Troubleshooting](#troubleshooting)

---

## 🎯 ¿Qué es JWT?

**JWT** significa **JSON Web Token**. Es un estándar de seguridad que permite transmitir información entre dos partes (cliente y servidor) de forma segura.

### Anatomía de un JWT

Un JWT tiene 3 partes separadas por puntos (`.`):

```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyLTAwMSIsImVtYWlsIjoi...a3NDOiJhZG1pbkB6b25h
```

**Estructura:**
```
HEADER.PAYLOAD.SIGNATURE
```

#### 1️⃣ **HEADER (Encabezado)**
```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```
- `alg`: Algoritmo de encriptación (HS512 = HMAC SHA-512)
- `typ`: Tipo de token (siempre "JWT")

#### 2️⃣ **PAYLOAD (Carga útil)**
```json
{
  "sub": "user-firebase-id-123",
  "email": "cliente@zonagamer.com",
  "isAdmin": false,
  "nombreCompleto": "Juan Pérez",
  "iat": 1733180000,
  "exp": 1733266400
}
```
- `sub` (subject): ID del usuario en Firebase
- `email`: Email del usuario
- `isAdmin`: Indica si es administrador
- `nombreCompleto`: Nombre completo para mostrar
- `iat` (issued at): Fecha de creación del token (timestamp)
- `exp` (expiration): Fecha de expiración (timestamp)

#### 3️⃣ **SIGNATURE (Firma)**
```
HMACSHA512(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  tu-secreto-super-seguro
)
```
Esta firma garantiza que nadie ha modificado el token.

---

## 🏗️ Cómo Funciona JWT en Tu Proyecto

### Flujo Visual

```
┌──────────────┐                          ┌──────────────┐
│   CLIENTE    │                          │   SERVIDOR   │
│  (React/     │                          │  (Spring     │
│   Postman)   │                          │   Boot)      │
└──────────────┘                          └──────────────┘
       │                                          │
       │  1. POST /api/auth/register             │
       │     { email, password, nombre }         │
       │─────────────────────────────────────────>│
       │                                          │
       │                     2. Encripta password │
       │                     3. Guarda en Firebase│
       │                     4. Genera JWT        │
       │                                          │
       │  5. { token: "eyJhbG...", userId: "..." }│
       │<─────────────────────────────────────────│
       │                                          │
       │  6. Guarda token en localStorage        │
       │                                          │
       │  7. GET /api/products                   │
       │     Authorization: Bearer eyJhbG...     │
       │─────────────────────────────────────────>│
       │                                          │
       │                     8. Valida JWT        │
       │                     9. Extrae userId     │
       │                    10. Busca usuario     │
       │                    11. Verifica rol      │
       │                                          │
       │  12. [ productos... ]                   │
       │<─────────────────────────────────────────│
       │                                          │
```

---

## 🧩 Arquitectura del Sistema de Autenticación

### Componentes Clave

```
┌─────────────────────────────────────────────────────────────┐
│                     CAPA DE PRESENTACIÓN                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ AuthController│  │ ProductCtrler│  │ OrderController│     │
│  │ /api/auth/**  │  │ /api/products│  │ /api/orders/** │     │
│  └───────┬───────┘  └──────┬───────┘  └───────┬────────┘    │
└──────────┼──────────────────┼──────────────────┼─────────────┘
           │                  │                  │
           │                  │                  │
┌──────────┼──────────────────┼──────────────────┼─────────────┐
│          │         CAPA DE NEGOCIO             │             │
│  ┌───────▼────────┐  ┌──────▼───────┐  ┌──────▼────────┐   │
│  │  AuthService   │  │ProductService│  │ OrderService  │   │
│  │ - register()   │  │ - create()   │  │ - checkout()  │   │
│  │ - login()      │  │ - getAll()   │  │ - getMyOrders│   │
│  └───────┬────────┘  └──────────────┘  └───────────────┘   │
└──────────┼────────────────────────────────────────────────────┘
           │
           │
┌──────────┼────────────────────────────────────────────────────┐
│          │           CAPA DE SEGURIDAD                        │
│  ┌───────▼────────┐  ┌─────────────────┐  ┌──────────────┐  │
│  │  JwtService    │  │JwtAuthFilter    │  │UserPrincipal │  │
│  │ - generateToken│  │ - doFilterInternal│ │ (UserDetails)│  │
│  │ - validateToken│  │   - Valida token │  │ - Roles      │  │
│  │ - getUserId    │  │   - Autentica    │  │              │  │
│  └────────────────┘  └─────────────────┘  └──────────────┘  │
└────────────────────────────────────────────────────────────────┘
           │
           │
┌──────────┼────────────────────────────────────────────────────┐
│          │         CAPA DE PERSISTENCIA                       │
│  ┌───────▼────────┐  ┌──────────────┐                        │
│  │ UserRepository │  │   Firebase   │                        │
│  │ - findByEmail()│  │   Firestore  │                        │
│  │ - save()       │  │              │                        │
│  └────────────────┘  └──────────────┘                        │
└────────────────────────────────────────────────────────────────┘
```

---

## ⚙️ Configuración Actual

### 1. **application.yml** - Configuración de JWT

```yaml
# Configuración de JWT
jwt:
  secret: ${JWT_SECRET:tu-secreto-super-seguro-cambialo-en-produccion}
  expiration: 86400000 # 24 horas en milisegundos (1000ms * 60seg * 60min * 24h)
```

**Explicación:**
- `jwt.secret`: Clave secreta para firmar los tokens (debe ser única y segura)
- `jwt.expiration`: Tiempo de vida del token = **24 horas**

**⚠️ IMPORTANTE:** Para producción, **SIEMPRE** usa una variable de entorno:
```bash
export JWT_SECRET=MiClaveSecretaSuperSeguraDeAlMenos256Bits2024!
```

---

### 2. **JwtService.java** - Servicio de Generación y Validación

**Ubicación:** `src/main/java/.../service/JwtService.java`

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;  // ← Lee de application.yml

    @Value("${jwt.expiration}")
    private Long expiration;  // ← 86400000 (24 horas)

    /**
     * 🔑 GENERA un token JWT cuando un usuario se registra o hace login
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .subject(user.getId())  // ← ID del usuario (principal identificador)
            .claim("email", user.getEmail())  // ← Email
            .claim("isAdmin", user.isAdmin())  // ← ¡ROL ADMIN!
            .claim("nombreCompleto", user.obtenerNombreCompleto())
            .issuedAt(now)  // ← Fecha de creación
            .expiration(expiryDate)  // ← Fecha de expiración
            .signWith(getSigningKey())  // ← Firma con la clave secreta
            .compact();  // ← Convierte a String
    }

    /**
     * 🔍 EXTRAE el ID del usuario desde el token
     */
    public String getUserFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();  // ← Retorna el ID
    }

    /**
     * 👤 VERIFICA si el usuario es ADMIN
     */
    public boolean isAdmin(String token) {
        Claims claims = parseToken(token);
        return claims.get("isAdmin", Boolean.class);
    }

    /**
     * ✅ VALIDA si el token es válido (no expirado, firma correcta)
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);  // ← Si no lanza excepción, es válido
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado");
            return false;
        } catch (JwtException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 🔓 PARSEA (decodifica) el token y extrae los claims
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())  // ← Verifica firma
            .build()
            .parseSignedClaims(token)
            .getPayload();  // ← Retorna los datos del payload
    }

    /**
     * 🔐 Convierte el secret a SecretKey para firmar
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
```

---

### 3. **AuthService.java** - Registro y Login

**Ubicación:** `src/main/java/.../service/AuthService.java`

```java
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;  // ← Inyecta JwtService

    /**
     * 📝 REGISTRO: Crea un nuevo usuario y devuelve un token
     */
    public AuthResponseDTO register(UserRegistrationDTO dto) {
        // 1. Validar que el email no exista
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // 2. Crear usuario (por defecto NO ES ADMIN)
        User user = User.builder()
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))  // ← Encripta password
            .nombre(dto.getNombre())
            .apellido(dto.getApellido())
            .numeroDeTelefono(dto.getNumeroDeTelefono())
            .isAdmin(false)  // ← ¡USUARIOS NORMALES POR DEFECTO!
            .active(true)
            .fechaCreacion(LocalDateTime.now())
            .build();

        // 3. Guardar en Firebase Firestore
        String userId = userRepository.save(user);
        user.setId(userId);

        // 4. GENERAR TOKEN JWT
        String token = jwtService.generateToken(user);

        // 5. Retornar respuesta con el token
        return AuthResponseDTO.builder()
            .token(token)  // ← ¡El token que el cliente usará!
            .type("Bearer")
            .userId(user.getId())
            .email(user.getEmail())
            .nombreCompleto(user.obtenerNombreCompleto())
            .isAdmin(user.isAdmin())  // ← false (usuario normal)
            .build();
    }

    /**
     * 🔑 LOGIN: Valida credenciales y devuelve un token
     */
    public AuthResponseDTO login(UserLoginDTO dto) {
        // 1. Buscar usuario por email
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        // 2. Verificar password
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // 3. Verificar que esté activo
        if (!user.isActive()) {
            throw new UnauthorizedException("Usuario inactivo");
        }

        // 4. GENERAR TOKEN JWT
        String token = jwtService.generateToken(user);

        // 5. Retornar respuesta con el token
        return AuthResponseDTO.builder()
            .token(token)  // ← El nuevo token
            .type("Bearer")
            .userId(user.getId())
            .email(user.getEmail())
            .nombreCompleto(user.obtenerNombreCompleto())
            .isAdmin(user.isAdmin())  // ← puede ser true o false
            .build();
    }
}
```

---

### 4. **JwtAuthenticationFilter.java** - Filtro de Autenticación

**Ubicación:** `src/main/java/.../security/JwtAuthenticationFilter.java`

Este filtro se ejecuta **ANTES** de cada petición HTTP.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * 🔍 Este método se ejecuta en CADA petición HTTP
     */
    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            // 1️⃣ EXTRAER el token del header "Authorization"
            String token = extractTokenFromRequest(request);

            if (token == null) {
                // No hay token → Continuar sin autenticar
                filterChain.doFilter(request, response);
                return;
            }

            // 2️⃣ VALIDAR el token (firma, expiración)
            if (!jwtService.validateToken(token)) {
                log.warn("Token inválido o expirado");
                filterChain.doFilter(request, response);
                return;
            }

            // 3️⃣ EXTRAER el userId del token
            String userId = jwtService.getUserFromToken(token);

            // 4️⃣ BUSCAR el usuario en Firebase
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                log.warn("Usuario no encontrado: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // 5️⃣ VERIFICAR que el usuario esté activo
            if (!user.isActive()) {
                log.warn("Usuario inactivo: {}", user.getEmail());
                filterChain.doFilter(request, response);
                return;
            }

            // 6️⃣ CREAR UserPrincipal (contiene los roles)
            UserPrincipal userPrincipal = UserPrincipal.create(user);

            // 7️⃣ CREAR autenticación de Spring Security
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    userPrincipal,  // ← El usuario autenticado
                    null,  // ← No necesitamos password aquí
                    userPrincipal.getAuthorities()  // ← ROLES (ADMIN o USER)
                );
            
            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 8️⃣ GUARDAR autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Usuario autenticado: {} (Admin: {})", 
                user.getEmail(), user.isAdmin());

        } catch (Exception e) {
            log.error("Error al autenticar usuario: {}", e.getMessage());
        }

        // 9️⃣ CONTINUAR con el siguiente filtro
        filterChain.doFilter(request, response);
    }

    /**
     * 🔎 Extrae el token del header "Authorization: Bearer <token>"
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // ← Quita "Bearer "
        }
        return null;
    }
}
```

---

### 5. **UserPrincipal.java** - Representación del Usuario Autenticado

**Ubicación:** `src/main/java/.../security/UserPrincipal.java`

```java
public class UserPrincipal implements UserDetails {
    
    private String id;
    private String email;
    private String password;
    private boolean isAdmin;
    private boolean active;

    /**
     * 🏭 Crea un UserPrincipal desde un User
     */
    public static UserPrincipal create(User user) {
        return new UserPrincipal(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.isAdmin(),
            user.isActive()
        );
    }

    /**
     * 🎭 ROLES del usuario (ADMIN o USER)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin) {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN")  // ← ROL ADMIN
            );
        }
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")  // ← ROL USER
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    // ... otros métodos de UserDetails
}
```

---

### 6. **SecurityConfig.java** - Configuración de Seguridad

**Ubicación:** `src/main/java/.../config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 🔐 Bean de BCrypt para encriptar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 🛡️ Configuración de la cadena de filtros de seguridad
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // ← Desactiva CSRF (JWT no lo necesita)
            
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of("*"));
                corsConfig.setAllowedMethods(List.of("*"));
                corsConfig.setAllowedHeaders(List.of("*"));
                return corsConfig;
            }))
            
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // ← Sin sesiones
            
            .authorizeHttpRequests(auth -> auth
                
                // ========== ENDPOINTS PÚBLICOS ==========
                .requestMatchers("/api/auth/**").permitAll()  // ← Register y Login
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                
                // ========== ENDPOINTS AUTENTICADOS ==========
                .requestMatchers("/api/cart/**").authenticated()  // ← Requiere login
                .requestMatchers("/api/orders/my-orders").authenticated()
                .requestMatchers("/api/orders/checkout").authenticated()
                
                // ========== ENDPOINTS SOLO ADMIN ==========
                .requestMatchers(HttpMethod.POST, "/api/products/**")
                    .hasAuthority("ROLE_ADMIN")  // ← Solo admins pueden crear productos
                .requestMatchers(HttpMethod.PUT, "/api/products/**")
                    .hasAuthority("ROLE_ADMIN")  // ← Solo admins pueden editar
                .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                    .hasAuthority("ROLE_ADMIN")  // ← Solo admins pueden eliminar
                
                .requestMatchers("/api/categories/**")
                    .hasAuthority("ROLE_ADMIN")  // ← Solo admins gestionan categorías
                
                .requestMatchers("/api/orders/all")
                    .hasAuthority("ROLE_ADMIN")  // ← Solo admins ven todas las órdenes
                
                .requestMatchers("/api/calendar/**")
                    .hasAuthority("ROLE_ADMIN")  // ← Solo admins usan el calendario
                
                .anyRequest().authenticated()  // ← Todo lo demás requiere autenticación
            )
            
            // ➕ Agregar el filtro JWT ANTES del filtro de autenticación estándar
            .addFilterBefore(
                jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
```

---

## 👥 Roles: Admin vs Usuario Normal

### Diferencias Clave

| Característica | ADMIN | USER |
|----------------|-------|------|
| **Campo en DB** | `isAdmin: true` | `isAdmin: false` |
| **Rol Spring Security** | `ROLE_ADMIN` | `ROLE_USER` |
| **Registro** | Manual en Firebase | Automático vía `/api/auth/register` |
| **Ver productos** | ✅ Sí | ✅ Sí |
| **Comprar** | ✅ Sí | ✅ Sí |
| **Crear productos** | ✅ Sí | ❌ No (403 Forbidden) |
| **Editar productos** | ✅ Sí | ❌ No |
| **Eliminar productos** | ✅ Sí | ❌ No |
| **Gestionar categorías** | ✅ Sí | ❌ No |
| **Ver todas las órdenes** | ✅ Sí | ❌ No (solo las propias) |
| **Calendario de eventos** | ✅ Sí | ❌ No |

---

## 🔄 Flujo Completo de Autenticación

### Escenario 1: Usuario Normal se Registra

```
┌─────────┐                                ┌─────────┐
│ CLIENTE │                                │ SERVIDOR│
└─────────┘                                └─────────┘
     │                                            │
     │  1. POST /api/auth/register                │
     │     {                                      │
     │       "email": "juan@gmail.com",           │
     │       "password": "Abc123!",               │
     │       "nombre": "Juan",                    │
     │       "apellido": "Pérez",                 │
     │       "numeroDeTelefono": "+56912345678"   │
     │     }                                      │
     │───────────────────────────────────────────>│
     │                                            │
     │              2. AuthController.register()  │
     │                 ↓                          │
     │              3. AuthService.register()     │
     │                 ↓                          │
     │              4. Verifica email no existe   │
     │                 ↓                          │
     │              5. Encripta password (BCrypt) │
     │                 password: "Abc123!"        │
     │                 ↓                          │
     │                 hash: "$2a$10$X7Y..."     │
     │                 ↓                          │
     │              6. Crea User:                 │
     │                 {                          │
     │                   id: null,                │
     │                   email: "juan@gmail.com", │
     │                   password: "$2a$10$...",  │
     │                   nombre: "Juan",          │
     │                   apellido: "Pérez",       │
     │                   isAdmin: false,  ← NORMAL│
     │                   active: true             │
     │                 }                          │
     │                 ↓                          │
     │              7. UserRepository.save()      │
     │                 ↓                          │
     │              8. Firebase Firestore guarda: │
     │                 userId: "abc123xyz"        │
     │                 ↓                          │
     │              9. JwtService.generateToken() │
     │                 ↓                          │
     │             10. Crea JWT:                  │
     │                 {                          │
     │                   sub: "abc123xyz",        │
     │                   email: "juan@gmail.com", │
     │                   isAdmin: false,          │
     │                   nombreCompleto: "Juan P",│
     │                   iat: 1733180000,         │
     │                   exp: 1733266400          │
     │                 }                          │
     │                 ↓                          │
     │             11. Firma con secret           │
     │                 ↓                          │
     │  12. Response:                             │
     │     {                                      │
     │       "token": "eyJhbGciOiJIUzUxMiJ9...",  │
     │       "type": "Bearer",                    │
     │       "userId": "abc123xyz",               │
     │       "email": "juan@gmail.com",           │
     │       "nombreCompleto": "Juan Pérez",      │
     │       "isAdmin": false                     │
     │     }                                      │
     │<───────────────────────────────────────────│
     │                                            │
     │  13. Guarda token en localStorage          │
     │      localStorage.setItem('token', ...)    │
     │                                            │
```

---

### Escenario 2: Usuario Normal Intenta Crear un Producto (Denegado)

```
┌─────────┐                                ┌─────────┐
│ CLIENTE │                                │ SERVIDOR│
└─────────┘                                └─────────┘
     │                                            │
     │  1. POST /api/products                     │
     │     Authorization: Bearer eyJhbG...        │
     │     {                                      │
     │       "nombreProducto": "RTX 5090",        │
     │       "precio": 1999.99,                   │
     │       ...                                  │
     │     }                                      │
     │───────────────────────────────────────────>│
     │                                            │
     │         2. JwtAuthenticationFilter         │
     │            ↓                               │
     │         3. Extrae token del header         │
     │            token = "eyJhbG..."             │
     │            ↓                               │
     │         4. JwtService.validateToken()      │
     │            → ✅ Válido                     │
     │            ↓                               │
     │         5. JwtService.getUserFromToken()   │
     │            → userId = "abc123xyz"          │
     │            ↓                               │
     │         6. UserRepository.findById()       │
     │            → User { isAdmin: false }       │
     │            ↓                               │
     │         7. UserPrincipal.create()          │
     │            → authorities: ["ROLE_USER"]    │
     │            ↓                               │
     │         8. SecurityConfig verifica:        │
     │            Endpoint: POST /api/products    │
     │            Requiere: ROLE_ADMIN            │
     │            Usuario tiene: ROLE_USER        │
     │            ↓                               │
     │            ❌ ACCESO DENEGADO              │
     │                                            │
     │  9. HTTP 403 Forbidden                     │
     │     {                                      │
     │       "error": "Forbidden",                │
     │       "message": "Access Denied"           │
     │     }                                      │
     │<───────────────────────────────────────────│
     │                                            │
```

---

### Escenario 3: Admin Crea un Producto (Permitido)

```
┌─────────┐                                ┌─────────┐
│  ADMIN  │                                │ SERVIDOR│
└─────────┘                                └─────────┘
     │                                            │
     │  1. POST /api/products                     │
     │     Authorization: Bearer eyJzdW...        │
     │     {                                      │
     │       "nombreProducto": "RTX 5090",        │
     │       "precio": 1999.99,                   │
     │       ...                                  │
     │     }                                      │
     │───────────────────────────────────────────>│
     │                                            │
     │         2. JwtAuthenticationFilter         │
     │            ↓                               │
     │         3. Extrae token del header         │
     │            token = "eyJzdW..."             │
     │            ↓                               │
     │         4. JwtService.validateToken()      │
     │            → ✅ Válido                     │
     │            ↓                               │
     │         5. JwtService.getUserFromToken()   │
     │            → userId = "admin-001"          │
     │            ↓                               │
     │         6. UserRepository.findById()       │
     │            → User { isAdmin: true }  ← ✅  │
     │            ↓                               │
     │         7. UserPrincipal.create()          │
     │            → authorities: ["ROLE_ADMIN"]   │
     │            ↓                               │
     │         8. SecurityConfig verifica:        │
     │            Endpoint: POST /api/products    │
     │            Requiere: ROLE_ADMIN            │
     │            Usuario tiene: ROLE_ADMIN       │
     │            ↓                               │
     │            ✅ ACCESO PERMITIDO             │
     │            ↓                               │
     │         9. ProductController.crearProducto()│
     │            ↓                               │
     │        10. ProductService.createProduct()  │
     │            ↓                               │
     │        11. Guarda en Firebase              │
     │                                            │
     │  12. HTTP 201 Created                      │
     │     {                                      │
     │       "id": "prod-123",                    │
     │       "nombreProducto": "RTX 5090",        │
     │       ...                                  │
     │     }                                      │
     │<───────────────────────────────────────────│
     │                                            │
```

---

## 🧪 Demostración Práctica con Postman

### Paso 1: Crear un Usuario Normal

**Request:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "juan.perez@gmail.com",
  "password": "Abc123!",
  "nombre": "Juan",
  "apellido": "Pérez",
  "numeroDeTelefono": "+56912345678"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyLWFiYzEyMyIsImVtYWlsIjoianVhbi5wZXJlekBnbWFpbC5jb20iLCJpc0FkbWluIjpmYWxzZSwibm9tYnJlQ29tcGxldG8iOiJKdWFuIFDDqXJleiIsImlhdCI6MTczMzE4MDAwMCwiZXhwIjoxNzMzMjY2NDAwfQ.signature",
  "type": "Bearer",
  "userId": "user-abc123",
  "email": "juan.perez@gmail.com",
  "nombreCompleto": "Juan Pérez",
  "isAdmin": false
}
```

**Verificación del Token:**
Copia el token y ve a [jwt.io](https://jwt.io) para decodificarlo:

```json
{
  "sub": "user-abc123",
  "email": "juan.perez@gmail.com",
  "isAdmin": false,  ← Usuario normal
  "nombreCompleto": "Juan Pérez",
  "iat": 1733180000,
  "exp": 1733266400
}
```

---

### Paso 2: Crear un Usuario Admin (Manual en Firebase)

Como los usuarios normales se registran con `isAdmin: false`, **debes crear el admin manualmente** en Firebase Console o usando un script.

**Opción 1: Firebase Console**
1. Ve a Firebase Console → Firestore Database
2. Colección `users`
3. Agrega documento con ID manual:

```json
{
  "id": "admin-001",
  "email": "admin@zonagamer.com",
  "password": "$2a$10$X7Y...",  // Hash de "Admin123!" (usar BCrypt)
  "nombre": "Administrador",
  "apellido": "ZonaGamer",
  "isAdmin": true,  ← ¡ADMIN!
  "active": true,
  "numeroDeTelefono": "+56900000000",
  "puntajeCliente": 0,
  "fechaCreacion": "2024-12-02T00:00:00Z"
}
```

**Opción 2: Script Node.js**
```javascript
const bcrypt = require('bcrypt');
const admin = require('firebase-admin');

// Generar hash de password
const password = 'Admin123!';
const hash = await bcrypt.hash(password, 10);

// Guardar en Firestore
await db.collection('users').doc('admin-001').set({
  email: 'admin@zonagamer.com',
  password: hash,
  nombre: 'Administrador',
  apellido: 'ZonaGamer',
  isAdmin: true,
  active: true,
  numeroDeTelefono: '+56900000000',
  fechaCreacion: admin.firestore.FieldValue.serverTimestamp()
});
```

---

### Paso 3: Login como Admin

**Request:**
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@zonagamer.com",
  "password": "Admin123!"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbi0wMDEiLCJlbWFpbCI6ImFkbWluQHpvbmFnYW1lci5jb20iLCJpc0FkbWluIjp0cnVlLCJub21icmVDb21wbGV0byI6IkFkbWluaXN0cmFkb3IgWm9uYUdhbWVyIiwiaWF0IjoxNzMzMTgxMDAwLCJleHAiOjE3MzMyNjc0MDB9.signature",
  "type": "Bearer",
  "userId": "admin-001",
  "email": "admin@zonagamer.com",
  "nombreCompleto": "Administrador ZonaGamer",
  "isAdmin": true  ← ¡ADMIN!
}
```

**Decodificar en jwt.io:**
```json
{
  "sub": "admin-001",
  "email": "admin@zonagamer.com",
  "isAdmin": true,  ← ADMINISTRADOR
  "nombreCompleto": "Administrador ZonaGamer",
  "iat": 1733181000,
  "exp": 1733267400
}
```

---

### Paso 4: Usuario Normal Intenta Crear Producto (Falla)

**Request:**
```http
POST http://localhost:8080/api/products
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...  ← Token de Juan (USER)
Content-Type: application/json

{
  "nombreProducto": "NVIDIA RTX 5090",
  "descripcion": "Tarjeta gráfica de última generación",
  "precio": 1999.99,
  "stock": 10,
  "categoryId": "gpu",
  "isFeatured": true
}
```

**Response (403 Forbidden):**
```json
{
  "timestamp": "2024-12-02T20:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/products"
}
```

---

### Paso 5: Admin Crea Producto (Éxito)

**Request:**
```http
POST http://localhost:8080/api/products
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...  ← Token de Admin
Content-Type: application/json

{
  "nombreProducto": "NVIDIA RTX 5090",
  "descripcion": "Tarjeta gráfica de última generación",
  "precio": 1999.99,
  "stock": 10,
  "categoryId": "gpu",
  "isFeatured": true
}
```

**Response (201 Created):**
```json
{
  "id": "prod-abc123",
  "nombre": "NVIDIA RTX 5090",
  "descripcion": "Tarjeta gráfica de última generación",
  "precio": 1999.99,
  "stock": 10,
  "imageUrl": null,
  "categoryId": "gpu",
  "isFeatured": true,
  "disponibilidad": true,
  "fechaCreacion": "2024-12-02T20:05:00Z"
}
```

---

### Paso 6: Usuario Normal Puede Ver Productos (Permitido)

**Request:**
```http
GET http://localhost:8080/api/products
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...  ← Token de Juan (USER)
```

**Response (200 OK):**
```json
[
  {
    "id": "prod-abc123",
    "nombre": "NVIDIA RTX 5090",
    "precio": 1999.99,
    ...
  }
]
```

**✅ FUNCIONA** porque `GET /api/products/**` está configurado como `.permitAll()` (público).

---

### Paso 7: Usuario Normal Compra Producto (Permitido)

**Request:**
```http
POST http://localhost:8080/api/cart/add
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...  ← Token de Juan (USER)
Content-Type: application/json

{
  "productId": "prod-abc123",
  "quantity": 1
}
```

**Response (200 OK):**
```json
{
  "id": "cart-user-abc123",
  "userId": "user-abc123",
  "items": [
    {
      "productId": "prod-abc123",
      "productName": "NVIDIA RTX 5090",
      "quantity": 1,
      "precio": 1999.99,
      "subtotal": 1999.99
    }
  ],
  "subtotal": 1999.99,
  "iva": 379.99,
  "total": 2379.98,
  "totalItems": 1
}
```

**✅ FUNCIONA** porque `/api/cart/**` requiere `.authenticated()` (solo estar logueado).

---

## 🔒 Seguridad y Buenas Prácticas

### 1. **Secreto JWT**

**❌ MAL (Hardcoded):**
```yaml
jwt:
  secret: miClaveSecreta123
```

**✅ BIEN (Variable de entorno):**
```yaml
jwt:
  secret: ${JWT_SECRET:default-solo-para-desarrollo}
```

**En producción:**
```bash
export JWT_SECRET=$(openssl rand -base64 64)
```

---

### 2. **Tiempo de Expiración**

**Recomendaciones:**
- **Desarrollo:** 24 horas (86400000 ms)
- **Producción:** 1-2 horas (3600000 - 7200000 ms)
- **Refresh tokens:** 7-30 días (para renovar sin re-login)

```yaml
jwt:
  expiration: 3600000  # 1 hora en producción
```

---

### 3. **Encriptación de Passwords**

**Tu proyecto ya usa BCrypt:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // ← BCrypt con salt automático
}
```

**Ejemplo:**
- Password: `"Admin123!"`
- Hash: `"$2a$10$X7Y5ZqW8rN3pLm9kJhGfCeOuY6tX4vB2cD8aE1fG3hI5jK7lM9nO1"`

**Cada vez que encriptas la misma password, el hash es diferente** (por el "salt" aleatorio).

---

### 4. **HTTPS en Producción**

**⚠️ NUNCA envíes tokens JWT por HTTP (sin cifrar).**

En producción, SIEMPRE usa HTTPS:
```
https://api.zonagamer.com/api/auth/login
```

---

### 5. **Validación de Entrada**

Tu proyecto ya usa `@Valid` con Jakarta Validation:

```java
@PostMapping("/register")
public ResponseEntity<AuthResponseDTO> register(
    @Valid @RequestBody UserRegistrationDTO dto  // ← Valida campos
) {
    // ...
}
```

**UserRegistrationDTO:**
```java
public class UserRegistrationDTO {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    
    @Pattern(regexp = "^\\+569\\d{8}$", message = "Teléfono debe ser formato chileno")
    private String numeroDeTelefono;
    
    // ...
}
```

---

### 6. **Manejo de Errores**

**Tu proyecto ya tiene excepciones personalizadas:**

```java
// 401 Unauthorized (credenciales incorrectas)
throw new UnauthorizedException("Credenciales inválidas");

// 403 Forbidden (no tiene permisos)
// Manejado automáticamente por Spring Security

// 404 Not Found (recurso no existe)
throw new ResourceNotFoundException("Producto no encontrado");
```

---

## 🐞 Troubleshooting

### Problema 1: "Token inválido o expirado"

**Síntoma:**
```json
{
  "error": "Unauthorized",
  "message": "Token inválido o expirado"
}
```

**Causa:** El token expiró (24 horas por defecto).

**Solución:** Hacer login nuevamente:
```http
POST /api/auth/login
```

---

### Problema 2: "Access Denied" (403)

**Síntoma:**
```json
{
  "error": "Forbidden",
  "message": "Access Denied"
}
```

**Causa:** El usuario no tiene el rol necesario.

**Verificar:**
1. Decodifica el token en [jwt.io](https://jwt.io)
2. Verifica el campo `"isAdmin"`
3. Si es `false`, ese usuario NO puede acceder a endpoints de admin

**Solución:** Usar un token de admin o cambiar el usuario a admin en Firebase.

---

### Problema 3: "Usuario no encontrado"

**Síntoma:** El filtro JWT dice "Usuario no encontrado" en los logs.

**Causa:** El `userId` en el token no existe en Firebase Firestore.

**Solución:**
1. Verifica que el documento del usuario existe en Firestore:
   ```
   Colección: users
   Documento ID: <userId del token>
   ```
2. Si no existe, elimina el token y regístrate de nuevo.

---

### Problema 4: Token no se está enviando

**Síntoma:** Postman devuelve 401 aunque tienes un token.

**Causa:** Header incorrecto.

**Solución:** Verifica el header en Postman:
```
Key: Authorization
Value: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**⚠️ IMPORTANTE:** Debe empezar con `Bearer ` (con espacio después).

---

### Problema 5: Secret de JWT incorrecto

**Síntoma:** Error al iniciar la aplicación:
```
Could not resolve placeholder 'jwt.secret'
```

**Causa:** No está configurado `jwt.secret` en `application.yml`.

**Solución:**
```yaml
jwt:
  secret: mi-clave-super-secreta-de-al-menos-256-bits
  expiration: 86400000
```

---

## 📊 Tabla Resumen de Endpoints

| Endpoint | Método | Autenticación | Rol Requerido | Descripción |
|----------|--------|---------------|---------------|-------------|
| `/api/auth/register` | POST | ❌ Pública | - | Registrar nuevo usuario |
| `/api/auth/login` | POST | ❌ Pública | - | Login y obtener token |
| `/api/products` | GET | ❌ Pública | - | Ver productos |
| `/api/products` | POST | ✅ Requerida | `ROLE_ADMIN` | Crear producto |
| `/api/products/{id}` | PUT | ✅ Requerida | `ROLE_ADMIN` | Actualizar producto |
| `/api/products/{id}` | DELETE | ✅ Requerida | `ROLE_ADMIN` | Eliminar producto |
| `/api/cart/add` | POST | ✅ Requerida | `ROLE_USER` o `ROLE_ADMIN` | Agregar al carrito |
| `/api/orders/my-orders` | GET | ✅ Requerida | `ROLE_USER` o `ROLE_ADMIN` | Ver mis órdenes |
| `/api/orders/all` | GET | ✅ Requerida | `ROLE_ADMIN` | Ver todas las órdenes |
| `/api/categories` | GET | ❌ Pública | - | Ver categorías |
| `/api/categories` | POST | ✅ Requerida | `ROLE_ADMIN` | Crear categoría |
| `/api/calendar/**` | * | ✅ Requerida | `ROLE_ADMIN` | Gestión de calendario |

---

## 🎓 Para Tu Presentación Universitaria

### Explicación Simple de JWT

**"JWT es como un pase VIP que te dan al entrar a un concierto."**

1. **Registro/Login** = Comprar la entrada (muestras tu ID, pagas)
2. **Token JWT** = Tu pulsera VIP con tu nombre y permisos
3. **Usar el token** = Mostrar la pulsera en cada puerta del concierto
4. **Expiración** = La pulsera solo sirve ese día (24 horas)
5. **Roles** = VIP (admin) vs General (user)

### Diferencia Admin vs User

**Admin:**
- Puede crear, editar y eliminar productos
- Puede ver todas las órdenes de todos los usuarios
- Puede gestionar categorías
- Puede usar el calendario de eventos

**Usuario Normal:**
- Puede ver productos
- Puede comprar y ver su carrito
- Puede ver solo sus propias órdenes
- NO puede crear/editar productos
- NO puede ver órdenes de otros

### Demostración en Clase

1. **Mostrar registro de usuario normal** → Token con `isAdmin: false`
2. **Intentar crear producto con token de user** → 403 Forbidden
3. **Mostrar login de admin** → Token con `isAdmin: true`
4. **Crear producto con token de admin** → 201 Created ✅
5. **Decodificar tokens en jwt.io** → Mostrar diferencia de roles

---

## 📝 Checklist de Configuración

- [x] `application.yml` tiene `jwt.secret` y `jwt.expiration`
- [x] `JwtService` genera y valida tokens correctamente
- [x] `AuthService` registra usuarios con `isAdmin: false`
- [x] `JwtAuthenticationFilter` intercepta y valida tokens
- [x] `UserPrincipal` asigna roles correctos (ADMIN o USER)
- [x] `SecurityConfig` protege endpoints por rol
- [x] Usuario admin creado manualmente en Firebase con `isAdmin: true`
- [x] Passwords encriptadas con BCrypt
- [x] CORS configurado para permitir frontend

---

## 🚀 Próximos Pasos (Opcional)

Si quieres mejorar tu proyecto:

1. **Refresh Tokens:** Tokens de larga duración para renovar sin re-login
2. **Logout:** Blacklist de tokens invalidados
3. **Rate Limiting:** Limitar intentos de login
4. **2FA:** Autenticación de dos factores
5. **Email Verification:** Verificar email al registrarse

---

**¡Tu sistema JWT está completamente funcional y listo para demostrar! 🎉**

---

**Creado por:** GitHub Copilot  
**Fecha:** 2 de Diciembre, 2025  
**Proyecto:** ZonaGamer Backend - Sistema de Autenticación JWT  
**Versión:** Java 21 + Spring Boot 3.5.7 + Firebase
