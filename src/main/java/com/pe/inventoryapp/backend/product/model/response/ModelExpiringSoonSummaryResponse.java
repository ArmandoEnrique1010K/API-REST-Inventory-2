package com.pe.inventoryapp.backend.product.model.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelExpiringSoonSummaryResponse {
  private Long id;
  private String modelName;

  private Long productId;
  private String productName;

  private String categoryName;
  private String typeName;

  private LocalDate caducityDate;
  private Integer totalQuantityAvailable;
}
