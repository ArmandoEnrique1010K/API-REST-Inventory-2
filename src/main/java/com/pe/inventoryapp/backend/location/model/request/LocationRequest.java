package com.pe.inventoryapp.backend.location.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    @NotBlank(message = "Introduzca el nombre de la ubicación")
    private String name;
    
    @NotNull(message = "Seleccione una región")
    private Long idRegion;
}
