package com.pe.inventoryapp.backend.movement.service;


import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.request.MovementAllocateRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReturnRequest;
import com.pe.inventoryapp.backend.movement.model.response.MovementListResponse;

public interface MovementService {


  // Movimientos relacionados con una linea de entrega y lote de stock

  // Movimiento de resolucion de una linea de entrega
  void saveMovementAllocate(MovementAllocateRequest movementAllocateRequest, Long id_user);

  void saveMovementReturn(MovementReturnRequest movementReturnRequest, Long id_user);


  Page<MovementListResponse> findAllMovements(
    Integer minQuantity,
    Integer maxQuantity,
    LocalDateTime minCreatedAt,
    LocalDateTime maxCreatedAt,
    MovementType movementType,
    String username,
    String productName,
    Pageable pageable
  );
}
