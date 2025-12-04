package com.zonagamer.zonagamer_backend.controller;

import com.zonagamer.zonagamer_backend.dto.CheckoutDTO;
import com.zonagamer.zonagamer_backend.dto.OrderResponseDTO;
import com.zonagamer.zonagamer_backend.model.Order;
import com.zonagamer.zonagamer_backend.security.UserPrincipal;
import com.zonagamer.zonagamer_backend.service.OrderService;

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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(
        @Valid @RequestBody CheckoutDTO checkoutDto,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Usuario {} procesando checkout", currentUser.getUsername());

        OrderResponseDTO order = orderService.checkout(currentUser.getId(), checkoutDto);

        log.info("Orden creada: {} (Total: ${})", order.getNumeroDeOrden(), order.getTotal());

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }


    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> obtenerMisOrdenes(
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.debug("Usuario {} obteniendo sus ordenes", currentUser.getUsername());

        List<OrderResponseDTO> orders = orderService.getUserOrders(currentUser.getId());

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> obtenerOrdenPorId(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.debug("Usuario {} buscando orden: {}", currentUser.getUsername(), id);

        OrderResponseDTO order = orderService.getOrderById(id);

        if(!currentUser.isAdmin() && !order.getUserId().equals(currentUser.getId())) {
            log.warn("Usuario {} intento ver orden de otro usuario", currentUser.getUsername());

            throw new org.springframework.security.access.AccessDeniedException(
                "No tienes permisos para ver esta orden"
            );
        }

        return ResponseEntity.ok(order);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> obtenerTodasLasOrdenes() throws ExecutionException, InterruptedException {
        
        log.debug("Admin obteniendo todas las ordeness");

        List<OrderResponseDTO> orders = orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> obtenerOrdenesPorStatus(
        @PathVariable Order.OrderStatus status
    ) throws ExecutionException, InterruptedException {

        log.debug("Admin obteniendo ordenes con estado: {}", status);

        List<OrderResponseDTO> orders = orderService.getOrdersByStatus(status);

        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderResponseDTO> actualizarOrderStatus(
        @PathVariable String id,
        @RequestParam Order.OrderStatus newStatus,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Admin {} actualizando orden {} a estado: {}", currentUser.getUsername(), id, newStatus);

        OrderResponseDTO order = orderService.updateOrderStatus(id, newStatus);

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarOrden(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) throws ExecutionException, InterruptedException {

        log.info("Usuario {} intentando cancelar orden: {}", currentUser.getUsername(), id);

        OrderResponseDTO order = orderService.getOrderById(id);

        // Verificar propiedad de la orden
        if (!currentUser.isAdmin() && !order.getUserId().equals(currentUser.getId())) {
            log.warn("Usuario {} intentó cancelar orden de otro usuario", currentUser.getUsername());
            throw new org.springframework.security.access.AccessDeniedException(
                "No tienes permiso para cancelar esta orden"
            );
        }

        // Usuario común solo puede cancelar si está PENDING
        if (!currentUser.isAdmin() && order.getStatus() != Order.OrderStatus.PENDING) {
            log.warn("Usuario {} intentó cancelar orden {} con estado: {}", 
                currentUser.getUsername(), id, order.getStatus());
            throw new IllegalStateException(
                "Solo puedes cancelar órdenes en estado PENDING. Esta orden ya fue procesada."
            );
        }

        orderService.cancelOrder(id, currentUser.isAdmin());

        log.info("✅ Orden {} cancelada exitosamente por {}", id, currentUser.getUsername());

        return ResponseEntity.noContent().build();
    }


}
