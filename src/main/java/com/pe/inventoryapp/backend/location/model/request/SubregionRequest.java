package com.pe.inventoryapp.backend.location.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubregionRequest {
  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(min = 4, max = 20, message = "El nombre debe tener entre 4 y 20 caracteres")
  private String name;

  @NotNull(message = "Seleccione una region")
  private Long idRegion;
}
