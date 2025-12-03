package com.zonagamer.zonagamer_backend.controller;

import com.zonagamer.zonagamer_backend.dto.CategoryCreateDTO;
import com.zonagamer.zonagamer_backend.dto.CategoryResponseDTO;
import com.zonagamer.zonagamer_backend.security.UserPrincipal;
import com.zonagamer.zonagamer_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoryController {
    

    private final CategoryService categoryService;


    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> obtenerTodasLasCategorias() throws ExecutionException, InterruptedException {

        log.debug("Obteniendo todas las categorias");

        List<CategoryResponseDTO> categorias = categoryService.getAllCategories();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/root")
    public ResponseEntity<List<CategoryResponseDTO>> obtenerCategoriaRoot() throws ExecutionException, InterruptedException {

        log.debug("Obteniendo categorias raiz");

        List<CategoryResponseDTO> categorias = categoryService.getRootCategories();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> obtenerCategoriaPorId(@PathVariable String id) throws ExecutionException, InterruptedException {

        log.debug("Buscando categorias: {}", id);

        CategoryResponseDTO categoria = categoryService.getCategoryById(id);

        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/{id}/hija")
    public ResponseEntity<List<CategoryResponseDTO>> obtenerCategoriaHija(@PathVariable String id) throws ExecutionException, InterruptedException {


        log.debug("Obteniendo subcategoria de: {}", id);

        List<CategoryResponseDTO> hija = categoryService.getCategoryChildren(id);

        return ResponseEntity.ok(hija);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponseDTO> crearCategoria(
        @Valid @RequestBody CategoryCreateDTO dto,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} crear categoria: {}", currentUser.getUsername(), dto.getNombreCategoria());

        CategoryResponseDTO categoria = categoryService.createCategory(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponseDTO> actualizarCategoria(
        @PathVariable String id,
        @Valid @RequestBody CategoryCreateDTO dto,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} actualizando categoria: {}", currentUser.getUsername(), id);

        CategoryResponseDTO categoria = categoryService.updateCategory(id, dto);

        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminarCategoria(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} eliminando categoria: {}", currentUser.getUsername(), id);

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}
