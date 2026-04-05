package com.pe.inventoryapp.backend.summary.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelProductSummaryDTO {
  private Long modelId;
  private String modelName;
  private Long productId;
  private String productName;
  private Long totalQuantity;
}