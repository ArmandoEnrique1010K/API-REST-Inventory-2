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
        movement.getProduct().getName()
    );
  }

  public MovementDetailsResponse buildMovementDetailsResponse(){
    if (movement == null){
      throw new RuntimeException("Debe pasar la entidad movement");
    }

    // Verificar que los ids no sean nulos
    Long stockLotEmitterId = movement.getStockLotEmitter() != null
        ? movement.getStockLotEmitter().getId()
        : null;
    Long stockLotReceiverId = movement.getStockLotReceiver() != null
        ? movement.getStockLotReceiver().getId()
        : null;
    Long deliveryLineId = movement.getDeliveryLine() != null
        ? movement.getDeliveryLine().getId()
        : null;
    List<Long> stockLotDetailIds = movement.getStockLotDetails() != null
        ? movement.getStockLotDetails()
            .stream()
            .map(stockLot -> stockLot.getId())
            .toList()
        : List.of();

    return new MovementDetailsResponse(
      movement.getId(),
      movement.getQuantity(),
      movement.getComment(),
      movement.getCreatedAt(),
      movement.getMovementType(),
      movement.getUser().getFirstname() + " " + movement.getUser().getLastname(),
      movement.getProduct().getName(),
        stockLotReceiverId,
        stockLotEmitterId,
        stockLotDetailIds,
        deliveryLineId
    );
  }
}
