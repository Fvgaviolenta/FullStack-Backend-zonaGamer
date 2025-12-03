package com.zonagamer.zonagamer_backend.controller;

import com.zonagamer.zonagamer_backend.dto.AddToCartDTO;
import com.zonagamer.zonagamer_backend.dto.CartResponseDTO;
import com.zonagamer.zonagamer_backend.security.UserPrincipal;
import com.zonagamer.zonagamer_backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.concurrent.ExecutionException;


@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    

    private final CartService cartService;


    @GetMapping
    public ResponseEntity<CartResponseDTO> obtenerCarrito(
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {


        log.debug("Usuario {} obteniendo carrito", currentUser.getEmail());

        CartResponseDTO cart = cartService.getCart(currentUser.getId());

        return ResponseEntity.ok(cart);
    }

    @PostMapping({"/add"})
    public ResponseEntity<CartResponseDTO> agregarAlCarrito(
        @Valid @RequestBody AddToCartDTO dto,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Usuario {} agregando producto {} al carrito (qty: {})", currentUser.getEmail(), dto.getProductId(), dto.getQuantity());

        CartResponseDTO cart = cartService.addToCart(currentUser.getId(), dto);

        return ResponseEntity.ok(cart);
    }


    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponseDTO> actualizarQtyDeItemsDelCarrito(
        @PathVariable String productId,
        @RequestParam int quantity,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {
        log.info("Usuario {} actualizando cantidad de {} a {}", currentUser.getEmail(), productId, quantity );

        CartResponseDTO cart = cartService.updateCartItemQuantity(
            currentUser.getId(),
            productId,
            quantity
        );

        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponseDTO> eliminarDelCarrito(
        @PathVariable String productId,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Usuario {} eliminando producto {} del carrito", currentUser.getUsername(), productId);

        CartResponseDTO cart = cartService.removeFromCart(currentUser.getId(), productId);

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<Void> limpiarCarrito(
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {


        log.info("Usuario {} vaciando carrito", currentUser.getUsername());

        cartService.clearCart(currentUser.getId());

        return ResponseEntity.noContent().build();
    }
}
