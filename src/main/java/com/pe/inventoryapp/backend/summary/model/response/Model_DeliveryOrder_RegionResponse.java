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
public class Model_DeliveryOrder_RegionResponse {
  private Long id;
  private Integer requiredTotalQuantity;
  private LocalDateTime updatedAt;

  private Long modelId;
  private String modelName;
  private String modelImageUrl;

  private Long regionId;
  private String regionName;
}