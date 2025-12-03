package com.zonagamer.zonagamer_backend.repository;

import com.zonagamer.zonagamer_backend.model.User;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
}
