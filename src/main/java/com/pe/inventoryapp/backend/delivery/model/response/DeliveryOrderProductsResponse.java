package com.pe.inventoryapp.backend.delivery.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderProductsResponse {
  private Long deliveryOrderId;
  private List<ProductZoneQuantityResponse> items;
}
