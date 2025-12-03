package com.zonagamer.zonagamer_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    


    private String token;

    @Builder.Default
    private String type = "Bearer";

    private String userId;

    private String email;

    private String nombreCompleto;

    @JsonProperty("admin")
    private boolean admin;
}
