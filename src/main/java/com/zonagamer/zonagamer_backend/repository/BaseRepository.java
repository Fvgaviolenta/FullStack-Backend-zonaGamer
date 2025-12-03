package com.zonagamer.zonagamer_backend.repository;

import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
/**
 * Repositorio base genérico para operaciones CRUD en Firestore
 * 
 * T = Tipo de entidad (User, Product, etc.)
 * 
 * Métodos disponibles:
 * - save(T entity): Guarda una entidad
 * - findById(String id): Busca por ID
 * - findAll(): Obtiene todas las entidades
 * - update(String id, T entity): Actualiza una entidad
 * - delete(String id): Elimina una entidad
 */

@Slf4j
@Repository
@RequiredArgsConstructor
public abstract class BaseRepository<T> {

    protected final Firestore firestore;
    
    protected abstract String getCollectionName();

    protected abstract Class<T> getEntityClass();

    public String save(T entity) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();

        log.debug("Guardado {} con ID: {}", getEntityClass().getSimpleName(), id);

        firestore.collection(getCollectionName())
            .document(id)
            .set(entity)
            .get();

        log.info("{} guardado exitosamente: {}", getEntityClass().getSimpleName(), id);

        return id;
    }

    public Optional<T> findById(String id) throws ExecutionException, InterruptedException {
        log.debug("Buscando {} con ID: {}", getEntityClass().getSimpleName(), id);

        var doc = firestore.collection(getCollectionName())
            .document(id)
            .get()
            .get();

        if (!doc.exists()) {
            log.debug("{} no encontrado: {}", getEntityClass().getSimpleName(), id);
            return Optional.empty();
        }

        T entity = doc.toObject(getEntityClass());
        log.debug("{} encontrado: {}", getEntityClass().getSimpleName());
        return Optional.of(entity);
    }

    public List<T> findAll() throws ExecutionException, InterruptedException {
        log.debug("Obteniendo a todos los {}", getEntityClass().getSimpleName());

        var querySnapshot = firestore.collection(getCollectionName())
            .get()
            .get();

        List<T> entities = querySnapshot.getDocuments().stream()
            .map(doc -> doc.toObject(getEntityClass()))
            .collect(Collectors.toList());

        log.info("Obtenidos {} registros de {}", entities.size(), getEntityClass().getSimpleName());

        return entities;
    }

    public void update(String id, T entity) throws ExecutionException, InterruptedException {
        log.debug("Actualizando {} con ID: {}", getEntityClass().getSimpleName(), id);

        firestore.collection(getCollectionName())
            .document(id)
            .set(entity)
            .get();
        log.info("{} actualizado: {}", getEntityClass().getSimpleName(), id);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        log.debug("Eliminando {} con ID: {}", getEntityClass().getSimpleName(), id);

        firestore.collection(getCollectionName())
            .document(id)
            .delete()
            .get();

        log.info("{} eliminado: {}", getEntityClass().getSimpleName(), id);
    }

    public long count() throws ExecutionException, InterruptedException {
        var querySnapshot = firestore.collection(getCollectionName())
            .get()
            .get();
        
        return querySnapshot.size();
    }
}
