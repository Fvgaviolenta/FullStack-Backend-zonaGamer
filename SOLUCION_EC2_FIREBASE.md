# 🔧 Solución: Error Firebase en EC2

## 🔍 Problema Identificado

```
UNAUTHENTICATED: Failed computing credential metadata
{"error":"invalid_grant","error_description":"Invalid JWT Signature."}
```

**Causa:** El archivo `fullstack-gamerzone-firebase.json` en EC2 tiene un problema con la firma JWT.

---

## ✅ Solución Paso a Paso

### 1. Verificar el archivo Firebase en EC2

**Conecta a tu EC2 y ejecuta:**

```bash
# Ver contenido del archivo
cat /ruta/a/fullstack-gamerzone-firebase.json | head -20

# Verificar permisos
ls -la /ruta/a/fullstack-gamerzone-firebase.json

# Debería mostrar algo como: -rw-r--r-- ec2-user ec2-user
```

**Problemas comunes:**
- ❌ Saltos de línea `\n` mal formateados en `private_key`
- ❌ Archivo copiado con codificación incorrecta
- ❌ Espacios extra o caracteres invisibles

---

### 2. Subir el archivo correctamente a EC2

**Opción A: Usar SCP (desde tu máquina local)**

```bash
# Desde tu carpeta local del proyecto
scp -i "tu-llave.pem" \
  src/main/resources/fullstack-gamerzone-firebase.json \
  ec2-user@tu-ec2-ip:/home/ec2-user/fullstack-gamerzone-firebase.json
```

**Opción B: Copiar manualmente con nano/vim**

```bash
# En EC2
nano /home/ec2-user/fullstack-gamerzone-firebase.json

# Pega EXACTAMENTE el contenido del archivo
# Guarda con: Ctrl+X -> Y -> Enter
```

⚠️ **IMPORTANTE:** Verifica que la `private_key` mantenga los saltos de línea `\n`:

```json
{
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgk...\n-----END PRIVATE KEY-----\n"
}
```

---

### 3. Configurar `application.properties` en EC2

**Crea/edita el archivo de configuración:**

```bash
# En tu EC2, en la carpeta donde está el JAR
nano application.properties
```

**Contenido:**

```properties
# Server Configuration
server.port=8080

# Firebase Configuration
firebase.credentials-path=/home/ec2-user/fullstack-gamerzone-firebase.json
firebase.storage-bucket=zonagamer-fullstack.appspot.com
firebase.project-id=zonagamer-fullstack
firebase.database-url=https://zonagamer-fullstack.firebaseio.com

# JWT Configuration (usa el mismo que en local)
jwt.secret=TU_SECRET_AQUI
jwt.expiration=86400000

# Logging
logging.level.com.zonagamer=INFO
logging.level.org.springframework.security=DEBUG
```

**Guarda con:** `Ctrl+X` → `Y` → `Enter`

---

### 4. Ejecutar la aplicación en EC2

**Con el `application.properties` en la misma carpeta del JAR:**

```bash
# Opción 1: Con properties en el mismo directorio
java -jar zonagamer-backend-0.0.1-SNAPSHOT.jar

# Opción 2: Especificar ubicación del properties
java -jar zonagamer-backend-0.0.1-SNAPSHOT.jar \
  --spring.config.location=file:./application.properties

# Opción 3: Variables de entorno (MÁS SEGURO)
export FIREBASE_CREDENTIALS_PATH=/home/ec2-user/fullstack-gamerzone-firebase.json
export JWT_SECRET=tu_secret_aqui
java -jar zonagamer-backend-0.0.1-SNAPSHOT.jar
```

---

### 5. Verificar que funciona

**Prueba el health endpoint:**

```bash
curl http://localhost:8080/api/health
```

**Debería responder:**

```json
{
  "status": "UP",
  "service": "ZonaGamer Backend",
  "timestamp": "1733289600000"
}
```

**Prueba el login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@zonagamer.com",
    "password": "tu_password"
  }'
```

---

## 🔐 Alternativa: Usar Variables de Entorno (RECOMENDADO)

En lugar de tener el archivo JSON en el servidor, usa variables de entorno:

**1. Modifica `FirebaseConfig.java`:**

```java
@PostConstruct
public void initialize() {
    try {
        // Opción 1: Desde archivo
        if (credentialsPath != null && !credentialsPath.isEmpty()) {
            InputStream serviceAccount = new FileInputStream(credentialsPath);
            googleCredentials = GoogleCredentials.fromStream(serviceAccount);
        } 
        // Opción 2: Desde variable de entorno GOOGLE_APPLICATION_CREDENTIALS
        else {
            googleCredentials = GoogleCredentials.getApplicationDefault();
        }
        
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(googleCredentials)
            .setStorageBucket(storageBucket)
            .setProjectId(projectId)
            .setDatabaseUrl(databaseUrl)
            .build();
            
        // Resto del código...
    }
}
```

**2. En EC2, configura:**

```bash
# Exportar variable de entorno
export GOOGLE_APPLICATION_CREDENTIALS=/home/ec2-user/fullstack-gamerzone-firebase.json

# Ejecutar aplicación
java -jar zonagamer-backend-0.0.1-SNAPSHOT.jar
```

---

## 🚨 Si el problema persiste

**Regenera las credenciales de Firebase:**

1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Proyecto: `zonagamer-fullstack`
3. ⚙️ Project Settings → Service Accounts
4. Click en **"Generate new private key"**
5. Descarga el nuevo JSON
6. Reemplaza `fullstack-gamerzone-firebase.json` en EC2
7. Reinicia la aplicación

---

## 📝 Checklist Final

- [ ] Archivo Firebase copiado correctamente en EC2
- [ ] `application.properties` configurado con ruta correcta
- [ ] Permisos del archivo JSON: `-rw-r--r--`
- [ ] Variable `GOOGLE_APPLICATION_CREDENTIALS` exportada (si aplica)
- [ ] Aplicación reiniciada después de cambios
- [ ] Health endpoint responde correctamente
- [ ] Login endpoint funciona sin errores UNAUTHENTICATED

---

## 🔗 Recursos

- [Firebase Admin SDK Setup](https://firebase.google.com/docs/admin/setup)
- [Google Application Default Credentials](https://cloud.google.com/docs/authentication/provide-credentials-adc)
