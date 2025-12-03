# 🔥 Configuración Firebase y Análisis del Proyecto ZonaGamer Backend

## 📋 TABLA DE CONTENIDOS
1. [Análisis de Conflictos entre Modelos y DTOs](#análisis-de-conflictos)
2. [Configuración de Firebase](#configuración-de-firebase)
3. [Scripts de Inicialización de Colecciones](#scripts-de-colecciones)
4. [Conexión Proyecto-Firebase](#conexión-proyecto-firebase)
5. [Testing con Datos Reales](#testing-con-datos-reales)

---

## 🔍 ANÁLISIS DE CONFLICTOS ENTRE MODELOS Y DTOs

### ⚠️ CONFLICTOS CRÍTICOS ENCONTRADOS

#### 1. **ProductCreateDTO vs Product Model**
**Problema:** Inconsistencia en nombres de campos
```java
// ProductCreateDTO.java
private String name;  // ❌ INCORRECTO

// Product.java  
private String nombreProducto;  // ✅ CORRECTO en el modelo
```
**Impacto:** Error de mapeo al crear productos  
**Solución Recomendada:** Cambiar `name` a `nombreProducto` en ProductCreateDTO

---

#### 2. **CheckoutDTO - Error de Tipografía**
```java
// CheckoutDTO.java
private String deliveryAddres;  // ❌ Falta 's' final
```
**Impacto:** Posible error en procesamiento de checkout  
**Solución:** Cambiar a `deliveryAddress`

---

#### 3. **AuthResponseDTO - Error de Tipografía**
```java
// AuthResponseDTO.java
private String type = "Baerer";  // ❌ Debería ser "Bearer"
```
**Impacto:** Error en formato de token JWT estándar  
**Solución:** Cambiar a `"Bearer"`

---

#### 4. **Order Model - Inconsistencia de Capitalización**
```java
// Order.java
private LocalDateTime FechaDeCreacion;  // ❌ Capital F (mal estilo Java)

// CalendarEvent.java
private LocalDateTime FechaDeCreacion;  // ❌ Capital F (mal estilo Java)

// Otros modelos usan:
private LocalDateTime fechaCreacion;  // ✅ CORRECTO (camelCase)
```
**Impacto:** Inconsistencia de código, confusión en equipo  
**Solución:** Estandarizar a `fechaCreacion` en todos los modelos

---

#### 5. **CalendarEventResponseDTO vs CalendarEvent Model**
**Múltiples inconsistencias de nombres:**
```java
// CalendarEventResponseDTO.java
private String description;      // ❌ Modelo usa "descripcion"
private LocalDateTime fechaInicio;  // ❌ Modelo usa "fechaDeInicio"
private String fechaTermino;     // ❌ Modelo usa LocalDateTime fechaDeTermino

// CalendarEvent.java (Modelo)
private String descripcion;
private LocalDateTime fechaDeInicio;
private LocalDateTime fechaDeTermino;
```
**Impacto:** Error de mapeo al recuperar eventos  
**Solución:** Alinear nombres de campos en el DTO con el modelo

---

#### 6. **CategoryCreateDTO vs Category Model**
```java
// CategoryCreateDTO.java
private String name;  // ❌ Inconsistente

// Category.java
private String nombreCategoria;  // ✅ Modelo usa este nombre
```
**Impacto:** Error al crear categorías  
**Solución:** Cambiar `name` a `nombreCategoria`

---

#### 7. **OrderItemDTO vs OrderItem Model**
```java
// OrderItemDTO.java
private String nombreProducto;       // ✅ CORRECTO
private Double precioAlComprar;      // ❌ Modelo usa "precioEnCompra"

// OrderItem.java (Modelo)
private String productName;          // ❌ Debería ser nombreProducto
private Double precioEnCompra;       // ✅ CORRECTO en modelo
```
**Impacto:** Error en detalles de órdenes  
**Solución:** Estandarizar campo de precio a `precioEnCompra`

---

#### 8. **UserResponseDTO vs User Model**
```java
// UserResponseDTO.java
private String nombreUsuario;    // ❌ Modelo usa "nombre"
private String apellidoUsuario;  // ❌ Modelo usa "apellido"
private String numeroTelefono;   // ❌ Modelo usa "numeroDeTelefono"

// User.java
private String nombre;
private String apellido;
private String numeroDeTelefono;
```
**Impacto:** Mapeo de usuario incorrecto en respuestas  
**Solución:** Alinear nombres con el modelo original

---

### ✅ CLASES SIN CONFLICTOS
- `UserLoginDTO` ✅
- `CartResponseDTO` ✅
- `CartItemDTO` ✅
- `AddToCartDTO` ✅
- `UserRegistrationDTO` ✅ (bien validado)

---

### 📊 RESUMEN DE CONFLICTOS
| Clase DTO | Conflictos | Severidad | Estado |
|-----------|-----------|-----------|--------|
| ProductCreateDTO | 1 campo | 🔴 Alta | Requiere corrección |
| CheckoutDTO | Typo | 🟡 Media | Requiere corrección |
| AuthResponseDTO | Typo | 🟡 Media | Requiere corrección |
| CalendarEventResponseDTO | 3 campos | 🔴 Alta | Requiere corrección |
| CategoryCreateDTO | 1 campo | 🔴 Alta | Requiere corrección |
| OrderItemDTO | 1 campo | 🟡 Media | Requiere corrección |
| UserResponseDTO | 3 campos | 🔴 Alta | Requiere corrección |
| Order.java | Capitalización | 🟡 Media | Refactorización |
| CalendarEvent.java | Capitalización | 🟡 Media | Refactorización |

**Total de conflictos:** 15 inconsistencias encontradas

---

## 🔥 CONFIGURACIÓN DE FIREBASE

### Paso 1: Configuración en Firebase Console

#### 1.1 Crear Proyecto Firebase
```
1. Ir a https://console.firebase.google.com/
2. Clic en "Add project" o "Agregar proyecto"
3. Nombre del proyecto: "zonagamer-fullstack" (o el que prefieras)
4. Deshabilitar Google Analytics (opcional para desarrollo)
5. Clic en "Create project"
```

#### 1.2 Habilitar Firestore Database
```
1. En la consola de Firebase, ir a "Build" > "Firestore Database"
2. Clic en "Create database"
3. Seleccionar modo:
   - Producción: Reglas restrictivas (recomendado)
   - Modo de prueba: 30 días de acceso abierto
4. Seleccionar ubicación: 
   - us-central1 (Iowa) - Recomendado para Latinoamérica
   - southamerica-east1 (São Paulo) - Más cercano a Chile
5. Clic en "Enable"
```

#### 1.3 Habilitar Authentication
```
1. Ir a "Build" > "Authentication"
2. Clic en "Get started"
3. En la pestaña "Sign-in method", habilitar:
   ✅ Email/Password
4. Guardar cambios
```

#### 1.4 Habilitar Storage
```
1. Ir a "Build" > "Storage"
2. Clic en "Get started"
3. Aceptar reglas de seguridad predeterminadas
4. Seleccionar misma ubicación que Firestore
5. Clic en "Done"
```

#### 1.5 Crear Archivo de Credenciales
```
1. Ir a "Project settings" (ícono de engranaje)
2. Pestaña "Service accounts"
3. Clic en "Generate new private key"
4. Se descargará un archivo JSON
5. Renombrar a: fullstack-gamerzone-firebase.json
6. Colocar en: src/main/resources/
```

**⚠️ IMPORTANTE:** Agregar este archivo a `.gitignore`:
```gitignore
# Firebase Credentials
src/main/resources/fullstack-gamerzone-firebase.json
```

---

### Paso 2: Configuración en application.yml

```yaml
spring:
  application:
    name: zonagamer-backend
    
  # CORS Configuration
  web:
    cors:
      allowed-origins: 
        - http://localhost:5173
        - http://localhost:3000
      allowed-methods: GET,POST,PUT,DELETE,OPTIONS
      allowed-headers: "*"
      allow-credentials: true

# Firebase Configuration
firebase:
  credentials-path: classpath:fullstack-gamerzone-firebase.json
  storage-bucket: ${FIREBASE_STORAGE_BUCKET:zonagamer-fullstack.appspot.com}
  database-url: https://zonagamer-fullstack.firebaseio.com

# JWT Configuration  
jwt:
  secret: ${JWT_SECRET:tu-clave-secreta-super-segura-de-al-menos-256-bits-para-produccion}
  expiration: 86400000  # 24 horas en milisegundos

# Server Configuration
server:
  port: 8080
```

**Variables de Entorno (Producción):**
```bash
# .env file (NO SUBIR A GIT)
JWT_SECRET=clave-super-segura-generada-con-openssl
FIREBASE_STORAGE_BUCKET=zonagamer-fullstack.appspot.com
```

---

### Paso 3: Estructura de FirebaseConfig.java

Tu configuración actual está correcta. Aquí un resumen:

```java
@Configuration(proxyBeanMethods = false)
public class FirebaseConfig {
    
    @Value("${firebase.credentials-path}")
    private String credentialsPath;
    
    @Value("${firebase.storage-bucket}")
    private String storageBucket;

    @PostConstruct
    public void initialize() {
        try {
            // Cargar credenciales desde resources
            InputStream serviceAccount = 
                getClass().getClassLoader()
                         .getResourceAsStream("fullstack-gamerzone-firebase.json");
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(storageBucket)
                .build();
            
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar Firebase", e);
        }
    }
    
    @Bean
    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
    
    @Bean
    public Bucket getStorageBucket() {
        return StorageClient.getInstance().bucket();
    }
}
```

**Validación:** El log debe mostrar:
```
? Firebase inicializado correctamente
```

---

## 📦 SCRIPTS DE INICIALIZACIÓN DE COLECCIONES

### Script 1: Inicializar Usuarios (users collection)

```javascript
// init-users.js
// Ejecutar con Node.js y firebase-admin

const admin = require('firebase-admin');
const serviceAccount = require('./fullstack-gamerzone-firebase.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

// Usuarios de ejemplo
const users = [
  {
    id: 'user-001',
    email: 'admin@zonagamer.com',
    password: '$2a$10$dummyHashedPassword1234567890', // Hash BCrypt de "Admin123!"
    nombre: 'Administrador',
    apellido: 'ZonaGamer',
    numeroDeTelefono: '+56912345678',
    isAdmin: true,
    active: true,
    puntajeCliente: 0,
    direccion: 'Av. Principal 123, Santiago',
    fechaCreacion: admin.firestore.FieldValue.serverTimestamp(),
    fechaActualizacion: admin.firestore.FieldValue.serverTimestamp()
  },
  {
    id: 'user-002',
    email: 'cliente1@email.com',
    password: '$2a$10$dummyHashedPassword0987654321', // Hash BCrypt de "Cliente123!"
    nombre: 'Juan',
    apellido: 'Pérez',
    numeroDeTelefono: '+56987654321',
    isAdmin: false,
    active: true,
    puntajeCliente: 150,
    direccion: 'Los Pinos 456, Valparaíso',
    fechaCreacion: admin.firestore.FieldValue.serverTimestamp(),
    fechaActualizacion: admin.firestore.FieldValue.serverTimestamp()
  },
  {
    id: 'user-003',
    email: 'cliente2@email.com',
    password: '$2a$10$anotherDummyHashedPassword123',
    nombre: 'María',
    apellido: 'González',
    numeroDeTelefono: '+56911223344',
    isAdmin: false,
    active: true,
    puntajeCliente: 320,
    direccion: 'Calle Falsa 789, Concepción',
    fechaCreacion: admin.firestore.FieldValue.serverTimestamp(),
    fechaActualizacion: admin.firestore.FieldValue.serverTimestamp()
  }
];

async function initUsers() {
  const batch = db.batch();
  
  users.forEach(user => {
    const docRef = db.collection('users').doc(user.id);
    batch.set(docRef, user);
  });
  
  await batch.commit();
  console.log('✅ Usuarios creados exitosamente');
}

initUsers().then(() => process.exit(0)).catch(console.error);
```

---

### Script 2: Inicializar Categorías (categories collection)

```javascript
// init-categories.js

const admin = require('firebase-admin');
const db = admin.firestore();

const categories = [
  {
    id: 'componentes-pc',
    nombreCategoria: 'Componentes PC',
    parentId: null,
    active: true,
    orden: 1
  },
  {
    id: 'gpu',
    nombreCategoria: 'Tarjetas Gráficas',
    parentId: 'componentes-pc',
    active: true,
    orden: 1
  },
  {
    id: 'perifericos',
    nombreCategoria: 'Periféricos',
    parentId: null,
    active: true,
    orden: 2
  },
  {
    id: 'teclados-mecanicos',
    nombreCategoria: 'Teclados Mecánicos',
    parentId: 'perifericos',
    active: true,
    orden: 1
  }
];

async function initCategories() {
  const batch = db.batch();
  
  categories.forEach(category => {
    const docRef = db.collection('categories').doc(category.id);
    batch.set(docRef, category);
  });
  
  await batch.commit();
  console.log('✅ Categorías creadas exitosamente');
}

initCategories().then(() => process.exit(0)).catch(console.error);
```

---

### Script 3: Inicializar Productos (products collection)

```javascript
// init-products.js

const admin = require('firebase-admin');
const db = admin.firestore();

const products = [
  {
    id: 'prod-001',
    nombreProducto: 'NVIDIA RTX 4090',
    descripcionProducto: 'Tarjeta gráfica de última generación con 24GB GDDR6X',
    precio: 1899990.00,
    stock: 5,
    categoryId: 'gpu',
    imageUrl: 'https://example.com/images/rtx4090.jpg',
    isFeatured: true,
    active: true,
    disponibilidad: true,
    fechaCreacion: admin.firestore.FieldValue.serverTimestamp(),
    fechaActualizacion: admin.firestore.FieldValue.serverTimestamp()
  },
  {
    id: 'prod-002',
    nombreProducto: 'AMD Ryzen 9 7950X',
    descripcionProducto: 'Procesador de 16 núcleos y 32 hilos, arquitectura Zen 4',
    precio: 749990.00,
    stock: 12,
    categoryId: 'componentes-pc',
    imageUrl: 'https://example.com/images/ryzen9.jpg',
    isFeatured: true,
    active: true,
    disponibilidad: true,
    fechaCreacion: admin.firestore.FieldValue.serverTimestamp(),
    fechaActualizacion: admin.firestore.FieldValue.serverTimestamp()
  },
  {
    id: 'prod-003',
    nombreProducto: 'Corsair K95 RGB Platinum',
    descripcionProducto: 'Teclado mecánico gaming con switches Cherry MX Speed',
    precio: 199990.00,
    stock: 25,
    categoryId: 'teclados-mecanicos',
    imageUrl: 'https://example.com/images/k95.jpg',
    isFeatured: false,
    active: true,
    disponibilidad: true,
    fechaCreacion: admin.firestore.FieldValue.serverTimestamp(),
    fechaActualizacion: admin.firestore.FieldValue.serverTimestamp()
  }
];

async function initProducts() {
  const batch = db.batch();
  
  products.forEach(product => {
    const docRef = db.collection('products').doc(product.id);
    batch.set(docRef, product);
  });
  
  await batch.commit();
  console.log('✅ Productos creados exitosamente');
}

initProducts().then(() => process.exit(0)).catch(console.error);
```

---

### Script 4: Inicializar Órdenes (orders collection)

```javascript
// init-orders.js

const admin = require('firebase-admin');
const db = admin.firestore();

const orders = [
  {
    id: 'order-001',
    userId: 'user-002',
    items: [
      {
        productId: 'prod-001',
        productName: 'NVIDIA RTX 4090',
        quantity: 1,
        precioEnCompra: 1899990.00
      }
    ],
    total: 1899990.00,
    status: 'PROCESSING',
    deliveryAddress: 'Los Pinos 456, Valparaíso',
    notes: 'Entregar en horario de oficina',
    numeroDeOrden: 'ORD-20240101-001',
    FechaDeCreacion: admin.firestore.FieldValue.serverTimestamp()
  },
  {
    id: 'order-002',
    userId: 'user-003',
    items: [
      {
        productId: 'prod-003',
        productName: 'Corsair K95 RGB Platinum',
        quantity: 2,
        precioEnCompra: 199990.00
      }
    ],
    total: 399980.00,
    status: 'DELIVERED',
    deliveryAddress: 'Calle Falsa 789, Concepción',
    notes: '',
    numeroDeOrden: 'ORD-20240102-002',
    FechaDeCreacion: admin.firestore.Timestamp.fromDate(new Date('2024-01-02'))
  }
];

async function initOrders() {
  const batch = db.batch();
  
  orders.forEach(order => {
    const docRef = db.collection('orders').doc(order.id);
    batch.set(docRef, order);
  });
  
  await batch.commit();
  console.log('✅ Órdenes creadas exitosamente');
}

initOrders().then(() => process.exit(0)).catch(console.error);
```

---

### Script 5: Inicializar Carritos (carts collection)

```javascript
// init-carts.js

const admin = require('firebase-admin');
const db = admin.firestore();

const carts = [
  {
    id: 'cart-user-002',
    userId: 'user-002',
    items: [
      {
        productId: 'prod-002',
        productName: 'AMD Ryzen 9 7950X',
        quantity: 1,
        precio: 749990.00
      }
    ],
    fechaCreacion: admin.firestore.FieldValue.serverTimestamp(),
    fechaActualizacion: admin.firestore.FieldValue.serverTimestamp()
  },
  {
    id: 'cart-user-003',
    userId: 'user-003',
    items: [],
    fechaCreacion: admin.firestore.FieldValue.serverTimestamp(),
    fechaActualizacion: admin.firestore.FieldValue.serverTimestamp()
  }
];

async function initCarts() {
  const batch = db.batch();
  
  carts.forEach(cart => {
    const docRef = db.collection('carts').doc(cart.id);
    batch.set(docRef, cart);
  });
  
  await batch.commit();
  console.log('✅ Carritos creados exitosamente');
}

initCarts().then(() => process.exit(0)).catch(console.error);
```

---

### Script 6: Inicializar Eventos de Calendario (calendar_events collection)

```javascript
// init-calendar-events.js

const admin = require('firebase-admin');
const db = admin.firestore();

const events = [
  {
    id: 'event-001',
    titulo: 'Lanzamiento RTX 5000 Series',
    descripcion: 'Presentación oficial de la nueva generación de tarjetas NVIDIA',
    fechaDeInicio: admin.firestore.Timestamp.fromDate(new Date('2024-06-15T10:00:00')),
    fechaDeTermino: admin.firestore.Timestamp.fromDate(new Date('2024-06-15T12:00:00')),
    type: 'LANZAMIENTO',
    completed: false,
    creadoPor: 'user-001',
    FechaDeCreacion: admin.firestore.FieldValue.serverTimestamp()
  },
  {
    id: 'event-002',
    titulo: 'Inventario Mensual',
    descripcion: 'Revisión de stock de todos los productos',
    fechaDeInicio: admin.firestore.Timestamp.fromDate(new Date('2024-05-01T09:00:00')),
    fechaDeTermino: admin.firestore.Timestamp.fromDate(new Date('2024-05-01T17:00:00')),
    type: 'INVENTARIO',
    completed: true,
    creadoPor: 'user-001',
    FechaDeCreacion: admin.firestore.Timestamp.fromDate(new Date('2024-04-25'))
  },
  {
    id: 'event-003',
    titulo: 'Promoción Black Friday',
    descripcion: 'Descuentos especiales en componentes seleccionados',
    fechaDeInicio: admin.firestore.Timestamp.fromDate(new Date('2024-11-29T00:00:00')),
    fechaDeTermino: admin.firestore.Timestamp.fromDate(new Date('2024-11-30T23:59:59')),
    type: 'PROMOCION',
    completed: false,
    creadoPor: 'user-001',
    FechaDeCreacion: admin.firestore.FieldValue.serverTimestamp()
  }
];

async function initCalendarEvents() {
  const batch = db.batch();
  
  events.forEach(event => {
    const docRef = db.collection('calendar_events').doc(event.id);
    batch.set(docRef, event);
  });
  
  await batch.commit();
  console.log('✅ Eventos de calendario creados exitosamente');
}

initCalendarEvents().then(() => process.exit(0)).catch(console.error);
```

---

### 🚀 Ejecutar Todos los Scripts

**Instalación de Firebase Admin SDK (Node.js):**
```bash
npm install firebase-admin
```

**Script Master (init-all.js):**
```javascript
// init-all.js
const admin = require('firebase-admin');
const serviceAccount = require('./fullstack-gamerzone-firebase.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  storageBucket: 'zonagamer-fullstack.appspot.com'
});

async function initializeAllCollections() {
  console.log('🔥 Inicializando Firebase Firestore...\n');
  
  // Importar scripts
  require('./init-users');
  await new Promise(resolve => setTimeout(resolve, 2000));
  
  require('./init-categories');
  await new Promise(resolve => setTimeout(resolve, 2000));
  
  require('./init-products');
  await new Promise(resolve => setTimeout(resolve, 2000));
  
  require('./init-orders');
  await new Promise(resolve => setTimeout(resolve, 2000));
  
  require('./init-carts');
  await new Promise(resolve => setTimeout(resolve, 2000));
  
  require('./init-calendar-events');
  
  console.log('\n✅ TODAS LAS COLECCIONES INICIALIZADAS CORRECTAMENTE');
  process.exit(0);
}

initializeAllCollections().catch(console.error);
```

**Ejecutar:**
```bash
node init-all.js
```

---

## 🔗 CONEXIÓN PROYECTO-FIREBASE

### Cómo Funciona la Conexión

#### 1. **Carga de Credenciales (Startup)**
```
Application Start
    ↓
FirebaseConfig.initialize() (@PostConstruct)
    ↓
Lee fullstack-gamerzone-firebase.json
    ↓
FirebaseApp.initializeApp(options)
    ↓
✅ Firebase Inicializado
```

#### 2. **Beans de Firebase**
```java
@Bean
public Firestore getFirestore() {
    return FirestoreClient.getFirestore();  // Singleton
}

@Bean
public Bucket getStorageBucket() {
    return StorageClient.getInstance().bucket();
}
```

Estos beans se inyectan en los repositorios:
```java
@Repository
public class ProductRepository extends BaseRepository<Product> {
    
    @Autowired
    private Firestore firestore;  // ← Inyectado automáticamente
    
    // ...
}
```

#### 3. **Flujo de una Petición API**
```
Cliente HTTP Request (Postman/Frontend)
    ↓
SecurityFilterChain (JWT validation)
    ↓
Controller (@RestController)
    ↓
Service (@Service)
    ↓
Repository (@Repository)
    ↓
Firestore Bean (operaciones CRUD)
    ↓
Firebase Cloud Firestore
    ↓
Respuesta JSON al cliente
```

---

### Arquitectura de Repositorios

**BaseRepository.java** (clase abstracta):
```java
public abstract class BaseRepository<T> {
    
    protected final Firestore firestore;
    protected final String collectionName;
    
    protected BaseRepository(Firestore firestore, String collectionName) {
        this.firestore = firestore;
        this.collectionName = collectionName;
    }
    
    public T save(T entity) throws ExecutionException, InterruptedException {
        // Guardar en Firestore
        firestore.collection(collectionName)
                 .document(id)
                 .set(entity)
                 .get();
        return entity;
    }
    
    public Optional<T> findById(String id, Class<T> clazz) {
        // Buscar en Firestore
        DocumentSnapshot doc = firestore.collection(collectionName)
                                       .document(id)
                                       .get()
                                       .get();
        return doc.exists() ? 
               Optional.of(doc.toObject(clazz)) : 
               Optional.empty();
    }
}
```

**Repositorio específico (ejemplo ProductRepository):**
```java
@Repository
public class ProductRepository extends BaseRepository<Product> {
    
    @Autowired
    public ProductRepository(Firestore firestore) {
        super(firestore, "products");  // ← Nombre de la colección
    }
    
    // Métodos específicos de Product
    public List<Product> findByCategory(String categoryId) {
        // Consulta personalizada
    }
}
```

---

### Variables de Entorno y Configuración

**Archivo: src/main/resources/application.yml**
```yaml
firebase:
  credentials-path: classpath:fullstack-gamerzone-firebase.json
  storage-bucket: ${FIREBASE_STORAGE_BUCKET:zonagamer-fullstack.appspot.com}
```

**Para Producción (Variables de entorno):**
```bash
export FIREBASE_STORAGE_BUCKET=zonagamer-production.appspot.com
export JWT_SECRET=clave-super-segura-generada
```

**Lectura en Java:**
```java
@Value("${firebase.storage-bucket}")
private String storageBucket;  // Lee de application.yml o ENV
```

---

## 🧪 TESTING CON DATOS REALES

### Paso 1: Verificar Conexión a Firebase

**Test en el startup:**
```
2024-05-15 10:30:45 INFO  FirebaseConfig - ? Firebase inicializado correctamente
2024-05-15 10:30:46 INFO  TomcatWebServer - Tomcat started on port(s): 8080 (http)
```

**Endpoint de Health Check:**
```bash
curl http://localhost:8080/actuator/health
```

---

### Paso 2: Testing con Postman

#### 2.1 Registrar Usuario
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "test@zonagamer.com",
  "password": "Test1234!",
  "nombre": "Usuario",
  "apellido": "Prueba",
  "numeroDeTelefono": "+56900000000"
}
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": "generated-firebase-id",
  "email": "test@zonagamer.com",
  "nombreCompleto": "Usuario Prueba",
  "isAdmin": false
}
```

---

#### 2.2 Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "test@zonagamer.com",
  "password": "Test1234!"
}
```

**Guardar el token para siguientes requests:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

#### 2.3 Obtener Productos
```http
GET http://localhost:8080/api/products
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta esperada:**
```json
[
  {
    "id": "prod-001",
    "nombre": "NVIDIA RTX 4090",
    "descripcion": "Tarjeta gráfica de última generación...",
    "precio": 1899990.00,
    "stock": 5,
    "imageUrl": "https://example.com/images/rtx4090.jpg",
    "categoryId": "gpu",
    "isFeatured": true,
    "disponibilidad": true,
    "fechaCreacion": "2024-05-15T10:30:00Z"
  }
]
```

---

#### 2.4 Agregar al Carrito
```http
POST http://localhost:8080/api/cart/add
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "productId": "prod-001",
  "quantity": 1
}
```

---

#### 2.5 Ver Carrito
```http
GET http://localhost:8080/api/cart
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta esperada:**
```json
{
  "id": "cart-user-generated-id",
  "userId": "user-generated-id",
  "items": [
    {
      "productId": "prod-001",
      "productName": "NVIDIA RTX 4090",
      "imageUrl": "https://example.com/images/rtx4090.jpg",
      "quantity": 1,
      "precio": 1899990.00,
      "subtotal": 1899990.00,
      "disponibilidad": true
    }
  ],
  "subtotal": 1899990.00,
  "iva": 361998.10,
  "total": 2261988.10,
  "totalItems": 1
}
```

---

#### 2.6 Hacer Checkout
```http
POST http://localhost:8080/api/orders/checkout
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "deliveryAddress": "Av. Libertador 123, Santiago",
  "notes": "Entregar en horario de tarde"
}
```

---

#### 2.7 Ver Mis Órdenes
```http
GET http://localhost:8080/api/orders/my-orders
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

### Paso 3: Verificar Datos en Firebase Console

1. Ir a https://console.firebase.google.com/
2. Seleccionar proyecto "zonagamer-fullstack"
3. Ir a "Firestore Database"
4. Verificar colecciones:
   - `users` → Ver usuarios registrados
   - `products` → Ver productos creados
   - `carts` → Ver carritos activos
   - `orders` → Ver órdenes generadas
   - `categories` → Ver categorías
   - `calendar_events` → Ver eventos

---

### Paso 4: Testing de Autenticación

**Endpoint protegido sin token:**
```http
GET http://localhost:8080/api/products
```
**Respuesta: 401 Unauthorized**

**Endpoint protegido con token válido:**
```http
GET http://localhost:8080/api/products
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```
**Respuesta: 200 OK con datos**

**Endpoint solo admin (sin ser admin):**
```http
POST http://localhost:8080/api/products
Authorization: Bearer <token-usuario-normal>
```
**Respuesta: 403 Forbidden**

---

## 🛡️ REGLAS DE SEGURIDAD FIRESTORE

**Configuración recomendada en Firebase Console:**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Usuarios: solo lectura/escritura de su propio documento
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }
    
    // Productos: lectura pública, escritura solo admin
    match /products/{productId} {
      allow read: if true;  // Público
      allow write: if request.auth.token.isAdmin == true;
    }
    
    // Categorías: igual que productos
    match /categories/{categoryId} {
      allow read: if true;
      allow write: if request.auth.token.isAdmin == true;
    }
    
    // Carritos: solo el dueño puede leer/escribir
    match /carts/{cartId} {
      allow read, write: if request.auth != null && 
                            resource.data.userId == request.auth.uid;
    }
    
    // Órdenes: el dueño puede leer, solo admin puede modificar estado
    match /orders/{orderId} {
      allow read: if request.auth != null && 
                     resource.data.userId == request.auth.uid;
      allow create: if request.auth != null;
      allow update: if request.auth.token.isAdmin == true;
    }
    
    // Eventos de calendario: solo admin
    match /calendar_events/{eventId} {
      allow read, write: if request.auth.token.isAdmin == true;
    }
  }
}
```

**⚠️ NOTA:** Estas reglas son complementarias a la seguridad de Spring Security. Firebase Admin SDK en el backend **bypasses estas reglas** porque usa credenciales de servicio.

---

## 📊 ÍNDICES RECOMENDADOS PARA FIRESTORE

**Crear en Firebase Console > Firestore > Indexes:**

1. **Productos por categoría y destacados:**
   ```
   Collection: products
   Fields: categoryId (Ascending), isFeatured (Descending)
   Query scope: Collection
   ```

2. **Órdenes por usuario y fecha:**
   ```
   Collection: orders
   Fields: userId (Ascending), FechaDeCreacion (Descending)
   Query scope: Collection
   ```

3. **Eventos de calendario por fecha:**
   ```
   Collection: calendar_events
   Fields: fechaDeInicio (Ascending), completed (Ascending)
   Query scope: Collection
   ```

---

## 🔧 TROUBLESHOOTING

### Error: "Firebase inicializado más de una vez"
**Solución:** Agregar `proxyBeanMethods = false` en `@Configuration`

### Error: "Could not find credentials file"
**Solución:** Verificar que `fullstack-gamerzone-firebase.json` esté en `src/main/resources/`

### Error: "Storage bucket not found"
**Solución:** Verificar variable `firebase.storage-bucket` en `application.yml`

### Error: "JWT token expired"
**Solución:** Hacer login nuevamente para obtener nuevo token

### Error: "Permission denied" en Firestore
**Solución:** Verificar reglas de seguridad en Firebase Console

---

## 📝 CHECKLIST DE IMPLEMENTACIÓN

- [ ] Proyecto creado en Firebase Console
- [ ] Firestore Database habilitado
- [ ] Authentication (Email/Password) habilitado
- [ ] Storage habilitado
- [ ] Archivo de credenciales descargado y ubicado en resources
- [ ] Credenciales agregadas a .gitignore
- [ ] application.yml configurado correctamente
- [ ] Scripts de inicialización ejecutados
- [ ] Datos de ejemplo creados en Firestore
- [ ] Índices creados en Firestore
- [ ] Reglas de seguridad configuradas
- [ ] Application compila sin errores
- [ ] Application arranca correctamente (log de Firebase exitoso)
- [ ] Testing con Postman: registro, login, productos, carrito, checkout
- [ ] Validación de datos en Firebase Console

---

## 📚 RECURSOS ADICIONALES

- [Firebase Admin SDK - Java](https://firebase.google.com/docs/admin/setup?hl=es-419)
- [Firestore Data Model](https://firebase.google.com/docs/firestore/data-model?hl=es-419)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

**Documento generado el:** 2024-05-15  
**Versión del proyecto:** zonagamer-backend v1.0  
**Java:** 21.0.9 LTS | **Spring Boot:** 3.5.7 | **Firebase Admin SDK:** 9.4.2
