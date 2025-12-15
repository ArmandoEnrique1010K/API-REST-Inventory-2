package com.pe.inventoryapp.backend.movement.model.request;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.data.Reason;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementRequest {
  private Integer quantity;
  private String comment;
  private MovementType movementType;
  private Reason reason;

  private Long idDeliveryLine;
  private Long idStockLot;
}
