package com.pe.inventoryapp.backend.product.model.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.annotation.Nullable;
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
public class ProductRequest {
  @NotBlank(message = "Introduzca un nombre del producto")
  private String name;

  // Estos campos son opcionales

  // Las medidas se expresan en centimetros
  private Double length;
  private Double width;

  // URL absoluta de la imagen
  private String imageUrl;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Nullable
  private LocalDate entryDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Nullable
  private LocalDate caducityDate;

  @NotNull(message = "Seleccione una categoria")
  private Long idCategory;
}
