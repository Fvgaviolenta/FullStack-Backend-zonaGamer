package com.zonagamer.zonagamer_backend.repository;


import com.google.cloud.firestore.Firestore;
import com.zonagamer.zonagamer_backend.model.Cart;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class CartRepository extends BaseRepository<Cart> {
    
    public CartRepository(Firestore firestore){
        super(firestore);
    }

    @Override
    protected String getCollectionName(){
        return "carts";
    }

    @Override
    protected Class<Cart> getEntityClass(){
        return Cart.class;
    }

    public Optional<Cart> findByUserId(String userId) throws ExecutionException, InterruptedException{
        var querySnapshot = firestore.collection(getCollectionName())
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .get();

        if (querySnapshot.isEmpty()) {
            return Optional.empty();
        }

        // ✅ CORRECCIÓN: Obtener el documento y asignar el ID
        var document = querySnapshot.getDocuments().get(0);
        Cart cart = document.toObject(Cart.class);
        
        if (cart != null) {
            cart.setId(document.getId());  // ← CRÍTICO: Asignar document ID
        }
        
        return Optional.of(cart);
    }
}
