package com.pe.inventoryapp.backend.deliveryorder.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product_DeliveryOrder_RegionResponse {
  // TODO: IMPLEMENTAR ESTO PARA MOSTRAR LAS ORDENES POR REGION
  private Long productId;
  private String productName;

  private Long locationId;
  private String locationName;

  private Integer totalRequiredQuantity;
}