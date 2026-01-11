package com.pe.inventoryapp.backend.deliveryorder.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product_DeliveryOrderResponse {
  private Long id;

  private Long productId;
  private String productName;
  private String productImageUrl;

  private Integer requiredQuantityTotal;
}
