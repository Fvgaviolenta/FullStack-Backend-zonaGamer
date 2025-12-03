package com.zonagamer.zonagamer_backend.controller;


import com.zonagamer.zonagamer_backend.dto.ProductCreateDTO;
import com.zonagamer.zonagamer_backend.dto.ProductResponseDTO;
import com.zonagamer.zonagamer_backend.service.ProductService;
import com.zonagamer.zonagamer_backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> obtenerTodosLosProductos() throws ExecutionException, InterruptedException {

        log.debug("Obteniendo todos los productos");

        List<ProductResponseDTO> products = productService.getAllProducts();

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> obtenerProductoPorId(@PathVariable String id) throws ExecutionException, InterruptedException {

        log.debug("Buscando producto: {}", id);

        ProductResponseDTO product = productService.getProductById(id);

        return ResponseEntity.ok(product);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> obtenerProductoPorCategoria(@PathVariable String categoryId) throws ExecutionException, InterruptedException {

        log.debug("Obteniendo productos de categoria: {}", categoryId);

        List<ProductResponseDTO> products = productService.getProductsByCategory(categoryId);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponseDTO>> obtenerProductosDestacados() throws ExecutionException, InterruptedException {
        log.debug("Obteniendo productos destacados");

        List<ProductResponseDTO> productos = productService.getFeaturedProducts();

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> buscarProductos(@RequestParam(name="q") String searchTerm) throws ExecutionException, InterruptedException {

        log.debug("Buscando productos: {}", searchTerm);

        if(searchTerm.length() < 2){
            throw new IllegalArgumentException(
                "El termino de busqueda debe tener al menos 2 caracteres"
            );
        }

        List<ProductResponseDTO> products = productService.searchProducts(searchTerm);

        return ResponseEntity.ok(products);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> crearProducto(
        @Valid @RequestBody ProductCreateDTO productDTO,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException, IOException {

        log.info("Admin {} creando producto: {}", currentUser.getUsername(), productDTO.getNombreProducto());

        ProductResponseDTO product = productService.createProduct(productDTO, null);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateProduct(
        @PathVariable String id,
        @Valid @RequestBody ProductCreateDTO productDTO,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException, IOException {

        log.info("Admin {} actualizando producto: {}", currentUser.getUsername(), id);

        ProductResponseDTO product = productService.updateProduct(id, productDTO, null);

        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {
        log.info("Admin {} eliminando producto: {}", currentUser.getEmail(), id);

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ProductResponseDTO>> obtenerProductosConBajoStock(
        @RequestParam(defaultValue = "10") int threshold
    ) throws ExecutionException, InterruptedException {

        log.debug("Obteniendo productos con stock menor a: {}", threshold);

        List<ProductResponseDTO> products = productService.getBajoStockProductos(threshold);

        return ResponseEntity.ok(products);
    }
}

