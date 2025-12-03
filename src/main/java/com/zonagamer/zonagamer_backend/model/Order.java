package com.zonagamer.zonagamer_backend.model;

import lombok.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import com.google.cloud.firestore.annotation.ServerTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;

    private String userId;

    private List<OrderItem> items;

    private Double total;

    private OrderStatus status;

    private String deliveryAddress;

    private String notes;

    @ServerTimestamp
    private Date FechaDeCreacion;

    public enum OrderStatus{
        PENDING,
        PAID,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}
