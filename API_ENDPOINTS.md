# 📘 Documentación de Endpoints - ZonaGamer Backend API

**Versión:** 0.0.1-SNAPSHOT  
**Base URL:** `http://localhost:8080` (desarrollo) | `http://EC2_IP:8080` (producción)  
**Tecnología:** Spring Boot 3.5.7 + Firebase Firestore  
**Autenticación:** JWT Bearer Token  
**Fecha de generación:** 4 de diciembre de 2025

---

## 🔑 Autenticación

Todos los endpoints (excepto `/api/auth/*` y `/api/health`) requieren autenticación JWT.

### Header de Autenticación
```http
Authorization: Bearer <JWT_TOKEN>
```

---

## 📋 Tabla de Contenidos

1. [Authentication (AuthController)](#1-authentication-authcontroller)
2. [Health Check (HealthController)](#2-health-check-healthcontroller)
3. [Categories (CategoryController)](#3-categories-categorycontroller)
4. [Products (ProductController)](#4-products-productcontroller)
5. [Cart (CartController)](#5-cart-cartcontroller)
6. [Orders (OrderController)](#6-orders-ordercontroller)
7. [Users (UserController)](#7-users-usercontroller)
8. [Calendar (CalendarController)](#8-calendar-calendarcontroller)

---

## 1. Authentication (AuthController)

**Base Path:** `/api/auth`

### 1.1 Registrar Usuario
```http
POST /api/auth/register
```

**Descripción:** Registra un nuevo usuario en el sistema.

**Autenticación:** ❌ No requerida

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "contraseña123",
  "nombre": "Juan",
  "apellido": "Pérez",
  "numeroDeTelefono": "+56912345678"
}
```

**Response:** `201 CREATED`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "abc123def456",
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "rol": "ROLE_USER"
}
```

---

### 1.2 Iniciar Sesión
```http
POST /api/auth/login
```

**Descripción:** Autentica un usuario y devuelve un token JWT.

**Autenticación:** ❌ No requerida

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "contraseña123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "abc123def456",
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "rol": "ROLE_USER"
}
```

---

### 1.3 Health Check (Auth Service)
```http
GET /api/auth/health
```

**Descripción:** Verifica que el servicio de autenticación esté funcionando.

**Autenticación:** ❌ No requerida

**Response:** `200 OK`
```json
"Auth service is running!"
```

---

## 2. Health Check (HealthController)

**Base Path:** `/api`

### 2.1 Health Check General
```http
GET /api/health
```

**Descripción:** Verifica el estado general del backend y devuelve información del sistema.

**Autenticación:** ❌ No requerida

**Response:** `200 OK`
```json
{
  "status": "UP",
  "service": "ZonaGamer Backend",
  "timestamp": "1733335654321"
}
```

---

## 3. Categories (CategoryController)

**Base Path:** `/api/categorias`

### 3.1 Obtener Todas las Categorías
```http
GET /api/categorias
```

**Descripción:** Lista todas las categorías disponibles en el sistema.

**Autenticación:** ✅ Requerida

**Response:** `200 OK`
```json
[
  {
    "categoriaId": "cat001",
    "nombreCategoria": "Consolas",
    "descripcion": "Consolas de videojuegos",
    "parentCategoriaId": null,
    "nivel": 0,
    "orden": 1,
    "activo": true,
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-01T10:00:00"
  }
]
```

---

### 3.2 Obtener Categorías Raíz
```http
GET /api/categorias/root
```

**Descripción:** Obtiene solo las categorías de nivel superior (sin padre).

**Autenticación:** ✅ Requerida

**Response:** `200 OK`
```json
[
  {
    "categoriaId": "cat001",
    "nombreCategoria": "Consolas",
    "descripcion": "Consolas de videojuegos",
    "parentCategoriaId": null,
    "nivel": 0,
    "orden": 1,
    "activo": true
  }
]
```

---

### 3.3 Obtener Categoría por ID
```http
GET /api/categorias/{id}
```

**Descripción:** Obtiene los detalles de una categoría específica.

**Autenticación:** ✅ Requerida

**Path Parameters:**
- `id` (String): ID de la categoría

**Response:** `200 OK`
```json
{
  "categoriaId": "cat001",
  "nombreCategoria": "Consolas",
  "descripcion": "Consolas de videojuegos",
  "parentCategoriaId": null,
  "nivel": 0,
  "orden": 1,
  "activo": true
}
```

---

### 3.4 Obtener Subcategorías
```http
GET /api/categorias/{id}/hija
```

**Descripción:** Obtiene todas las subcategorías (hijas) de una categoría específica.

**Autenticación:** ✅ Requerida

**Path Parameters:**
- `id` (String): ID de la categoría padre

**Response:** `200 OK`
```json
[
  {
    "categoriaId": "cat002",
    "nombreCategoria": "PlayStation",
    "descripcion": "Consolas PlayStation",
    "parentCategoriaId": "cat001",
    "nivel": 1,
    "orden": 1,
    "activo": true
  }
]
```

---

### 3.5 Crear Categoría
```http
POST /api/categorias
```

**Descripción:** Crea una nueva categoría en el sistema.

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Request Body:**
```json
{
  "nombreCategoria": "Nintendo Switch",
  "descripcion": "Consolas y accesorios Nintendo Switch",
  "parentCategoriaId": "cat001",
  "orden": 3
}
```

**Response:** `201 CREATED`
```json
{
  "categoriaId": "cat003",
  "nombreCategoria": "Nintendo Switch",
  "descripcion": "Consolas y accesorios Nintendo Switch",
  "parentCategoriaId": "cat001",
  "nivel": 1,
  "orden": 3,
  "activo": true,
  "createdAt": "2025-12-04T10:30:00"
}
```

---

### 3.6 Actualizar Categoría
```http
PUT /api/categorias/{id}
```

**Descripción:** Actualiza los datos de una categoría existente.

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID de la categoría

**Request Body:**
```json
{
  "nombreCategoria": "Nintendo Switch OLED",
  "descripcion": "Consolas Nintendo Switch OLED",
  "parentCategoriaId": "cat001",
  "orden": 3
}
```

**Response:** `200 OK`
```json
{
  "categoriaId": "cat003",
  "nombreCategoria": "Nintendo Switch OLED",
  "descripcion": "Consolas Nintendo Switch OLED",
  "parentCategoriaId": "cat001",
  "nivel": 1,
  "orden": 3,
  "activo": true,
  "updatedAt": "2025-12-04T11:00:00"
}
```

---

### 3.7 Eliminar Categoría
```http
DELETE /api/categorias/{id}
```

**Descripción:** Elimina una categoría del sistema.

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID de la categoría

**Response:** `204 NO CONTENT`

---

## 4. Products (ProductController)

**Base Path:** `/api/products`

### 4.1 Obtener Todos los Productos
```http
GET /api/products
```

**Descripción:** Lista todos los productos disponibles.

**Autenticación:** ✅ Requerida

**Response:** `200 OK`
```json
[
  {
    "productoId": "prod001",
    "nombreProducto": "PlayStation 5",
    "descripcion": "Consola de última generación",
    "precio": 499990.0,
    "stock": 25,
    "categoriaId": "cat002",
    "imageUrl": "https://storage.googleapis.com/.../ps5.jpg",
    "destacado": true,
    "activo": true,
    "createdAt": "2025-11-01T10:00:00"
  }
]
```

---

### 4.2 Obtener Producto por ID
```http
GET /api/products/{id}
```

**Descripción:** Obtiene los detalles de un producto específico.

**Autenticación:** ✅ Requerida

**Path Parameters:**
- `id` (String): ID del producto

**Response:** `200 OK`
```json
{
  "productoId": "prod001",
  "nombreProducto": "PlayStation 5",
  "descripcion": "Consola de última generación",
  "precio": 499990.0,
  "stock": 25,
  "categoriaId": "cat002",
  "imageUrl": "https://storage.googleapis.com/.../ps5.jpg",
  "destacado": true,
  "activo": true
}
```

---

### 4.3 Obtener Productos por Categoría
```http
GET /api/products/category/{categoryId}
```

**Descripción:** Lista todos los productos de una categoría específica.

**Autenticación:** ✅ Requerida

**Path Parameters:**
- `categoryId` (String): ID de la categoría

**Response:** `200 OK`
```json
[
  {
    "productoId": "prod001",
    "nombreProducto": "PlayStation 5",
    "categoriaId": "cat002",
    "precio": 499990.0,
    "stock": 25
  }
]
```

---

### 4.4 Obtener Productos Destacados
```http
GET /api/products/featured
```

**Descripción:** Lista todos los productos marcados como destacados.

**Autenticación:** ✅ Requerida

**Response:** `200 OK`
```json
[
  {
    "productoId": "prod001",
    "nombreProducto": "PlayStation 5",
    "precio": 499990.0,
    "destacado": true,
    "imageUrl": "https://storage.googleapis.com/.../ps5.jpg"
  }
]
```

---

### 4.5 Buscar Productos
```http
GET /api/products/search?q={searchTerm}
```

**Descripción:** Busca productos por nombre o descripción (mínimo 2 caracteres).

**Autenticación:** ✅ Requerida

**Query Parameters:**
- `q` (String): Término de búsqueda (mínimo 2 caracteres)

**Response:** `200 OK`
```json
[
  {
    "productoId": "prod001",
    "nombreProducto": "PlayStation 5",
    "descripcion": "Consola de última generación",
    "precio": 499990.0
  }
]
```

**Errores:**
- `400 BAD REQUEST`: Si el término de búsqueda tiene menos de 2 caracteres

---

### 4.6 Crear Producto
```http
POST /api/products
```

**Descripción:** Crea un nuevo producto en el catálogo.

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Request Body:**
```json
{
  "nombreProducto": "Xbox Series X",
  "descripcion": "Consola Xbox de última generación",
  "precio": 549990.0,
  "stock": 15,
  "categoriaId": "cat003",
  "imageUrl": "https://example.com/xbox.jpg",
  "destacado": true
}
```

**Response:** `201 CREATED`
```json
{
  "productoId": "prod010",
  "nombreProducto": "Xbox Series X",
  "descripcion": "Consola Xbox de última generación",
  "precio": 549990.0,
  "stock": 15,
  "categoriaId": "cat003",
  "imageUrl": "https://example.com/xbox.jpg",
  "destacado": true,
  "activo": true,
  "createdAt": "2025-12-04T12:00:00"
}
```

---

### 4.7 Actualizar Producto
```http
PUT /api/products/{id}
```

**Descripción:** Actualiza los datos de un producto existente.

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del producto

**Request Body:**
```json
{
  "nombreProducto": "Xbox Series X (Actualizado)",
  "descripcion": "Consola Xbox con 1TB de almacenamiento",
  "precio": 529990.0,
  "stock": 20,
  "categoriaId": "cat003",
  "destacado": true
}
```

**Response:** `200 OK`
```json
{
  "productoId": "prod010",
  "nombreProducto": "Xbox Series X (Actualizado)",
  "precio": 529990.0,
  "stock": 20,
  "updatedAt": "2025-12-04T13:00:00"
}
```

---

### 4.8 Eliminar Producto
```http
DELETE /api/products/{id}
```

**Descripción:** Elimina un producto del catálogo.

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del producto

**Response:** `204 NO CONTENT`

---

### 4.9 Obtener Productos con Bajo Stock
```http
GET /api/products/low-stock?threshold={cantidad}
```

**Descripción:** Lista productos con stock inferior al umbral especificado.

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Query Parameters:**
- `threshold` (int, opcional): Umbral de stock (default: 10)

**Response:** `200 OK`
```json
[
  {
    "productoId": "prod005",
    "nombreProducto": "Control DualSense",
    "stock": 5,
    "precio": 69990.0
  }
]
```

---

## 5. Cart (CartController)

**Base Path:** `/api/cart`

### 5.1 Obtener Carrito del Usuario
```http
GET /api/cart
```

**Descripción:** Obtiene el carrito de compras del usuario autenticado.

**Autenticación:** ✅ Requerida

**Response:** `200 OK`
```json
{
  "cartId": "cart123",
  "userId": "user456",
  "items": [
    {
      "productId": "prod001",
      "nombreProducto": "PlayStation 5",
      "precio": 499990.0,
      "quantity": 1,
      "imageUrl": "https://storage.googleapis.com/.../ps5.jpg",
      "subtotal": 499990.0
    }
  ],
  "totalItems": 1,
  "totalPrice": 499990.0,
  "updatedAt": "2025-12-04T14:00:00"
}
```

---

### 5.2 Agregar Producto al Carrito
```http
POST /api/cart/add
```

**Descripción:** Agrega un producto al carrito del usuario autenticado.

**Autenticación:** ✅ Requerida

**Request Body:**
```json
{
  "productId": "prod001",
  "quantity": 2
}
```

**Response:** `200 OK`
```json
{
  "cartId": "cart123",
  "userId": "user456",
  "items": [
    {
      "productId": "prod001",
      "nombreProducto": "PlayStation 5",
      "precio": 499990.0,
      "quantity": 2,
      "subtotal": 999980.0
    }
  ],
  "totalItems": 2,
  "totalPrice": 999980.0
}
```

---

### 5.3 Actualizar Cantidad de Producto en Carrito
```http
PUT /api/cart/items/{productId}?quantity={cantidad}
```

**Descripción:** Actualiza la cantidad de un producto específico en el carrito.

**Autenticación:** ✅ Requerida

**Path Parameters:**
- `productId` (String): ID del producto

**Query Parameters:**
- `quantity` (int): Nueva cantidad del producto

**Response:** `200 OK`
```json
{
  "cartId": "cart123",
  "userId": "user456",
  "items": [
    {
      "productId": "prod001",
      "quantity": 3,
      "subtotal": 1499970.0
    }
  ],
  "totalPrice": 1499970.0
}
```

---

### 5.4 Eliminar Producto del Carrito
```http
DELETE /api/cart/items/{productId}
```

**Descripción:** Elimina un producto específico del carrito.

**Autenticación:** ✅ Requerida

**Path Parameters:**
- `productId` (String): ID del producto a eliminar

**Response:** `200 OK`
```json
{
  "cartId": "cart123",
  "userId": "user456",
  "items": [],
  "totalItems": 0,
  "totalPrice": 0.0
}
```

---

### 5.5 Vaciar Carrito
```http
DELETE /api/cart
```

**Descripción:** Elimina todos los productos del carrito del usuario.

**Autenticación:** ✅ Requerida

**Response:** `204 NO CONTENT`

---

## 6. Orders (OrderController)

**Base Path:** `/api/orders`

### 6.1 Realizar Checkout
```http
POST /api/orders/checkout
```

**Descripción:** Procesa el checkout del carrito y crea una orden de compra.

**Autenticación:** ✅ Requerida

**Request Body:**
```json
{
  "direccionEnvio": "Av. Libertador 1234, Santiago, Chile",
  "metodoDePago": "CREDIT_CARD",
  "notas": "Entregar en horario de oficina"
}
```

**Response:** `201 CREATED`
```json
{
  "orderId": "order789",
  "userId": "user456",
  "numeroDeOrden": "ORD-20251204-001",
  "items": [
    {
      "productId": "prod001",
      "nombreProducto": "PlayStation 5",
      "cantidad": 1,
      "precioUnitario": 499990.0,
      "subtotal": 499990.0
    }
  ],
  "subtotal": 499990.0,
  "impuestos": 0.0,
  "costoEnvio": 5000.0,
  "total": 504990.0,
  "status": "PENDING",
  "direccionEnvio": "Av. Libertador 1234, Santiago, Chile",
  "metodoDePago": "CREDIT_CARD",
  "notas": "Entregar en horario de oficina",
  "createdAt": "2025-12-04T15:00:00"
}
```

---

### 6.2 Obtener Mis Órdenes
```http
GET /api/orders/my-orders
```

**Descripción:** Lista todas las órdenes del usuario autenticado.

**Autenticación:** ✅ Requerida

**Response:** `200 OK`
```json
[
  {
    "orderId": "order789",
    "numeroDeOrden": "ORD-20251204-001",
    "total": 504990.0,
    "status": "PENDING",
    "createdAt": "2025-12-04T15:00:00"
  }
]
```

---

### 6.3 Obtener Orden por ID
```http
GET /api/orders/{id}
```

**Descripción:** Obtiene los detalles de una orden específica. Los usuarios solo pueden ver sus propias órdenes, los admins pueden ver cualquier orden.

**Autenticación:** ✅ Requerida

**Path Parameters:**
- `id` (String): ID de la orden

**Response:** `200 OK`
```json
{
  "orderId": "order789",
  "userId": "user456",
  "numeroDeOrden": "ORD-20251204-001",
  "items": [
    {
      "productId": "prod001",
      "nombreProducto": "PlayStation 5",
      "cantidad": 1,
      "precioUnitario": 499990.0,
      "subtotal": 499990.0
    }
  ],
  "total": 504990.0,
  "status": "PENDING",
  "direccionEnvio": "Av. Libertador 1234, Santiago, Chile"
}
```

**Errores:**
- `403 FORBIDDEN`: Si un usuario intenta ver la orden de otro usuario

---

### 6.4 Obtener Todas las Órdenes (Admin)
```http
GET /api/orders
```

**Descripción:** Lista todas las órdenes del sistema (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Response:** `200 OK`
```json
[
  {
    "orderId": "order789",
    "userId": "user456",
    "numeroDeOrden": "ORD-20251204-001",
    "total": 504990.0,
    "status": "PENDING",
    "createdAt": "2025-12-04T15:00:00"
  }
]
```

---

### 6.5 Obtener Órdenes por Estado (Admin)
```http
GET /api/orders/status/{status}
```

**Descripción:** Lista todas las órdenes con un estado específico (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `status` (OrderStatus): Estado de la orden
  - Valores válidos: `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

**Response:** `200 OK`
```json
[
  {
    "orderId": "order789",
    "numeroDeOrden": "ORD-20251204-001",
    "status": "PENDING",
    "total": 504990.0
  }
]
```

---

### 6.6 Actualizar Estado de Orden (Admin)
```http
PUT /api/orders/{id}/status?newStatus={status}
```

**Descripción:** Actualiza el estado de una orden (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID de la orden

**Query Parameters:**
- `newStatus` (OrderStatus): Nuevo estado
  - Valores válidos: `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

**Response:** `200 OK`
```json
{
  "orderId": "order789",
  "numeroDeOrden": "ORD-20251204-001",
  "status": "PROCESSING",
  "updatedAt": "2025-12-04T16:00:00"
}
```

---

### 6.7 Cancelar Orden
```http
DELETE /api/orders/{id}
```

**Descripción:** Cancela una orden. Los usuarios solo pueden cancelar sus propias órdenes en estado PENDING. Los administradores pueden cancelar cualquier orden en cualquier estado.

**Autenticación:** ✅ Requerida

**Path Parameters:**
- `id` (String): ID de la orden

**Response:** `204 NO CONTENT`

**Errores:**
- `403 FORBIDDEN`: Si un usuario intenta cancelar la orden de otro usuario
- `400 BAD REQUEST`: Si un usuario intenta cancelar una orden que no está en estado PENDING

---

## 7. Users (UserController)

**Base Path:** `/api/users`

### 7.1 Obtener Perfil del Usuario Actual
```http
GET /api/users/me
```

**Descripción:** Obtiene el perfil del usuario autenticado.

**Autenticación:** ✅ Requerida

**Response:** `200 OK`
```json
{
  "userId": "user456",
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "numeroDeTelefono": "+56912345678",
  "rol": "ROLE_USER",
  "activo": true,
  "createdAt": "2025-11-01T10:00:00"
}
```

---

### 7.2 Actualizar Perfil del Usuario
```http
PUT /api/users/me
```

**Descripción:** Actualiza los datos del perfil del usuario autenticado.

**Autenticación:** ✅ Requerida

**Request Body:**
```json
{
  "nombre": "Juan Carlos",
  "apellido": "Pérez González",
  "numeroDeTelefono": "+56987654321"
}
```

**Response:** `200 OK`
```json
{
  "userId": "user456",
  "email": "usuario@example.com",
  "nombre": "Juan Carlos",
  "apellido": "Pérez González",
  "numeroDeTelefono": "+56987654321",
  "updatedAt": "2025-12-04T17:00:00"
}
```

**Errores:**
- `400 BAD REQUEST`: Si nombre o apellido están vacíos

---

### 7.3 Cambiar Contraseña
```http
PUT /api/users/me/password
```

**Descripción:** Cambia la contraseña del usuario autenticado.

**Autenticación:** ✅ Requerida

**Request Body:**
```json
{
  "contraseñaActual": "contraseña123",
  "nuevaContraseña": "nuevaContraseña456"
}
```

**Response:** `204 NO CONTENT`

**Errores:**
- `400 BAD REQUEST`: Si falta contraseña actual o nueva contraseña
- `401 UNAUTHORIZED`: Si la contraseña actual es incorrecta

---

### 7.4 Obtener Todos los Usuarios (Admin)
```http
GET /api/users
```

**Descripción:** Lista todos los usuarios del sistema (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Response:** `200 OK`
```json
[
  {
    "userId": "user456",
    "email": "usuario@example.com",
    "nombre": "Juan",
    "apellido": "Pérez",
    "rol": "ROLE_USER",
    "activo": true,
    "createdAt": "2025-11-01T10:00:00"
  }
]
```

---

### 7.5 Obtener Usuario por ID (Admin)
```http
GET /api/users/{id}
```

**Descripción:** Obtiene los detalles de un usuario específico (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del usuario

**Response:** `200 OK`
```json
{
  "userId": "user456",
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "numeroDeTelefono": "+56912345678",
  "rol": "ROLE_USER",
  "activo": true
}
```

---

### 7.6 Promover a Administrador (Admin)
```http
PUT /api/users/{id}/promote
```

**Descripción:** Otorga privilegios de administrador a un usuario (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del usuario

**Response:** `204 NO CONTENT`

---

### 7.7 Revocar Privilegios de Administrador (Admin)
```http
PUT /api/users/{id}/revoke
```

**Descripción:** Quita privilegios de administrador a un usuario (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del usuario

**Response:** `204 NO CONTENT`

---

### 7.8 Desactivar Usuario (Admin)
```http
PUT /api/users/{id}/desactivarUser
```

**Descripción:** Desactiva una cuenta de usuario (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del usuario

**Response:** `204 NO CONTENT`

---

### 7.9 Activar Usuario (Admin)
```http
PUT /api/users/{id}/activarUser
```

**Descripción:** Activa una cuenta de usuario previamente desactivada (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del usuario

**Response:** `204 NO CONTENT`

---

### 7.10 Obtener Estadísticas de Usuarios (Admin)
```http
GET /api/users/stats
```

**Descripción:** Obtiene estadísticas generales sobre usuarios del sistema (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Response:** `200 OK`
```json
{
  "totalUsuarios": 150,
  "usuariosActivos": 142,
  "usuariosInactivos": 8
}
```

---

## 8. Calendar (CalendarController)

**Base Path:** `/api/calendar`

**Nota:** Todos los endpoints de este módulo requieren permisos de ROLE_ADMIN.

### 8.1 Crear Evento
```http
POST /api/calendar/eventos
```

**Descripción:** Crea un nuevo evento en el calendario (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Request Body:**
```json
{
  "titulo": "Lanzamiento PlayStation VR2",
  "descripcion": "Evento de lanzamiento del nuevo sistema de realidad virtual",
  "fechaInicio": "2025-12-15T10:00:00",
  "fechaFin": "2025-12-15T18:00:00",
  "ubicacion": "Tienda principal Santiago",
  "tipoEvento": "LAUNCH"
}
```

**Response:** `201 CREATED`
```json
{
  "eventoId": "evt123",
  "titulo": "Lanzamiento PlayStation VR2",
  "descripcion": "Evento de lanzamiento del nuevo sistema de realidad virtual",
  "fechaInicio": "2025-12-15T10:00:00",
  "fechaFin": "2025-12-15T18:00:00",
  "ubicacion": "Tienda principal Santiago",
  "tipoEvento": "LAUNCH",
  "status": "PENDING",
  "createdBy": "admin789",
  "createdAt": "2025-12-04T18:00:00"
}
```

---

### 8.2 Obtener Todos los Eventos
```http
GET /api/calendar/eventos
```

**Descripción:** Lista todos los eventos del calendario (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Response:** `200 OK`
```json
[
  {
    "eventoId": "evt123",
    "titulo": "Lanzamiento PlayStation VR2",
    "fechaInicio": "2025-12-15T10:00:00",
    "fechaFin": "2025-12-15T18:00:00",
    "status": "PENDING",
    "tipoEvento": "LAUNCH"
  }
]
```

---

### 8.3 Obtener Eventos Pendientes
```http
GET /api/calendar/eventos/pendientes
```

**Descripción:** Lista todos los eventos con estado PENDING (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Response:** `200 OK`
```json
[
  {
    "eventoId": "evt123",
    "titulo": "Lanzamiento PlayStation VR2",
    "status": "PENDING",
    "fechaInicio": "2025-12-15T10:00:00"
  }
]
```

---

### 8.4 Obtener Eventos por Rango de Fechas
```http
GET /api/calendar/eventos/rango?inicio={fechaInicio}&fin={fechaFin}
```

**Descripción:** Lista eventos dentro de un rango de fechas específico (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Query Parameters:**
- `inicio` (LocalDateTime): Fecha y hora de inicio (formato ISO: `2025-12-01T00:00:00`)
- `fin` (LocalDateTime): Fecha y hora de fin (formato ISO: `2025-12-31T23:59:59`)

**Response:** `200 OK`
```json
[
  {
    "eventoId": "evt123",
    "titulo": "Lanzamiento PlayStation VR2",
    "fechaInicio": "2025-12-15T10:00:00",
    "fechaFin": "2025-12-15T18:00:00"
  }
]
```

---

### 8.5 Obtener Próximos Eventos
```http
GET /api/calendar/eventos/proximos?days={dias}
```

**Descripción:** Lista eventos que ocurrirán en los próximos N días (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Query Parameters:**
- `days` (int, opcional): Número de días hacia el futuro (default: 7)

**Response:** `200 OK`
```json
[
  {
    "eventoId": "evt123",
    "titulo": "Lanzamiento PlayStation VR2",
    "fechaInicio": "2025-12-15T10:00:00",
    "diasRestantes": 11
  }
]
```

---

### 8.6 Obtener Evento por ID
```http
GET /api/calendar/eventos/{id}
```

**Descripción:** Obtiene los detalles de un evento específico (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del evento

**Response:** `200 OK`
```json
{
  "eventoId": "evt123",
  "titulo": "Lanzamiento PlayStation VR2",
  "descripcion": "Evento de lanzamiento del nuevo sistema de realidad virtual",
  "fechaInicio": "2025-12-15T10:00:00",
  "fechaFin": "2025-12-15T18:00:00",
  "ubicacion": "Tienda principal Santiago",
  "tipoEvento": "LAUNCH",
  "status": "PENDING"
}
```

---

### 8.7 Actualizar Evento
```http
PUT /api/calendar/eventos/{id}
```

**Descripción:** Actualiza los datos de un evento existente (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del evento

**Request Body:**
```json
{
  "titulo": "Lanzamiento PlayStation VR2 - ACTUALIZADO",
  "descripcion": "Evento extendido hasta las 20:00",
  "fechaInicio": "2025-12-15T10:00:00",
  "fechaFin": "2025-12-15T20:00:00",
  "ubicacion": "Tienda principal Santiago",
  "tipoEvento": "LAUNCH"
}
```

**Response:** `200 OK`
```json
{
  "eventoId": "evt123",
  "titulo": "Lanzamiento PlayStation VR2 - ACTUALIZADO",
  "fechaFin": "2025-12-15T20:00:00",
  "updatedAt": "2025-12-04T19:00:00"
}
```

---

### 8.8 Marcar Evento como Completado
```http
PUT /api/calendar/eventos/{id}/complete
```

**Descripción:** Marca un evento como completado (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del evento

**Response:** `200 OK`
```json
{
  "eventoId": "evt123",
  "titulo": "Lanzamiento PlayStation VR2",
  "status": "COMPLETED",
  "updatedAt": "2025-12-15T20:30:00"
}
```

---

### 8.9 Marcar Evento como Pendiente
```http
PUT /api/calendar/eventos/{id}/pending
```

**Descripción:** Marca un evento como pendiente (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del evento

**Response:** `200 OK`
```json
{
  "eventoId": "evt123",
  "titulo": "Lanzamiento PlayStation VR2",
  "status": "PENDING",
  "updatedAt": "2025-12-04T19:30:00"
}
```

---

### 8.10 Eliminar Evento
```http
DELETE /api/calendar/eventos/{id}
```

**Descripción:** Elimina un evento del calendario (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Path Parameters:**
- `id` (String): ID del evento

**Response:** `204 NO CONTENT`

---

### 8.11 Obtener Estadísticas del Calendario
```http
GET /api/calendar/stats
```

**Descripción:** Obtiene estadísticas generales sobre eventos del calendario (solo administradores).

**Autenticación:** ✅ Requerida (ROLE_ADMIN)

**Response:** `200 OK`
```json
{
  "totalEventos": 25,
  "eventosPendientes": 15,
  "eventosCompletados": 10
}
```

---

## 📊 Resumen de Endpoints

| Módulo | Total Endpoints | Públicos | Autenticados | Solo Admin |
|--------|----------------|----------|--------------|------------|
| Authentication | 3 | 3 | 0 | 0 |
| Health Check | 1 | 1 | 0 | 0 |
| Categories | 7 | 0 | 4 | 3 |
| Products | 9 | 0 | 5 | 4 |
| Cart | 5 | 0 | 5 | 0 |
| Orders | 7 | 0 | 3 | 4 |
| Users | 10 | 0 | 3 | 7 |
| Calendar | 11 | 0 | 0 | 11 |
| **TOTAL** | **53** | **4** | **20** | **29** |

---

## 🔒 Códigos de Estado HTTP

| Código | Descripción | Casos de Uso |
|--------|-------------|--------------|
| `200 OK` | Solicitud exitosa | GET, PUT exitosos |
| `201 CREATED` | Recurso creado exitosamente | POST exitosos |
| `204 NO CONTENT` | Solicitud exitosa sin contenido | DELETE, PUT sin respuesta |
| `400 BAD REQUEST` | Solicitud inválida | Validación fallida, parámetros incorrectos |
| `401 UNAUTHORIZED` | No autenticado | Token JWT inválido o faltante |
| `403 FORBIDDEN` | No autorizado | Usuario sin permisos suficientes |
| `404 NOT FOUND` | Recurso no encontrado | ID inválido, recurso eliminado |
| `500 INTERNAL SERVER ERROR` | Error del servidor | Excepciones no controladas |

---

## 🛠️ Tecnologías y Configuración

### Stack Tecnológico
- **Backend Framework:** Spring Boot 3.5.7
- **Base de Datos:** Firebase Firestore (NoSQL)
- **Almacenamiento:** Firebase Storage
- **Autenticación:** JWT (JSON Web Tokens) + Firebase Admin SDK
- **Seguridad:** Spring Security 6.5.6
- **Java Version:** 21 (Eclipse Adoptium)
- **Build Tool:** Maven 3.9+

### Configuración de Servidor
- **Puerto Desarrollo:** 8080
- **Puerto Producción (EC2):** 8080
- **Context Path:** `/`
- **Perfiles:** `default`, `prod`

### CORS Configuration
El backend acepta solicitudes de:
- `http://localhost:3000` (React development)
- `http://localhost:5173` (Vite development)
- `*.vercel.app` (Despliegue Vercel)
- `*.netlify.app` (Despliegue Netlify)

### Firebase Configuration
- **Project ID:** `zonagamer-fullstack`
- **Storage Bucket:** `zonagamer-fullstack.appspot.com`
- **Database URL:** `https://zonagamer-fullstack.firebaseio.com`

---

## 📝 Notas Importantes

1. **Autenticación JWT:**
   - Token expira en 24 horas (86400000 ms)
   - Incluir header `Authorization: Bearer <token>` en todas las solicitudes autenticadas
   - Token se obtiene en `/api/auth/login` o `/api/auth/register`

2. **Roles de Usuario:**
   - `ROLE_USER`: Usuario estándar (puede gestionar su carrito, órdenes y perfil)
   - `ROLE_ADMIN`: Administrador (acceso completo a gestión de productos, categorías, usuarios, órdenes y calendario)

3. **Manejo de Errores:**
   - Todos los endpoints devuelven respuestas JSON estructuradas
   - Los errores incluyen mensaje descriptivo y timestamp
   - Validaciones de entrada implementadas con Bean Validation

4. **Limitaciones:**
   - Búsqueda de productos requiere mínimo 2 caracteres
   - Usuarios solo pueden cancelar órdenes en estado PENDING
   - Stock de productos se valida al agregar al carrito y hacer checkout

5. **Despliegue:**
   - Servidor desarrollo: `http://localhost:8080`
   - Servidor producción (EC2): Configurar variable `server.address` en application-prod.properties
   - Health checks disponibles en `/api/health` y `/api/auth/health`

---

## 🚀 Próximos Pasos para Desarrollo

1. Implementar paginación en endpoints de listado masivo
2. Agregar filtros avanzados para productos (rango de precio, ordenamiento)
3. Implementar notificaciones push para cambios de estado de órdenes
4. Agregar webhooks para integraciones de pago
5. Implementar sistema de reviews y ratings de productos
6. Agregar endpoint para reportes y analytics

---

**Generado automáticamente por:** GitHub Copilot  
**Fecha:** 4 de diciembre de 2025  
**Versión del documento:** 1.0.0
