package com.pe.inventoryapp.backend.deliveryorder.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderComentRequest {
  private String comment;
}
