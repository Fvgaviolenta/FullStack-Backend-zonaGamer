package com.zonagamer.zonagamer_backend.service;

import com.zonagamer.zonagamer_backend.dto.CategoryCreateDTO;
import com.zonagamer.zonagamer_backend.dto.CategoryResponseDTO;
import com.zonagamer.zonagamer_backend.exception.ResourceNotFoundException;
import com.zonagamer.zonagamer_backend.model.Category;
import com.zonagamer.zonagamer_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar categorías
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Obtiene todas las categorías
     */
    public List<CategoryResponseDTO> getAllCategories() 
            throws ExecutionException, InterruptedException {
        
        List<Category> categories = categoryRepository.findAll();
        
        return categories.stream()
            .filter(Category::isActive)
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene categorías raíz
     */
    public List<CategoryResponseDTO> getRootCategories() 
            throws ExecutionException, InterruptedException {
        
        List<Category> categories = categoryRepository.findRootCategories();
        
        return categories.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una categoría por ID
     */
    public CategoryResponseDTO getCategoryById(String id) 
            throws ExecutionException, InterruptedException {
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Categoría no encontrada: " + id
            ));
        
        return mapToResponseDTO(category);
    }
    
    /**
     * Obtiene subcategorías de una categoría padre
     */
    public List<CategoryResponseDTO> getCategoryChildren(String parentId) 
            throws ExecutionException, InterruptedException {
        
        List<Category> children = categoryRepository.findByParentId(parentId);
        
        return children.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Crea una nueva categoría
     */
    public CategoryResponseDTO createCategory(CategoryCreateDTO dto) 
            throws ExecutionException, InterruptedException {
        
        log.info("Creando categoría: {}", dto.getNombreCategoria());
        
        // Verificar que no exista el ID
        if (categoryRepository.findById(dto.getId()).isPresent()) {
            throw new IllegalArgumentException(
                "Ya existe una categoría con ID: " + dto.getId()
            );
        }
        
        // Verificar que la categoría padre exista (si se especifica)
        if (dto.getParentId() != null) {
            categoryRepository.findById(dto.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Categoría padre no encontrada: " + dto.getParentId()
                ));
        }
        
        Category category = Category.builder()
            .id(dto.getId())
            .nombreCategoria(dto.getNombreCategoria())
            .parentId(dto.getParentId())
            .active(true)
            .build();
        
        categoryRepository.save(category);
        
        log.info("✅ Categoría creada: {}", dto.getId());
        
        return mapToResponseDTO(category);
    }
    
    /**
     * Actualiza una categoría
     */
    public CategoryResponseDTO updateCategory(String id, CategoryCreateDTO dto) 
            throws ExecutionException, InterruptedException {
        
        log.info("Actualizando categoría: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Categoría no encontrada: " + id
            ));
        
        category.setNombreCategoria(dto.getNombreCategoria());
        category.setParentId(dto.getParentId());
        
        categoryRepository.update(id, category);
        
        log.info("✅ Categoría actualizada: {}", id);
        
        return mapToResponseDTO(category);
    }
    
    /**
     * Elimina una categoría (soft delete)
     */
    public void deleteCategory(String id) 
            throws ExecutionException, InterruptedException {
        
        log.info("Eliminando categoría: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Categoría no encontrada: " + id
            ));
        
        // Verificar que no tenga subcategorías activas
        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new IllegalStateException(
                "No se puede eliminar una categoría con subcategorías activas"
            );
        }
        
        category.setActive(false);
        categoryRepository.update(id, category);
        
        log.info("✅ Categoría marcada como inactiva: {}", id);
    }
    
    /**
     * Convierte Category a CategoryResponseDTO
     */
    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
            .id(category.getId())
            .nombreCategoria(category.getNombreCategoria())
            .parentId(category.getParentId())
            .active(category.isActive())
            .build();
    }
}