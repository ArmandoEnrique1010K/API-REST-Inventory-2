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

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Nullable
  private LocalDate entryDate;
  private LocalDate caducityDate;
  private Double length;
  private Double width;
  private Double height;

  @NotNull(message = "El stock es obligatorio")
  private Integer stock;
  private String imageUrl;
  private Long idCategory;
}
