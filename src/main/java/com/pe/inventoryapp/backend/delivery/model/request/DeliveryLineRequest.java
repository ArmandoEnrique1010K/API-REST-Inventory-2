package com.pe.inventoryapp.backend.delivery.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLineRequest {
  private Integer requiredQuantity;
  private Long idLocation;
  private Long idDeliveryOrder;
}
