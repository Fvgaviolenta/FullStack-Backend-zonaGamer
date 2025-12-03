package com.zonagamer.zonagamer_backend.service;

import com.zonagamer.zonagamer_backend.dto.CheckoutDTO;
import com.zonagamer.zonagamer_backend.dto.OrderItemDTO;
import com.zonagamer.zonagamer_backend.dto.OrderResponseDTO;
import com.zonagamer.zonagamer_backend.exception.InsufficientStockException;
import com.zonagamer.zonagamer_backend.exception.ResourceNotFoundException;
import com.zonagamer.zonagamer_backend.model.*;
import com.zonagamer.zonagamer_backend.repository.CartRepository;
import com.zonagamer.zonagamer_backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    
    public OrderResponseDTO checkout(String userId, CheckoutDTO checkoutDTO) 
            throws ExecutionException, InterruptedException {
        
        log.info("Procesando checkout para usuario: {}", userId);
        
        // Obtener cart para validación
        var cartResponse = cartService.getCart(userId);
        
        if (cartResponse.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }
        
        // Obtener Cart real desde el repositorio
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Carrito no encontrado para usuario: " + userId
            ));
        
        // Validar stock
        cartService.validateCartStock(cart);
        
        log.debug("Carrito contiene {} items, total: ${}", 
            cart.getItems().size(), cart.getTotal());

        List<OrderItem> orderItems = cart.getItems().stream()
            .map(cartItem -> OrderItem.builder()
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .quantity(cartItem.getQuantity())
                .precioEnCompra(cartItem.getPrecio())  // Precio al momento de la compra
                .build())
            .collect(Collectors.toList());

        Order order = Order.builder()
            .userId(userId)
            .items(orderItems)
            .total(cart.getTotal())
            .status(Order.OrderStatus.PAID)  // Simulamos que ya está pagada
            .deliveryAddress(checkoutDTO.getDeliveryAddress())
            .notes(checkoutDTO.getNotes())
            .build();

        String orderId = orderRepository.save(order);
        order.setId(orderId);
        
        log.info("✅ Orden creada: {}", orderId);
        
        // 7. Reducir stock de cada producto
        try {
            for (CartItem item : cart.getItems()) {
                productService.reduceStock(item.getProductId(), item.getQuantity());
                log.debug("Stock reducido para: {} (-{})", 
                    item.getProductName(), item.getQuantity());
            }
        } catch (Exception e) {
            // Si falla la reducción de stock, deberíamos revertir la orden
            // Para simplicidad, solo logueamos el error
            log.error("❌ Error al reducir stock: {}", e.getMessage());
            throw new RuntimeException("Error al procesar la orden", e);
        }
        
        // 8. Vaciar el carrito
        cartService.clearCart(userId);
        
        log.info("✅ Checkout completado exitosamente. Orden: {}", orderId);
        
        // 9. Retornar orden como DTO
        return mapToResponseDTO(order);
    }
    
    public List<OrderResponseDTO> getUserOrders(String userId) 
            throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo órdenes de usuario: {}", userId);
        
        List<Order> orders = orderRepository.findByUserId(userId);
        
        return orders.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public OrderResponseDTO getOrderById(String orderId) 
            throws ExecutionException, InterruptedException {
        
        log.debug("Buscando orden: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Orden no encontrada: " + orderId
            ));
        
        return mapToResponseDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders() 
            throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo todas las órdenes");
        
        List<Order> orders = orderRepository.findAll();
        
        return orders.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<OrderResponseDTO> getOrdersByStatus(Order.OrderStatus status) 
            throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo órdenes con estado: {}", status);
        
        List<Order> orders = orderRepository.findByStatus(status);
        
        return orders.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    

    public OrderResponseDTO updateOrderStatus(String orderId, Order.OrderStatus newStatus) 
            throws ExecutionException, InterruptedException {
        
        log.info("Actualizando estado de orden {}: {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Orden no encontrada: " + orderId
            ));
        
        order.setStatus(newStatus);
        
        orderRepository.update(orderId, order);
        
        log.info("✅ Estado de orden actualizado");
        
        return mapToResponseDTO(order);
    }
    

    public void cancelOrder(String orderId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Cancelando orden: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Orden no encontrada: " + orderId
            ));
        
        // Verificar que se pueda cancelar
        if (order.getStatus() == Order.OrderStatus.SHIPPED || 
            order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                "No se puede cancelar una orden ya enviada o entregada"
            );
        }
        
        // Restaurar stock de cada producto
        for (OrderItem item : order.getItems()) {
            productService.increaseStock(item.getProductId(), item.getQuantity());
            log.debug("Stock restaurado para: {} (+{})", 
                item.getProductName(), item.getQuantity());
        }
        
        // Marcar orden como cancelada
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.update(orderId, order);
        
        log.info("✅ Orden cancelada: {}", orderId);
    }
    

    private String generateOrderNumber(String orderId) {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        
        // Extraer últimos 6 caracteres del ID
        String shortId = orderId.length() > 6 
            ? orderId.substring(orderId.length() - 6)
            : orderId;
        
        return String.format("ORD-%s-%s", year, shortId.toUpperCase());
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime();
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
            .map(item -> OrderItemDTO.builder()
                .productId(item.getProductId())
                .nombreProducto(item.getProductName())
                .quantity(item.getQuantity())
                .precioEnCompra(item.getPrecioEnCompra())
                .subtotal(item.getSubtotal())
                .build())
            .collect(Collectors.toList());
        
        return OrderResponseDTO.builder()
            .id(order.getId())
            .userId(order.getUserId())
            .items(itemDTOs)
            .total(order.getTotal())
            .status(order.getStatus())
            .direccionDelivery(order.getDeliveryAddress())
            .notas(order.getNotes())
            .fechaDeCreacion(order.getFechaDeCreacion() != null ? 
                convertToLocalDateTime(order.getFechaDeCreacion()) : null)
            .numeroDeOrden(generateOrderNumber(order.getId()))
            .build();
    }
}