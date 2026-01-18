package com.pe.inventoryapp.backend.deliveryline.model.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLineAllocateRequest {
  @NotNull(message = "Especifique la cantidad")
  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private Integer quantity;

  @NotNull(message = "Seleccione al menos un lote")
  private List<Long> idStockLots;
}
