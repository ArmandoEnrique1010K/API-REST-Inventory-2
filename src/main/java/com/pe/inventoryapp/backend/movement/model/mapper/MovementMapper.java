package com.pe.inventoryapp.backend.movement.model.mapper;

import java.util.List;

import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.response.MovementDetailsResponse;
import com.pe.inventoryapp.backend.movement.model.response.MovementListResponse;

public class MovementMapper {
  private Movement movement;

  private MovementMapper() {
  }

  public static MovementMapper builder() {
    return new MovementMapper();
  }

  public MovementMapper setMovement(Movement movement) {
    this.movement = movement;
    return this;
  }

  public MovementListResponse buildMovementListResponse() {
    if (movement == null) {
      throw new RuntimeException("Debe pasar la entidad movement");
    }
    return new MovementListResponse(
        movement.getId(),
        movement.getQuantity(),
        movement.getCreatedAt(),
        movement.getMovementType(),
        movement.getUser().getFirstname() + " " + movement.getUser().getLastname(),
        movement.getModel().getId(),
        movement.getModel().getName(),
        movement.getModel().getProduct().getId(),
        movement.getModel().getProduct().getName());
  }

  public MovementDetailsResponse buildMovementDetailsResponse() {
    if (movement == null) {
      throw new RuntimeException("Debe pasar la entidad movement");
    }

    // Verificar que los ids no sean nulos
    Long stockLotReceiverId = movement.getStockLotReceiver() != null
        ? movement.getStockLotReceiver().getId()
        : null;

    String stockLotReceiverBatch = movement.getStockLotReceiver() != null ? movement.getStockLotReceiver().getBatch()
        : null;

    Long stockLotEmitterId = movement.getStockLotEmitter() != null
        ? movement.getStockLotEmitter().getId()
        : null;

    String stockLotEmitterBatch = movement.getStockLotEmitter() != null ? movement.getStockLotEmitter().getBatch()
        : null;

    Long deliveryLineId = movement.getDeliveryLine() != null
        ? movement.getDeliveryLine().getId()
        : null;

    List<Long> movement_StockLots = movement.getMovement_StockLots() != null
        ? movement.getMovement_StockLots()
            .stream()
            .map(stockLot -> stockLot.getId())
            .toList()
        : List.of();

    Long deliveryOrderId = movement.getDeliveryLine() != null &&
            movement.getDeliveryLine().getDeliveryOrder() != null
                    ? movement.getDeliveryLine().getDeliveryOrder().getId()
                    : null;

    String deliveryOrderBatch = movement.getDeliveryLine() != null &&
            movement.getDeliveryLine().getDeliveryOrder() != null
                    ? movement.getDeliveryLine().getDeliveryOrder().getBatch()
                    : null;
                    
    return new MovementDetailsResponse(
        movement.getId(),
        movement.getQuantity(),
        movement.getComment(),
        movement.getCreatedAt(),
        movement.getMovementType(),
        movement.getUser().getFirstname() + " " + movement.getUser().getLastname(),
        movement.getModel().getProduct().getId(),
        movement.getModel().getProduct().getName(),
        movement.getModel().getId(),
        movement.getModel().getName(),
        movement.getModel().getImageUrl(),
        movement_StockLots,
        stockLotReceiverId,
        stockLotReceiverBatch,
        stockLotEmitterId,
        stockLotEmitterBatch,
        deliveryLineId,
        deliveryOrderId,
        deliveryOrderBatch);
  }
}
