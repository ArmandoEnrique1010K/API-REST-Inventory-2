package com.pe.inventoryapp.backend.product.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
  @NotBlank(message = "Introduzca un nombre para la categoria")
  private String name;
}
