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
  
  // Introduce la cantidad en que se va a aumentar o disminuir si es un número negativo
  @NotNull
  private Integer quantity;

  private String comment;
  
  @NotNull
  private Long idStockLot;

  // Campo opcional
  private LocalDate caducityDate;

  private boolean alterQuantityReceived;
}
