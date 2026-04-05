package com.pe.inventoryapp.backend.movement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movement_StockLotResponse {
  private Long id;
  private Integer quantityTaken;
  private Long stockLotId;
  private String stockLotBatch;
}
