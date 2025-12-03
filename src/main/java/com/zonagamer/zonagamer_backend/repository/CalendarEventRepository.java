package com.zonagamer.zonagamer_backend.repository;


import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.zonagamer.zonagamer_backend.model.CalendarEvent;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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

    public List<CalendarEvent> findPeding() throws ExecutionException, InterruptedException {
        return firestore.collection(getCollectionName())
            .whereEqualTo("completed", false)
            .orderBy("diaDeInicio", Query.Direction.ASCENDING)
            .get()
            .get()
            .toObjects(CalendarEvent.class);
    }

    public List<CalendarEvent> findByDateRage(LocalDateTime comienzo, LocalDateTime fin) throws ExecutionException, InterruptedException {
        return firestore.collection(getCollectionName())
            .whereGreaterThan("diaDeInicio", comienzo)
            .whereLessThanOrEqualTo("diaDeInicio", fin)
            .orderBy("diaDeInicio", Query.Direction.ASCENDING)
            .get()
            .get()
            .toObjects(CalendarEvent.class);
    }
}
