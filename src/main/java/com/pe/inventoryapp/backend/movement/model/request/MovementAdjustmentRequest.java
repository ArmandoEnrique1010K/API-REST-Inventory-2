package com.pe.inventoryapp.backend.movement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para ajustar manualmente el stock de un lote, hay 3 casos:
// Se produce cuando se quiere aumentar el stock de un lote (nuevo ingreso al mismo lote)
// Se produce cuando se quiere disminuir el stock de un lote (perdida)
// Se produce cuando se quiere aumentar el stock de un lote (recuperación de stock dañado considerado como perdida)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementAdjustmentRequest {
  @NotNull
  private Integer quantity;

  private String comment;
  
  @NotNull
  private Long idStockLot;
}
