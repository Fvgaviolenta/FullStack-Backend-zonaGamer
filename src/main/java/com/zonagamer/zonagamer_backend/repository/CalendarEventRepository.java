package com.zonagamer.zonagamer_backend.repository;


import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Query;
import com.google.cloud.Timestamp;
import com.zonagamer.zonagamer_backend.model.CalendarEvent;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
public class CalendarEventRepository extends BaseRepository<CalendarEvent> {
    

    public CalendarEventRepository(Firestore firestore){
        super(firestore);
    }

    @Override
    protected String getCollectionName() {
        return "calendar_events";
    }

    @Override
    protected Class<CalendarEvent> getEntityClass(){
        return CalendarEvent.class;
    }

    @Override
    public String save(CalendarEvent entity) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        
        // Crear Map para garantizar timestamps automáticos
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("titulo", entity.getTitulo());
        eventData.put("descripcion", entity.getDescripcion());
        eventData.put("fechaDeInicio", entity.getFechaDeInicio());
        eventData.put("fechaDeTermino", entity.getFechaDeTermino());
        eventData.put("type", entity.getType().name());
        eventData.put("completed", entity.isCompleted());
        eventData.put("creadoPor", entity.getCreadoPor());
        
        // ✅ Timestamp automático para fechaDeCreacion
        eventData.put("fechaDeCreacion", FieldValue.serverTimestamp());
        
        firestore.collection(getCollectionName())
            .document(id)
            .set(eventData)
            .get();
        
        return id;
    }

    @Override
    public void update(String id, CalendarEvent entity) throws ExecutionException, InterruptedException {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("titulo", entity.getTitulo());
        eventData.put("descripcion", entity.getDescripcion());
        eventData.put("fechaDeInicio", entity.getFechaDeInicio());
        eventData.put("fechaDeTermino", entity.getFechaDeTermino());
        eventData.put("type", entity.getType().name());
        eventData.put("completed", entity.isCompleted());
        // NO actualizar creadoPor ni fechaDeCreacion
        
        firestore.collection(getCollectionName())
            .document(id)
            .update(eventData)
            .get();
    }

    public List<CalendarEvent> findPending() throws ExecutionException, InterruptedException {
        var querySnapshot = firestore.collection(getCollectionName())
            .whereEqualTo("completed", false)
            .orderBy("fechaDeInicio", Query.Direction.ASCENDING)
            .get()
            .get();
        
        return querySnapshot.getDocuments().stream()
            .map(doc -> {
                CalendarEvent event = doc.toObject(CalendarEvent.class);
                if (event != null) {
                    event.setId(doc.getId());
                }
                return event;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    public List<CalendarEvent> findByDateRange(Timestamp start, Timestamp end) 
            throws ExecutionException, InterruptedException {
        var querySnapshot = firestore.collection(getCollectionName())
            .whereGreaterThanOrEqualTo("fechaDeInicio", start)
            .whereLessThanOrEqualTo("fechaDeInicio", end)
            .orderBy("fechaDeInicio", Query.Direction.ASCENDING)
            .get()
            .get();
        
        return querySnapshot.getDocuments().stream()
            .map(doc -> {
                CalendarEvent event = doc.toObject(CalendarEvent.class);
                if (event != null) {
                    event.setId(doc.getId());
                }
                return event;
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
