package com.pe.inventoryapp.backend.summary.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryResponse {
  private Long productModelId;
  private String productModelName;
  private Integer quantity;
}
