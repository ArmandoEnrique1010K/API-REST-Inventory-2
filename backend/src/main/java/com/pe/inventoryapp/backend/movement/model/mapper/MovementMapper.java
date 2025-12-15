package com.pe.inventoryapp.backend.movement.model.mapper;

import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.response.MovementResponse;

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

  public MovementResponse buildMovementResponse() {
    if (movement == null) {
      throw new RuntimeException("Debe pasar la entidad movement");
    }
    return new MovementResponse(
        movement.getId(),
        // movement.getQuantity(),
        movement.getCreatedAt(),
        movement.getUsername_snapshot() // ,
    // movement.getComment(),
    // movement.getUser().getId(),
    // movement.getStockLot().getId(),
    // movement.getDeliveryLine().getId(),
    // movement.getReason(),
    // movement.getMovementType()
    );
  }
}
