package com.pe.inventoryapp.backend.deliveryorder.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model_DeliveryOrderResponse {
  private Long id;
  private Integer requiredQuantityTotal;
  private Long modelId;
  private String modelName;
  private String modelImageUrl;
  private String productName;
}
