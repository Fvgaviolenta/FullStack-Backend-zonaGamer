# 🧪 Pruebas Postman Pendientes - Zona Gamer API

## ✅ Pruebas Ya Realizadas
- ✅ Registro de usuario
- ✅ Login de usuario
- ✅ Visualizar productos
- ✅ Añadir productos al carrito
- ✅ Aumentar cantidad de productos en un carrito
- ✅ Eliminar un producto de un carrito

---

## 📋 Índice de Pruebas Pendientes
1. [Productos - Endpoints Avanzados](#1-productos---endpoints-avanzados)
2. [Carrito - Endpoints Pendientes](#2-carrito---endpoints-pendientes)
3. [Órdenes/Pedidos](#3-órdenes-pedidos)
4. [Categorías](#4-categorías)
5. [Usuarios - Perfil y Administración](#5-usuarios---perfil-y-administración)
6. [Calendario de Eventos (Admin)](#6-calendario-de-eventos-admin)

---

## 1. Productos - Endpoints Avanzados

### 1.1 Obtener Producto por ID (FUNCIONA)
**Endpoint:** `GET /api/products/{id}`  
**Autenticación:** No requerida  
**Descripción:** Obtener detalles de un producto específico

**Ejemplo Request:**
```
GET http://localhost:8080/api/products/AbgOZ9HVy2j8ya6t07Q4
```

**Expected Response:** `200 OK`
```json
{
    "id": "AbgOZ9HVy2j8ya6t07Q4",
    "nombreProducto": "Teclado Mecánico",
    "descripcion": "Teclado gaming RGB",
    "precio": 89.99,
    "stock": 15,
    "categoria": "perifericos",
    "imageUrl": "...",
    "destacado": true
}
```

---

### 1.2 Buscar Productos por Categoría (FUNCIONA)
**Endpoint:** `GET /api/products/category/{categoryId}`  
**Autenticación:** No requerida  
**Descripción:** Filtrar productos por categoría

**Ejemplo Request:**
```
GET http://localhost:8080/api/products/category/consolas
```

**Expected Response:** `200 OK`
```json
[
    {
        "id": "prod-123",
        "nombreProducto": "PlayStation 5",
        "precio": 499.99,
        "categoria": "consolas",
        ...
    }
]
```

---

### 1.3 Obtener Productos Destacados (FUNCIONA)
**Endpoint:** `GET /api/products/featured`  
**Autenticación:** No requerida  
**Descripción:** Obtener todos los productos marcados como destacados

**Ejemplo Request:**
```
GET http://localhost:8080/api/products/featured
```

**Expected Response:** `200 OK`
```json
[
    {
        "id": "prod-001",
        "nombreProducto": "Xbox Series X",
        "destacado": true,
        ...
    }
]
```

---

### 1.4 Buscar Productos por Término
**Endpoint:** `GET /api/products/search?q={searchTerm}`  
**Autenticación:** No requerida  
**Descripción:** Buscar productos por nombre (mínimo 2 caracteres)

**Ejemplo Request:**
```
GET http://localhost:8080/api/products/search?q=teclado
```

**Expected Response:** `200 OK`
```json
[
    {
        "id": "prod-456",
        "nombreProducto": "Teclado Mecánico RGB",
        ...
    }
]
```

---

### 1.5 🔒 Crear Producto (ADMIN) (FUNCIONA)
**Endpoint:** `POST /api/products`  
**Autenticación:** Bearer Token (Admin)  
**Content-Type:** `application/json`  
**Descripción:** Crear un nuevo producto

**Headers:**
```
Authorization: Bearer {tu_token_admin}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "nombreProducto": "Mouse Gamer Pro",
    "descripcion": "Mouse óptico de alta precisión 16000 DPI",
    "precio": 49.99,
    "stock": 30,
    "categoriaId": "perifericos",
    "destacado": false
}
```

**Expected Response:** `201 CREATED`
```json
{
    "id": "AbgOZ9HVy2j8ya6t07Q4",
    "nombreProducto": "Mouse Gamer Pro",
    "descripcion": "Mouse óptico de alta precisión 16000 DPI",
    "precio": 49.99,
    "stock": 30,
    "categoria": "perifericos",
    "destacado": false,
    "imageUrl": null
}
```

---

### 1.6 🔒 Actualizar Producto (ADMIN) (FUNCIONA)
**Endpoint:** `PUT /api/products/{id}`  
**Autenticación:** Bearer Token (Admin)  
**Content-Type:** `application/json`  
**Descripción:** Actualizar un producto existente

**Headers:**
```
Authorization: Bearer {tu_token_admin}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "nombreProducto": "Mouse Gamer Pro V2",
    "descripcion": "Mouse óptico mejorado 20000 DPI",
    "precio": 59.99,
    "stock": 25,
    "categoriaId": "perifericos",
    "destacado": true
}
```

**Expected Response:** `200 OK`

---

### 1.7 🔒 Eliminar Producto (ADMIN) (FUNCIONA)
**Endpoint:** `DELETE /api/products/{id}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Eliminar un producto del catálogo

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
DELETE http://localhost:8080/api/products/prod-789
```

**Expected Response:** `204 NO CONTENT`

---

### 1.8 🔒 Obtener Productos con Bajo Stock (ADMIN) (FUNCIONA)
**Endpoint:** `GET /api/products/low-stock?threshold={number}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Listar productos con stock por debajo del umbral

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
GET http://localhost:8080/api/products/low-stock?threshold=5
```

**Expected Response:** `200 OK`
```json
[
    {
        "id": "prod-111",
        "nombreProducto": "Audífonos Gamer",
        "stock": 3
    }
]
```

---

## 2. Carrito - Endpoints Pendientes

### 2.1 Obtener Carrito del Usuario (FUNCIONA)
**Endpoint:** `GET /api/cart`  
**Autenticación:** Bearer Token  
**Descripción:** Ver el carrito actual del usuario autenticado

**Headers:**
```
Authorization: Bearer {tu_token}
```

**Expected Response:** `200 OK`
```json
{
    "userId": "user-123",
    "items": [
        {
            "productId": "prod-001",
            "productName": "Teclado Mecánico",
            "quantity": 2,
            "precio": 89.99,
            "subtotal": 179.98
        }
    ],
    "total": 179.98,
    "totalItems": 2
}
```

---

### 2.2 Limpiar Carrito (Vaciar Todo)
**Endpoint:** `DELETE /api/cart`  
**Autenticación:** Bearer Token  
**Descripción:** Eliminar todos los productos del carrito

**Headers:**
```
Authorization: Bearer {tu_token}
```

**Expected Response:** `200 OK` (Carrito vacío)

---

## 3. Órdenes/Pedidos

### 3.1 Crear Orden (Checkout) (FUNCIONA)
**Endpoint:** `POST /api/orders/checkout`  
**Autenticación:** Bearer Token  
**Descripción:** Procesar el checkout del carrito y crear una orden

**Headers:**
```
Authorization: Bearer {tu_token}
Content-Type: application/json
```

**Body:**
```json
{
    "direccionDeEnvio": "Av. Libertador 123, Santiago",
    "metodoDePago": "TARJETA_CREDITO",
    "notas": "Entregar en la tarde"
}
```

**Expected Response:** `201 CREATED`
```json
{
    "id": "order-abc123",
    "numeroDeOrden": "ORD-2025-001234",
    "userId": "user-123",
    "items": [...],
    "total": 179.98,
    "status": "PENDIENTE",
    "direccionDeEnvio": "Av. Libertador 123, Santiago",
    "metodoDePago": "TARJETA_CREDITO",
    "fechaCreacion": "2025-12-03T10:30:00"
}
```

---

### 3.2 Obtener Mis Órdenes
**Endpoint:** `GET /api/orders/my-orders`  
**Autenticación:** Bearer Token  
**Descripción:** Ver historial de órdenes del usuario autenticado

**Headers:**
```
Authorization: Bearer {tu_token}
```

**Expected Response:** `200 OK`
```json
[
    {
        "id": "order-abc123",
        "numeroDeOrden": "ORD-2025-001234",
        "total": 179.98,
        "status": "PENDIENTE",
        "fechaCreacion": "2025-12-03T10:30:00"
    }
]
```

---

### 3.3 Obtener Orden por ID
**Endpoint:** `GET /api/orders/{id}`  
**Autenticación:** Bearer Token  
**Descripción:** Ver detalles de una orden específica (solo propia o admin)

**Headers:**
```
Authorization: Bearer {tu_token}
```

**Ejemplo Request:**
```
GET http://localhost:8080/api/orders/order-abc123
```

**Expected Response:** `200 OK`

---

### 3.4 🔒 Obtener Todas las Órdenes (ADMIN)
**Endpoint:** `GET /api/orders`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Listar todas las órdenes del sistema

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Expected Response:** `200 OK`

---

### 3.5 🔒 Filtrar Órdenes por Estado (ADMIN)
**Endpoint:** `GET /api/orders/status/{status}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Filtrar órdenes por estado

**Valores de status:**
- `PENDIENTE`
- `PROCESANDO`
- `ENVIADO`
- `ENTREGADO`
- `CANCELADO`

**Ejemplo Request:**
```
GET http://localhost:8080/api/orders/status/PENDIENTE
```

**Expected Response:** `200 OK`

---

### 3.6 🔒 Actualizar Estado de Orden (ADMIN)
**Endpoint:** `PUT /api/orders/{id}/status?newStatus={status}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Cambiar el estado de una orden

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
PUT http://localhost:8080/api/orders/order-abc123/status?newStatus=ENVIADO
```

**Expected Response:** `200 OK`

---

### 3.7 Cancelar Orden
**Endpoint:** `DELETE /api/orders/{id}`  
**Autenticación:** Bearer Token  
**Descripción:** Cancelar una orden (solo propia o admin)

**Headers:**
```
Authorization: Bearer {tu_token}
```

**Ejemplo Request:**
```
DELETE http://localhost:8080/api/orders/order-abc123
```

**Expected Response:** `204 NO CONTENT`

---

## 4. Categorías

### 4.1 Obtener Todas las Categorías
**Endpoint:** `GET /api/categorias`  
**Autenticación:** No requerida  
**Descripción:** Listar todas las categorías disponibles

**Ejemplo Request:**
```
GET http://localhost:8080/api/categorias
```

**Expected Response:** `200 OK`
```json
[
    {
        "id": "cat-001",
        "nombreCategoria": "Consolas",
        "descripcion": "Consolas de videojuegos",
        "parentId": null
    }
]
```

---

### 4.2 Obtener Categorías Raíz
**Endpoint:** `GET /api/categorias/root`  
**Autenticación:** No requerida  
**Descripción:** Obtener solo las categorías principales (sin padre)

**Ejemplo Request:**
```
GET http://localhost:8080/api/categorias/root
```

**Expected Response:** `200 OK`

---

### 4.3 Obtener Categoría por ID
**Endpoint:** `GET /api/categorias/{id}`  
**Autenticación:** No requerida  
**Descripción:** Ver detalles de una categoría específica

**Ejemplo Request:**
```
GET http://localhost:8080/api/categorias/cat-001
```

**Expected Response:** `200 OK`

---

### 4.4 Obtener Subcategorías
**Endpoint:** `GET /api/categorias/{id}/hija`  
**Autenticación:** No requerida  
**Descripción:** Obtener las categorías hijas de una categoría

**Ejemplo Request:**
```
GET http://localhost:8080/api/categorias/cat-001/hija
```

**Expected Response:** `200 OK`

---

### 4.5 🔒 Crear Categoría (ADMIN)
**Endpoint:** `POST /api/categorias`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Crear una nueva categoría

**Headers:**
```
Authorization: Bearer {tu_token_admin}
Content-Type: application/json
```

**Body:**
```json
{
    "nombreCategoria": "Accesorios",
    "descripcion": "Accesorios para gaming",
    "parentId": null
}
```

**Expected Response:** `201 CREATED`

---

### 4.6 🔒 Actualizar Categoría (ADMIN)
**Endpoint:** `PUT /api/categorias/{id}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Modificar una categoría existente

**Headers:**
```
Authorization: Bearer {tu_token_admin}
Content-Type: application/json
```

**Body:**
```json
{
    "nombreCategoria": "Accesorios Gaming",
    "descripcion": "Accesorios premium para gamers"
}
```

**Expected Response:** `200 OK`

---

### 4.7 🔒 Eliminar Categoría (ADMIN)
**Endpoint:** `DELETE /api/categorias/{id}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Eliminar una categoría

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
DELETE http://localhost:8080/api/categorias/cat-999
```

**Expected Response:** `204 NO CONTENT`

---

## 5. Usuarios - Perfil y Administración

### 5.1 Obtener Mi Perfil
**Endpoint:** `GET /api/users/me`  
**Autenticación:** Bearer Token  
**Descripción:** Ver información del usuario autenticado

**Headers:**
```
Authorization: Bearer {tu_token}
```

**Expected Response:** `200 OK`
```json
{
    "id": "user-123",
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@example.com",
    "numeroDeTelefono": "+56912345678",
    "admin": false,
    "activo": true,
    "fechaCreacion": "2025-01-15T10:00:00"
}
```

---

### 5.2 Actualizar Mi Perfil
**Endpoint:** `PUT /api/users/me`  
**Autenticación:** Bearer Token  
**Descripción:** Actualizar datos del perfil (nombre, apellido, teléfono)

**Headers:**
```
Authorization: Bearer {tu_token}
Content-Type: application/json
```

**Body:**
```json
{
    "nombre": "Juan Carlos",
    "apellido": "Pérez González",
    "numeroDeTelefono": "+56987654321"
}
```

**Expected Response:** `200 OK`

---

### 5.3 Cambiar Mi Contraseña
**Endpoint:** `PUT /api/users/me/password`  
**Autenticación:** Bearer Token  
**Descripción:** Cambiar la contraseña del usuario autenticado

**Headers:**
```
Authorization: Bearer {tu_token}
Content-Type: application/json
```

**Body:**
```json
{
    "contraseñaActual": "MiPassword123!",
    "nuevaContraseña": "NuevaPassword456!"
}
```

**Expected Response:** `204 NO CONTENT`

---

### 5.4 🔒 Obtener Todos los Usuarios (ADMIN)
**Endpoint:** `GET /api/users`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Listar todos los usuarios del sistema

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Expected Response:** `200 OK`

---

### 5.5 🔒 Obtener Usuario por ID (ADMIN)
**Endpoint:** `GET /api/users/{id}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Ver detalles de un usuario específico

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
GET http://localhost:8080/api/users/user-456
```

**Expected Response:** `200 OK`

---

### 5.6 🔒 Promover Usuario a Admin (ADMIN)
**Endpoint:** `PUT /api/users/{id}/promote`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Dar privilegios de administrador a un usuario

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
PUT http://localhost:8080/api/users/user-456/promote
```

**Expected Response:** `204 NO CONTENT`

---

### 5.7 🔒 Revocar Admin (ADMIN)
**Endpoint:** `PUT /api/users/{id}/revoke`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Quitar privilegios de administrador

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
PUT http://localhost:8080/api/users/user-456/revoke
```

**Expected Response:** `204 NO CONTENT`

---

### 5.8 🔒 Desactivar Usuario (ADMIN)
**Endpoint:** `PUT /api/users/{id}/desactivarUser`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Desactivar cuenta de usuario

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
PUT http://localhost:8080/api/users/user-456/desactivarUser
```

**Expected Response:** `204 NO CONTENT`

---

### 5.9 🔒 Activar Usuario (ADMIN)
**Endpoint:** `PUT /api/users/{id}/activarUser`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Reactivar cuenta de usuario

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
PUT http://localhost:8080/api/users/user-456/activarUser
```

**Expected Response:** `204 NO CONTENT`

---

### 5.10 🔒 Estadísticas de Usuarios (ADMIN)
**Endpoint:** `GET /api/users/stats`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Obtener estadísticas generales de usuarios

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Expected Response:** `200 OK`
```json
{
    "totalUsuarios": 150,
    "usuariosActivos": 142,
    "usuariosInactivos": 8
}
```

---

## 6. Calendario de Eventos (ADMIN)

> ⚠️ **IMPORTANTE:** Todos los endpoints de calendario requieren privilegios de administrador

### 6.1 🔒 Crear Evento
**Endpoint:** `POST /api/calendar/eventos`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Crear un nuevo evento en el calendario

**Headers:**
```
Authorization: Bearer {tu_token_admin}
Content-Type: application/json
```

**Body:**
```json
{
    "titulo": "Lanzamiento PlayStation 6",
    "descripcion": "Evento de lanzamiento oficial",
    "fechaInicio": "2025-12-15T10:00:00",
    "fechaFin": "2025-12-15T18:00:00",
    "ubicacion": "Centro de Eventos",
    "tipo": "LANZAMIENTO"
}
```

**Expected Response:** `201 CREATED`

---

### 6.2 🔒 Obtener Todos los Eventos
**Endpoint:** `GET /api/calendar/eventos`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Listar todos los eventos del calendario

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Expected Response:** `200 OK`

---

### 6.3 🔒 Obtener Eventos Pendientes
**Endpoint:** `GET /api/calendar/eventos/pendientes`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Listar eventos que aún no se han completado

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Expected Response:** `200 OK`

---

### 6.4 🔒 Obtener Eventos por Rango de Fechas
**Endpoint:** `GET /api/calendar/eventos/rango?inicio={datetime}&fin={datetime}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Filtrar eventos por rango de fechas

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
GET http://localhost:8080/api/calendar/eventos/rango?inicio=2025-12-01T00:00:00&fin=2025-12-31T23:59:59
```

**Expected Response:** `200 OK`

---

### 6.5 🔒 Obtener Próximos Eventos
**Endpoint:** `GET /api/calendar/eventos/proximos?days={number}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Obtener eventos de los próximos N días (default: 7)

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
GET http://localhost:8080/api/calendar/eventos/proximos?days=14
```

**Expected Response:** `200 OK`

---

### 6.6 🔒 Obtener Evento por ID
**Endpoint:** `GET /api/calendar/evento/{id}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Ver detalles de un evento específico

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
GET http://localhost:8080/api/calendar/evento/event-123
```

**Expected Response:** `200 OK`

---

### 6.7 🔒 Actualizar Evento
**Endpoint:** `PUT /api/calendar/evento/{id}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Modificar un evento existente

**Headers:**
```
Authorization: Bearer {tu_token_admin}
Content-Type: application/json
```

**Body:**
```json
{
    "titulo": "Lanzamiento PlayStation 6 - ACTUALIZADO",
    "descripcion": "Evento pospuesto",
    "fechaInicio": "2025-12-20T10:00:00"
}
```

**Expected Response:** `200 OK`

---

### 6.8 🔒 Marcar Evento como Completado
**Endpoint:** `PUT /api/calendar/events/{id}/complete`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Cambiar estado del evento a completado

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
PUT http://localhost:8080/api/calendar/events/event-123/complete
```

**Expected Response:** `200 OK`

---

### 6.9 🔒 Marcar Evento como Pendiente
**Endpoint:** `PUT /api/calendar/events/{id}/pending`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Cambiar estado del evento a pendiente

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
PUT http://localhost:8080/api/calendar/events/event-123/pending
```

**Expected Response:** `200 OK`

---

### 6.10 🔒 Eliminar Evento
**Endpoint:** `DELETE /api/calendar/events/{id}`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Eliminar un evento del calendario

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Ejemplo Request:**
```
DELETE http://localhost:8080/api/calendar/events/event-123
```

**Expected Response:** `204 NO CONTENT`

---

### 6.11 🔒 Estadísticas del Calendario
**Endpoint:** `GET /api/calendar/stats`  
**Autenticación:** Bearer Token (Admin)  
**Descripción:** Obtener estadísticas de eventos

**Headers:**
```
Authorization: Bearer {tu_token_admin}
```

**Expected Response:** `200 OK`
```json
{
    "totalEventos": 25,
    "eventosPendientes": 10,
    "eventosCompletados": 15
}
```

---

## 📝 Notas Importantes

### Autenticación
- **Bearer Token:** Se obtiene del endpoint de login (`/api/auth/login`)
- **Header format:** `Authorization: Bearer {token_jwt}`
- Los endpoints marcados con 🔒 requieren token de admin

### IDs de Firestore
- Los IDs de productos, categorías, etc. son **document IDs de Firestore**
- Ejemplo: `AbgOZ9HVy2j8ya6t07Q4`
- Para obtener IDs reales, primero consulta los endpoints GET correspondientes

### Estados de Órdenes
Los valores válidos para el estado de una orden son:
- `PENDIENTE`
- `PROCESANDO`
- `ENVIADO`
- `ENTREGADO`
- `CANCELADO`

### Formato de Fechas
Las fechas deben enviarse en formato ISO 8601:
```
2025-12-03T10:30:00
```

---

## 🎯 Resumen de Endpoints por Módulo

| Módulo | Total Endpoints | Públicos | Requieren Auth | Solo Admin |
|--------|----------------|----------|----------------|------------|
| Productos | 8 | 5 | 0 | 3 |
| Carrito | 5 | 0 | 5 | 0 |
| Órdenes | 7 | 0 | 4 | 3 |
| Categorías | 7 | 4 | 0 | 3 |
| Usuarios | 10 | 0 | 3 | 7 |
| Calendario | 11 | 0 | 0 | 11 |
| **TOTAL** | **48** | **9** | **12** | **27** |

---

## 🚀 Orden Recomendado de Pruebas

### Fase 1: Endpoints Públicos (Sin autenticación)
1. ✅ Obtener todos los productos
2. ✅ Obtener producto por ID
3. ✅ Buscar productos por categoría
4. ✅ Obtener productos destacados
5. ✅ Buscar productos por término
6. ✅ Obtener todas las categorías
7. ✅ Obtener categorías raíz
8. ✅ Obtener categoría por ID
9. ✅ Obtener subcategorías

### Fase 2: Usuario Autenticado (Con token)
1. ✅ Obtener mi perfil
2. ✅ Actualizar mi perfil
3. ✅ Cambiar contraseña
4. ✅ Ver mi carrito
5. ✅ Añadir al carrito
6. ✅ Actualizar cantidad en carrito
7. ✅ Eliminar del carrito
8. ✅ Realizar checkout
9. ✅ Ver mis órdenes
10. ✅ Ver detalles de una orden
11. ✅ Cancelar orden

### Fase 3: Administrador (Con token admin)
1. ✅ Crear producto
2. ✅ Actualizar producto
3. ✅ Eliminar producto
4. ✅ Ver productos con bajo stock
5. ✅ Crear categoría
6. ✅ Actualizar categoría
7. ✅ Eliminar categoría
8. ✅ Ver todas las órdenes
9. ✅ Filtrar órdenes por estado
10. ✅ Actualizar estado de orden
11. ✅ Ver todos los usuarios
12. ✅ Ver usuario por ID
13. ✅ Promover a admin
14. ✅ Revocar admin
15. ✅ Desactivar usuario
16. ✅ Activar usuario
17. ✅ Estadísticas de usuarios
18. ✅ Crear evento
19. ✅ Ver eventos
20. ✅ Actualizar evento
21. ✅ Marcar evento completado
22. ✅ Eliminar evento
23. ✅ Estadísticas del calendario

---

## 💡 Tips para Pruebas Efectivas

### 1. Crear una Colección en Postman
Organiza las pruebas en carpetas:
```
Zona Gamer API/
├── 01 - Auth/
├── 02 - Productos/
├── 03 - Carrito/
├── 04 - Órdenes/
├── 05 - Categorías/
├── 06 - Usuarios/
└── 07 - Calendario/
```

### 2. Variables de Entorno
Crea variables en Postman:
- `baseUrl`: `http://localhost:8080`
- `token`: (se actualiza automáticamente después del login)
- `adminToken`: (token de usuario administrador)
- `productId`: (ID de producto de ejemplo)
- `orderId`: (ID de orden de ejemplo)

### 3. Tests Automáticos
Agrega scripts en la pestaña "Tests" de Postman:
```javascript
// Guardar token automáticamente después del login
pm.test("Login successful", function () {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
});
```

### 4. Casos de Error a Probar
- Token inválido o expirado
- IDs inexistentes
- Datos inválidos en body
- Usuario sin permisos
- Stock insuficiente
- Carrito vacío en checkout

---

**¡Buena suerte con tus pruebas! 🎮🚀**
