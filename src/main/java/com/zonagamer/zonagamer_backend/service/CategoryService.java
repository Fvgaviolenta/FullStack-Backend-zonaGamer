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
     * Crea o verifica que exista una categoría (usado al crear productos)
     * Si la categoría no existe, la crea automáticamente
     */
    public void ensureCategoryExists(String categoryId) 
            throws ExecutionException, InterruptedException {
        
        // Verificar si la categoría ya existe
        var existingCategory = categoryRepository.findById(categoryId);
        
        if (existingCategory.isEmpty()) {
            log.info("Categoría '{}' no existe, creando automáticamente...", categoryId);
            
            // Crear la categoría con un nombre formateado
            String formattedName = formatCategoryName(categoryId);
            
            Category category = Category.builder()
                .id(categoryId)
                .nombreCategoria(formattedName)
                .parentId(null)  // Categoría raíz por defecto
                .active(true)
                .build();
            
            categoryRepository.save(category);
            
            log.info("✅ Categoría '{}' creada automáticamente", categoryId);
        } else {
            log.debug("Categoría '{}' ya existe", categoryId);
        }
    }
    
    /**
     * Formatea el ID de categoría a un nombre legible
     * Ejemplo: "componentes-pc" -> "Componentes Pc"
     */
    private String formatCategoryName(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) {
            return "Sin categoría";
        }
        
        // Reemplazar guiones y guiones bajos por espacios
        String formatted = categoryId.replace("-", " ").replace("_", " ");
        
        // Capitalizar primera letra de cada palabra
        String[] words = formatted.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }
        
        return result.toString().trim();
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