package com.zonagamer.zonagamer_backend.repository;

import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.google.cloud.firestore.Firestore;
import com.zonagamer.zonagamer_backend.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Repository
public class CategoryRepository extends BaseRepository<Category>{
    

    public CategoryRepository(Firestore firestore){
        super(firestore);
    }

    @Override
    protected String getCollectionName(){
        return "categories";
    }

    @Override
    protected Class<Category> getEntityClass(){
        return Category.class;
    }

    public List<Category> findRootCategories() throws ExecutionException, InterruptedException {
        return findAll().stream()
            .filter(Category::isRoot)
            .collect(Collectors.toList());
    }

    public List<Category> findByParentId(String parentId) throws ExecutionException, InterruptedException {
        return firestore.collection(getCollectionName())
            .whereEqualTo("parentId", parentId)
            .whereEqualTo("active", true)
            .get()
            .get()
            .toObjects(Category.class);
    }
}
