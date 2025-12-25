package com.pe.inventoryapp.backend.location.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequest {
    @NotBlank(message = "Introduzca el nombre de una región")
    private String name;
}
