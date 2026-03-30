package com.pe.inventoryapp.backend.product.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFirstTenModelsResponse {
  private Long id;
  private String modelProductName;
}
