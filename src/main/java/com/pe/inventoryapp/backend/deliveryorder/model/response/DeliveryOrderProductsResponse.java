package com.pe.inventoryapp.backend.deliveryorder.model.response;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryline.model.response.ProductZoneQuantityResponse;

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
