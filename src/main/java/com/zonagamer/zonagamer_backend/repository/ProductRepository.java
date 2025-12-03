package com.zonagamer.zonagamer_backend.repository;

import com.google.cloud.firestore.Firestore;
import com.zonagamer.zonagamer_backend.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Repository
public class ProductRepository extends BaseRepository<Product>{
    public ProductRepository(Firestore firestore){
        super(firestore);
    }

    @Override
    protected String getCollectionName(){
        return "products";
    }

    @Override
    protected Class<Product> getEntityClass(){
        return Product.class;
    }

    public List<Product> findByCategory(String categoryId) throws ExecutionException, InterruptedException {
        return firestore.collection(getCollectionName())
            .whereEqualTo("categiryId", categoryId)
            .whereEqualTo("active", true)
            .get()
            .get()
            .toObjects(Product.class);
    }

    public List<Product> findFeatured() throws ExecutionException, InterruptedException {
        return firestore.collection(getCollectionName())
            .whereEqualTo("isFeatured", true)
            .whereEqualTo("active", true)
            .get()
            .get()
            .toObjects(null);
    }

    public List<Product> findLowStock(int threshold) throws ExecutionException, InterruptedException {
        return firestore.collection(getCollectionName())
            .whereLessThan("stock", threshold)
            .whereEqualTo("active", true)
            .get()
            .get()
            .toObjects(Product.class);
    }

    public List<Product> searchByName(String searchTerm) throws ExecutionException, InterruptedException {
        List<Product> allProducts = findAll();

        String searchLower = searchTerm.toLowerCase();

        return allProducts.stream()
            .filter(p -> p.getNombreProducto().toLowerCase().contains(searchLower) ||
                        (p.getDescripcionProducto() != null &&
                        p.getDescripcionProducto().toLowerCase().contains(searchLower)))
            .collect(Collectors.toList());
    }
}
