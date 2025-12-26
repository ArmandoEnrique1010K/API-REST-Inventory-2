package com.pe.inventoryapp.backend.movement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementLossRequest {
  private Integer quantity;
  private String comment;
  private Long idStockLot;
}
