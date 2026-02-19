package com.pe.inventoryapp.backend.movement.model.response;

import java.time.LocalDateTime;
import java.util.List;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementDetailsResponse {
  private Long id;
  private Integer quantity;
  private String comment;
  private LocalDateTime createdAt;
  private MovementType movementType;

  private String userName;

  private Long modelId;
  private String modelName;

  private List<Long> movements_StockLots;

  private Long stockLotReceiverId;
  private String stockLotReceiverBatch;

  private Long stockLotEmitterId;
  private String stockLotEmitterBatch;

  private Long deliveryLineId;
}
