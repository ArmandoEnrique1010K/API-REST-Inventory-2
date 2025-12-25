package com.pe.inventoryapp.backend.delivery.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductZoneQuantityResponse {
  private Long productId;
  private String productName;

  private Long locationId;
  private String locationName;

  private Integer totalRequiredQuantity;
}