package com.zonagamer.zonagamer_backend.dto;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



@Data
public class CheckoutDTO {
    

    @NotBlank(message = "La direccion de entrega es obligatoria")
    @Size(min=10, max = 200, message = "La direccion debe tener entre 10 a 200 caracteres")
    private String deliveryAddress;

    @Size(max = 500, message = "Las notas no pueden exceder los 500 caracetes")
    private String notes;
}
