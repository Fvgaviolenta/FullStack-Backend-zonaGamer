package com.zonagamer.zonagamer_backend.service;

import com.zonagamer.zonagamer_backend.dto.AddToCartDTO;
import com.zonagamer.zonagamer_backend.dto.CartItemDTO;
import com.zonagamer.zonagamer_backend.dto.CartResponseDTO;
import com.zonagamer.zonagamer_backend.exception.InsufficientStockException;
import com.zonagamer.zonagamer_backend.exception.ResourceNotFoundException;
import com.zonagamer.zonagamer_backend.model.Cart;
import com.zonagamer.zonagamer_backend.model.CartItem;
import com.zonagamer.zonagamer_backend.model.Product;
import com.zonagamer.zonagamer_backend.repository.CartRepository;
import com.zonagamer.zonagamer_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    

    private static final double TAX_RATE = 0.19;

    public CartResponseDTO getCart(String userId) 
            throws ExecutionException, InterruptedException {
        
        log.debug("Obteniendo carrito de usuario: {}", userId);
        

        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> {

                log.info("Creando nuevo carrito para usuario: {}", userId);
                
                Cart newCart = Cart.builder()
                    .userId(userId)
                    .items(new ArrayList<>())
                    .fechaCreacion(new Date())
                    .build();
                
                try {
                    String cartId = cartRepository.save(newCart);
                    newCart.setId(cartId);
                } catch (ExecutionException | InterruptedException e) {
                    log.error("❌ Error al crear carrito: {}", e.getMessage());
                    throw new RuntimeException("Error al crear carrito", e);
                }
                
                return newCart;
            });
        

        return mapToResponseDTO(cart);
    }

    public CartResponseDTO addToCart(String userId, AddToCartDTO dto) 
            throws ExecutionException, InterruptedException {
        
        log.info("Agregando producto al carrito: userId={}, productId={}, quantity={}", 
            userId, dto.getProductId(), dto.getQuantity());
        
        // 1. Buscar producto por document ID
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Producto no encontrado: " + dto.getProductId()
            ));
        
        // 2. Verificar que esté activo
        if (!product.isActive()) {
            throw new IllegalStateException("El producto no está disponible");
        }
        
        // 3. Verificar stock disponible
        if (product.getStock() < dto.getQuantity()) {
            log.warn("⚠️ Stock insuficiente: disponible={}, solicitado={}", 
                product.getStock(), dto.getQuantity());
            
            throw new InsufficientStockException(
                String.format("Stock insuficiente. Disponible: %d", product.getStock())
            );
        }
        
        // 4. Obtener carrito existente O crear uno nuevo
        Cart cart;
        boolean isNewCart = false;
        
        var existingCart = cartRepository.findByUserId(userId);
        
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            log.debug("Carrito existente encontrado con ID: {}", cart.getId());
        } else {
            // Crear nuevo carrito
            log.info("Creando nuevo carrito para usuario: {}", userId);
            
            cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .fechaCreacion(new Date())
                .build();
            
            // Guardar y obtener el ID generado
            String cartId = cartRepository.save(cart);
            cart.setId(cartId);
            isNewCart = true;
            
            log.info("✅ Nuevo carrito creado con ID: {}", cartId);
        }
        
        // Verificar que tenemos un ID válido
        if (cart.getId() == null || cart.getId().isEmpty()) {
            log.error("❌ CRÍTICO: Cart ID es null después de obtener/crear carrito");
            throw new RuntimeException("Error al obtener/crear carrito: ID es null");
        }
        
        // 5. Buscar si el producto ya está en el carrito
        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(dto.getProductId()))
            .findFirst()
            .orElse(null);
        
        if (existingItem != null) {
            // Ya existe: actualizar cantidad
            int newQuantity = existingItem.getQuantity() + dto.getQuantity();
            
            // Verificar que no exceda el stock
            if (newQuantity > product.getStock()) {
                throw new InsufficientStockException(
                    String.format("Stock insuficiente. Disponible: %d, en carrito: %d", 
                        product.getStock(), existingItem.getQuantity())
                );
            }
            
            existingItem.setQuantity(newQuantity);
            log.debug("Item actualizado en carrito: nueva cantidad={}", newQuantity);
            
        } else {
            // No existe: agregar nuevo item
            CartItem newItem = CartItem.builder()
                .productId(product.getId())
                .productName(product.getNombreProducto())
                .imageUrl(product.getImageUrl())
                .quantity(dto.getQuantity())
                .precio(product.getPrecio())
                .build();
            
            cart.getItems().add(newItem);
            log.debug("Nuevo item agregado al carrito");
        }
        
        // 6. Actualizar timestamp
        cart.setFechaActualizacion(new Date());
        
        // 7. Guardar cambios
        log.debug("Actualizando carrito con ID: {}", cart.getId());
        cartRepository.update(cart.getId(), cart);
        
        log.info("✅ Carrito actualizado: {} items", cart.getItems().size());
        
        return mapToResponseDTO(cart);
    }

    public CartResponseDTO updateCartItemQuantity(String userId, String productId, int newQuantity) 
            throws ExecutionException, InterruptedException {
        
        log.info("Actualizando cantidad en carrito: userId={}, productId={}, newQuantity={}", 
            userId, productId, newQuantity);
        
        // Validar cantidad
        if (newQuantity < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        
        // Obtener carrito
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Carrito no encontrado para usuario: " + userId
            ));
        
        // Buscar item
        CartItem item = cart.getItems().stream()
            .filter(i -> i.getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(
                "Producto no encontrado en el carrito: " + productId
            ));
        
        // Verificar stock disponible
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Producto no encontrado: " + productId
            ));
        
        if (newQuantity > product.getStock()) {
            throw new InsufficientStockException(
                String.format("Stock insuficiente. Disponible: %d", product.getStock())
            );
        }
        
        // Actualizar cantidad
        item.setQuantity(newQuantity);
        cart.setFechaActualizacion(new Date());
        
        // Guardar cambios
        cartRepository.update(cart.getId(), cart);
        
        log.info("✅ Cantidad actualizada en carrito");
        
        return mapToResponseDTO(cart);
    }

    public CartResponseDTO removeFromCart(String userId, String productId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Eliminando producto del carrito: userId={}, productId={}", 
            userId, productId);
        
        // Obtener carrito
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Carrito no encontrado para usuario: " + userId
            ));
        
        // Eliminar item
        boolean removed = cart.getItems().removeIf(
            item -> item.getProductId().equals(productId)
        );
        
        if (!removed) {
            throw new ResourceNotFoundException(
                "Producto no encontrado en el carrito: " + productId
            );
        }
        
        // Actualizar timestamp
        cart.setFechaActualizacion(new Date());
        
        // Guardar cambios
        cartRepository.update(cart.getId(), cart);
        
        log.info("✅ Producto eliminado del carrito");
        
        return mapToResponseDTO(cart);
    }
    

    public void clearCart(String userId) 
            throws ExecutionException, InterruptedException {
        
        log.info("Vaciando carrito de usuario: {}", userId);
        
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Carrito no encontrado para usuario: " + userId
            ));
        
        cart.getItems().clear();
        cart.setFechaActualizacion(new Date());
        
        cartRepository.update(cart.getId(), cart);
        
        log.info("✅ Carrito vaciado");
    }

    public boolean validateCartStock(Cart cart) 
            throws ExecutionException, InterruptedException {
        
        log.debug("Validando stock del carrito");
        
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Producto no encontrado: " + item.getProductId()
                ));
            
            // Verificar que esté activo
            if (!product.isActive()) {
                throw new IllegalStateException(
                    "El producto '" + product.getNombreProducto() + "' ya no está disponible"
                );
            }
            
            // Verificar stock
            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                    String.format(
                        "Stock insuficiente para '%s'. Disponible: %d, en carrito: %d",
                        product.getNombreProducto(),
                        product.getStock(),
                        item.getQuantity()
                    )
                );
            }
        }
        
        log.debug("✅ Stock validado correctamente");
        return true;
    }
    
    private CartResponseDTO mapToResponseDTO(Cart cart) 
            throws ExecutionException, InterruptedException {
        
        // Convertir items a DTOs y verificar disponibilidad
        List<CartItemDTO> itemDTOs = new ArrayList<>();
        
        for (CartItem item : cart.getItems()) {
            try {
                Product product = productRepository.findById(item.getProductId())
                    .orElse(null);
                
                boolean available = product != null && 
                                product.isActive() && 
                                product.getStock() >= item.getQuantity();
                
                CartItemDTO itemDTO = CartItemDTO.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .imageUrl(item.getImageUrl())
                    .quantity(item.getQuantity())
                    .precio(item.getPrecio())
                    .subtotal(item.getSubtotal())
                    .disponibilidad(available)
                    .build();
                
                itemDTOs.add(itemDTO);
                
            } catch (Exception e) {
                log.warn("⚠️ Error al procesar item del carrito: {}", e.getMessage());
                // Continuar con los demás items
            }
        }
        
        // Calcular totales
        double subtotal = itemDTOs.stream()
            .mapToDouble(CartItemDTO::getSubtotal)
            .sum();
        
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;
        
        int totalItems = itemDTOs.stream()
            .mapToInt(CartItemDTO::getQuantity)
            .sum();
        
        return CartResponseDTO.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .items(itemDTOs)
            .subtotal(subtotal)
            .iva(tax)
            .total(total)
            .totalItems(totalItems)
            .build();
    }
}