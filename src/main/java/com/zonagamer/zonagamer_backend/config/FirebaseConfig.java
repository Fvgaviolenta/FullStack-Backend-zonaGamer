package com.zonagamer.zonagamer_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuración de Firebase para el proyecto ZonaGamer
 * 
 * Esta clase inicializa la conexión con Firebase al arrancar la aplicación.
 * Configura tanto Firestore (base de datos) como Cloud Storage (imágenes).
 */
@Slf4j  // Lombok: genera automáticamente el logger
@Configuration(proxyBeanMethods = false)  // Le dice a Spring que esta clase contiene configuración
public class FirebaseConfig {

    // Inyecta valores desde application.yml
    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @Value("${firebase.storage-bucket}")
    private String storageBucket;

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.database-url}")
    private String databaseUrl;

    private GoogleCredentials googleCredentials;

    /**
     * Inicializa Firebase cuando Spring Boot arranca
     * 
     *: Se ejecuta automáticamente después de crear el bean
     */
    @PostConstruct
    public void initialize() {
        try {
            // Remover "classpath:" del path si está presente
            String cleanPath = credentialsPath.replace("classpath:", "");
            
            // Asegurar que empiece con /
            if (!cleanPath.startsWith("/")) {
                cleanPath = "/" + cleanPath;
            }
            
            // Carga el archivo de credenciales desde resources/
            InputStream serviceAccountStream = getClass()
                .getResourceAsStream(cleanPath);

            if (serviceAccountStream == null) {
                throw new RuntimeException(
                    "No se encontro el archivo de credeneciales de Firebase en: " + cleanPath + ". Porfavor, asegurese de que el nombre del archivo en 'application.yml' coincida y este en 'src/main/resources'."
                );
            }

            googleCredentials = GoogleCredentials.fromStream(serviceAccountStream);
            serviceAccountStream.close();

            // Construye las opciones de Firebase
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .setStorageBucket(storageBucket)
                .setProjectId(projectId)
                .setDatabaseUrl(databaseUrl)
                .build();

            // Inicializa Firebase solo si no está ya inicializado
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase inicializado correctamente");
                log.info("📦 Project ID: {}", projectId);
                log.info("🗄️ Firestore Database: Conectado");
                log.info("☁️ Storage Bucket: {}", storageBucket);
            } else {
                log.info("ℹ️ Firebase Admin SDK ya estaba inicializado. No se realizó una nueva inicialización.");
            }

        } catch (IOException e) {
            log.error("❌ Error al inicializar Firebase: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo inicializar Firebase", e);
        } catch (RuntimeException e) {
            log.error("❌ Error de configuración al inicializar Firebase: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Bean de Firestore para inyectar en repositorios
     * 
     * return Instancia de Firestore para hacer consultas
     */
    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    /**
     * Bean de Storage para subir imágenes
     * 
     * Instancia de Storage para manejar archivos
     */
    @Bean
    public Storage storage() {
        if (googleCredentials == null) {
            log.error("❌ Las credenciales de Google no se han inicializado. Fallo al crear el bean de Storage.");
            throw new IllegalStateException("Las credenciales de Google no están disponibles para el bean de Storage.");
        }
        return StorageOptions.newBuilder()
            .setCredentials(googleCredentials)
            .setProjectId(FirebaseApp.getInstance().getOptions().getProjectId())
            .build()
            .getService();
    }
}