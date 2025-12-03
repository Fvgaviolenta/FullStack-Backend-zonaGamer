package com.zonagamer.zonagamer_backend.dto;


import com.zonagamer.zonagamer_backend.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String id;

    private String userId;

    private List<OrderItemDTO> items;

    private Double total;

    private Order.OrderStatus status;

    private String direccionDelivery;

    private String notas;

    private LocalDateTime fechaDeCreacion;

    private String numeroDeOrden;
}
