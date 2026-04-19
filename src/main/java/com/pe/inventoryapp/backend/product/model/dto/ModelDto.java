package com.pe.inventoryapp.backend.product.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDto {
  private Long id;
  private String modelName;

  private Integer totalQuantityAvailable;

  private Long productId;
  private String productName;

  private String categoryName;
  private String typeName;

  private LocalDate entryDate;
  private LocalDate caducityDate;
}
