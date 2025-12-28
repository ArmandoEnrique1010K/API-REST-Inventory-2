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
// MOVIMIENTO DE DEVOLUCIÓN DE LA LINEA DE ENTREGA
public class MovementReturnRequest {
  @NotNull
  private Integer quantity;
  private String comment;
  @NotNull
  private Long idDeliveryLine;
  private boolean returnByChange;
}
