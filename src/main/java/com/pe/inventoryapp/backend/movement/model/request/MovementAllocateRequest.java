package com.pe.inventoryapp.backend.movement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TRASNFERIR UNA CANTIDAD DEL STOCK A UNA LINEA DE ENTREGA
public class MovementAllocateRequest {
  @NotNull
  private Integer quantity;

  private String comment;
  
  @NotNull
  private Long idDeliveryLine;

  @NotNull
  private Long idStockLot;
}

