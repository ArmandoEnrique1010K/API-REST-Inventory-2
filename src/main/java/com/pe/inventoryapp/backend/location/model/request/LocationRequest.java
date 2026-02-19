package com.pe.inventoryapp.backend.location.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 4, max = 20, message = "El nombre debe tener entre 4 y 20 caracteres")
    private String name;
    
    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ .,;:()\\-_/]*$", message = "La dirección contiene caracteres no permitidos")
    private String address;

    @NotNull(message = "Seleccione una subregion")
    private Long idSubregion;
}
