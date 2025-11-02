package com.pe.inventoryapp.backend.product.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
  @NotBlank(message = "Introduzca una categoria")
  private String name;
  private Boolean status;
}
