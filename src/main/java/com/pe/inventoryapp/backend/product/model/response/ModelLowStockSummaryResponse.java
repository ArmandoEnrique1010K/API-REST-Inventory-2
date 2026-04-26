package com.pe.inventoryapp.backend.product.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelLowStockSummaryResponse {
  private Long id;
  private String modelName;
  
  private Long productId;
  private String productName;

  private Integer totalQuantityAvailable;
  private Integer minimumAvailableQuantity;

  private String categoryName;
  private String typeName;
}
