package com.pe.inventoryapp.backend.deliveryorder.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderRequest {
  @NotBlank(message = "Introduzca un lote de entrega")
  private String batch;
}
