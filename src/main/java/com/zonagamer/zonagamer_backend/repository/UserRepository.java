package com.zonagamer.zonagamer_backend.repository;

import com.zonagamer.zonagamer_backend.model.User;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.FieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import com.google.cloud.firestore.Firestore;

@Repository
public class UserRepository extends BaseRepository<User>{

    public UserRepository(Firestore firestore){
        super(firestore);
    }

    @Override
    protected String getCollectionName(){
        return "users";
    }

    @Override
    protected Class<User> getEntityClass(){
        return User.class;
    }

    public Optional<User> findByEmail(String email) throws ExecutionException, InterruptedException {
        var querySnapshot = firestore.collection(getCollectionName())
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .get();

        if (querySnapshot.isEmpty()) {
            return Optional.empty();
        }

        var document = querySnapshot.getDocuments().get(0);
        User user = document.toObject(User.class);
        
        // CRÍTICO: Asignar el document ID al campo id del objeto User
        if (user != null) {
            user.setId(document.getId());
        }
        
        return Optional.ofNullable(user);
    }

    public boolean existsByEmail(String email) throws ExecutionException, InterruptedException {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<User> findAll() throws ExecutionException, InterruptedException {
        var querySnapshot = firestore.collection(getCollectionName())
            .get()
            .get();
        
        return querySnapshot.getDocuments().stream()
            .map(doc -> {
                try {
                    // Convertir manualmente para evitar errores con timestamps antiguos
                    User user = User.builder()
                        .id(doc.getId())
                        .email(doc.getString("email"))
                        .password(doc.getString("password"))
                        .nombre(doc.getString("nombre"))
                        .apellido(doc.getString("apellido"))
                        .numeroDeTelefono(doc.getString("numeroDeTelefono"))
                        .admin(Boolean.TRUE.equals(doc.getBoolean("admin")))
                        .active(Boolean.TRUE.equals(doc.getBoolean("active")))
                        .puntajeCliente(doc.getLong("puntajeCliente") != null ? 
                            doc.getLong("puntajeCliente").intValue() : 0)
                        .fechaCreacion(doc.getTimestamp("fechaCreacion"))
                        .fechaActualizacion(doc.getTimestamp("fechaActualizacion"))
                        .build();
                    
                    return user;
                } catch (Exception e) {
                    // Si falla la conversión manual, intentar con toObject como fallback
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        user.setId(doc.getId());
                    }
                    return user;
                }
            })
            .collect(Collectors.toList());
    }

    @Override
    public String save(User entity) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        
        // Crear Map para garantizar que los timestamps se generen automáticamente
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", entity.getEmail());
        userData.put("password", entity.getPassword());
        userData.put("nombre", entity.getNombre());
        userData.put("apellido", entity.getApellido());
        userData.put("numeroDeTelefono", entity.getNumeroDeTelefono());
        userData.put("admin", entity.isAdmin());
        userData.put("active", entity.isActive());
        userData.put("puntajeCliente", entity.getPuntajeCliente() != null ? entity.getPuntajeCliente() : 0);
        
        // CRÍTICO: Usar FieldValue.serverTimestamp() para timestamps automáticos
        userData.put("fechaCreacion", FieldValue.serverTimestamp());
        userData.put("fechaActualizacion", FieldValue.serverTimestamp());
        
        firestore.collection(getCollectionName())
            .document(id)
            .set(userData)
            .get();
        
        return id;
    }

    @Override
    public void update(String id, User entity) throws ExecutionException, InterruptedException {
        // Crear Map para actualización
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", entity.getEmail());
        userData.put("password", entity.getPassword());
        userData.put("nombre", entity.getNombre());
        userData.put("apellido", entity.getApellido());
        userData.put("numeroDeTelefono", entity.getNumeroDeTelefono());
        userData.put("admin", entity.isAdmin());
        userData.put("active", entity.isActive());
        userData.put("puntajeCliente", entity.getPuntajeCliente() != null ? entity.getPuntajeCliente() : 0);
        
        // CRÍTICO: Actualizar solo fechaActualizacion automáticamente
        userData.put("fechaActualizacion", FieldValue.serverTimestamp());
        // NO actualizar fechaCreacion para preservar la fecha original
        
        firestore.collection(getCollectionName())
            .document(id)
            .update(userData)
            .get();
    }
}
