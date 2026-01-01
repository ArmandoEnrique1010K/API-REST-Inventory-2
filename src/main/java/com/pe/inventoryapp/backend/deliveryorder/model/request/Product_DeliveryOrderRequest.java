package com.pe.inventoryapp.backend.deliveryorder.model.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product_DeliveryOrderRequest {
  private List<Long> idProducts;
}
