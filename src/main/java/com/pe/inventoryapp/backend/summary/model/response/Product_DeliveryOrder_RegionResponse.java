package com.pe.inventoryapp.backend.summary.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product_DeliveryOrder_RegionResponse {
  private Long productId;
  private String productName;

  private Long regionId;
  private String regionName;

  private LocalDateTime updatedAt;

  private Integer requiredTotalQuantity;
}