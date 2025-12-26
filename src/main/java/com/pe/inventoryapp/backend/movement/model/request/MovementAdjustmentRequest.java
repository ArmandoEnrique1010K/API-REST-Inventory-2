package com.pe.inventoryapp.backend.movement.model.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// AJUSTE MANUAL DEL STOCK
public class MovementAdjustmentRequest {
  @NotNull
  private Integer quantity;
  private String comment;
  @NotNull
  private Long idStockLot;
  private LocalDate caducityDate;

  private boolean alterQuantityReceived;
}
