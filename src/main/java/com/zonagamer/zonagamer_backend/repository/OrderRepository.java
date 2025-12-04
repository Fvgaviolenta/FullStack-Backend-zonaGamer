package com.zonagamer.zonagamer_backend.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.zonagamer.zonagamer_backend.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class OrderRepository extends BaseRepository<Order>{
    
    public OrderRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    protected String getCollectionName() {
        return "orders";
    }

    @Override
    protected Class<Order> getEntityClass(){
        return Order.class;
    }

    public List<Order> findByUserId(String userId) throws ExecutionException, InterruptedException{
        var querySnapshot = firestore.collection(getCollectionName())
            .whereEqualTo("userId", userId)
            .orderBy("fechaDeCreacion", Query.Direction.DESCENDING)
            .get()
            .get();
        
        return querySnapshot.getDocuments().stream()
            .map(doc -> {
                Order order = doc.toObject(Order.class);
                if (order != null) {
                    order.setId(doc.getId());  // CRÍTICO: Asignar document ID
                }
                return order;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Order> findByStatus(Order.OrderStatus status) throws ExecutionException, InterruptedException{
        var querySnapshot = firestore.collection(getCollectionName())
            .whereEqualTo("status", status.name())
            .get()
            .get();
        
        return querySnapshot.getDocuments().stream()
            .map(doc -> {
                Order order = doc.toObject(Order.class);
                if (order != null) {
                    order.setId(doc.getId());  // CRÍTICO: Asignar document ID
                }
                return order;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
}
