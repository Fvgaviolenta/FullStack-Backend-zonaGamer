package com.zonagamer.zonagamer_backend.service;

import com.zonagamer.zonagamer_backend.dto.ProductCreateDTO;
import com.zonagamer.zonagamer_backend.dto.ProductResponseDTO;
import com.zonagamer.zonagamer_backend.model.Product;
import com.zonagamer.zonagamer_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final StorageService storageService;
    private final CategoryService categoryService;
    
    public ProductResponseDTO createProduct(ProductCreateDTO dto, MultipartFile image) 
            throws ExecutionException, InterruptedException, IOException {
        
        // Asegurar que la categoría exista (si no existe, se crea automáticamente)
        categoryService.ensureCategoryExists(dto.getCategoryId());
        
        String imageUrl = image != null ? 
            storageService.uploadFile(image, "products") : 
            dto.getImageUrl();
        
        Product product = Product.builder()
            .nombreProducto(dto.getNombreProducto())
            .precio(dto.getPrice())
            .descripcionProducto(dto.getDescripcion())
            .categoryId(dto.getCategoryId())
            .stock(dto.getStock())
            .imageUrl(imageUrl)
            .featured(dto.getIsFeatured())
            .active(true)
            .fechaCreacion(new java.util.Date())
            .fechaActualizacion(new java.util.Date())
            .build();
            
        String id = productRepository.save(product);
        product.setId(id);
        
        return mapToDTO(product);
    }
    
    public List<ProductResponseDTO> getAllProducts() throws ExecutionException, InterruptedException {
        return productRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public List<ProductResponseDTO> getProductsByCategory(String categoryId) 
            throws ExecutionException, InterruptedException {
        return productRepository.findByCategory(categoryId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public ProductResponseDTO getProductById(String id) 
            throws ExecutionException, InterruptedException {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return mapToDTO(product);
    }
    
    public List<ProductResponseDTO> getFeaturedProducts() 
            throws ExecutionException, InterruptedException {
        return productRepository.findAll().stream()
            .filter(Product::isFeatured)
            .filter(Product::isActive)
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public List<ProductResponseDTO> searchProducts(String searchTerm) 
            throws ExecutionException, InterruptedException {
        String lowerSearch = searchTerm.toLowerCase();
        return productRepository.findAll().stream()
            .filter(p -> p.getNombreProducto().toLowerCase().contains(lowerSearch) ||
                        p.getDescripcionProducto().toLowerCase().contains(lowerSearch))
            .filter(Product::isActive)
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public List<ProductResponseDTO> getBajoStockProductos(int threshold) 
            throws ExecutionException, InterruptedException {
        return productRepository.findAll().stream()
            .filter(p -> p.getStock() <= threshold)
            .filter(Product::isActive)
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public ProductResponseDTO updateProduct(String id, ProductCreateDTO dto) 
            throws ExecutionException, InterruptedException {
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
        product.setNombreProducto(dto.getNombreProducto());
        product.setPrecio(dto.getPrice());
        product.setDescripcionProducto(dto.getDescripcion());
        product.setCategoryId(dto.getCategoryId());
        product.setStock(dto.getStock());
        product.setFeatured(dto.getIsFeatured());
        product.setFechaActualizacion(new java.util.Date());
        
        productRepository.update(id, product);
        return mapToDTO(product);
    }
    
    public ProductResponseDTO updateProduct(String id, ProductCreateDTO dto, MultipartFile image) 
            throws ExecutionException, InterruptedException, IOException {
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (image != null) {
            String imageUrl = storageService.uploadFile(image, "products");
            product.setImageUrl(imageUrl);
        }
            
        product.setNombreProducto(dto.getNombreProducto());
        product.setPrecio(dto.getPrice());
        product.setDescripcionProducto(dto.getDescripcion());
        product.setCategoryId(dto.getCategoryId());
        product.setStock(dto.getStock());
        product.setFeatured(dto.getIsFeatured());
        product.setFechaActualizacion(new java.util.Date());
        
        productRepository.update(id, product);
        return mapToDTO(product);
    }
    
    public void deleteProduct(String id) throws ExecutionException, InterruptedException {
        productRepository.delete(id);
    }
    
    public void reduceStock(String productId, Integer quantity) 
            throws ExecutionException, InterruptedException {
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productId));
        
        int newStock = product.getStock() - quantity;
        
        if (newStock < 0) {
            throw new RuntimeException("Stock insuficiente para producto: " + product.getNombreProducto());
        }
        
        product.setStock(newStock);
        productRepository.update(productId, product);
    }
    
    public void increaseStock(String productId, Integer quantity) 
            throws ExecutionException, InterruptedException {
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productId));
        
        int newStock = product.getStock() + quantity;
        product.setStock(newStock);
        productRepository.update(productId, product);
    }
    
    private ProductResponseDTO mapToDTO(Product product) {
        return ProductResponseDTO.builder()
            .id(product.getId())
            .nombre(product.getNombreProducto())
            .precio(product.getPrecio())
            .descripcion(product.getDescripcionProducto())
            .imageUrl(product.getImageUrl())
            .categoryId(product.getCategoryId())
            .stock(product.getStock())
            .featured(product.isFeatured())
            .disponibilidad(product.isActive())
            .fechaCreacion(product.getFechaCreacion() != null ? 
                product.getFechaCreacion().toString() : null)
            .build();
    }
}