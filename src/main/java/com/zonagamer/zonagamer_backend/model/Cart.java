package com.zonagamer.zonagamer_backend.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.annotation.ServerTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private String id;

    private String userId;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @ServerTimestamp
    private Date fechaCreacion;

    @ServerTimestamp
    private Date fechaActualizacion;

    public Double getTotal(){
        return items.stream()
            .mapToDouble(CartItem::getSubtotal)
            .sum();
    }

    public int getTotalItems(){
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    public void addItem(CartItem item){
        CartItem existingItem = items.stream()
            .filter(i -> i.getProductId().equals(item.getProductId()))
            .findFirst()
            .orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
            } else {
                items.add(item);
            }

            this.fechaActualizacion = new Date();
    }

    public void removeItem(String productId){
        items.removeIf(item -> item.getProductId().equals(productId));
        this.fechaActualizacion = new Date();
    }

    public void clear(){
        items.clear();
        this.fechaActualizacion = new Date();
    }
}
