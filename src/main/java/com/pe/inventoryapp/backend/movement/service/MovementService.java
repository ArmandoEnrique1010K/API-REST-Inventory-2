package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.response.MovementDetailsResponse;
import com.pe.inventoryapp.backend.movement.model.response.MovementListResponse;

public interface MovementService {
  PageResponse<MovementListResponse> findAllMovements(
    Integer minQuantity,
    Integer maxQuantity,
    LocalDateTime minCreatedAt,
    LocalDateTime maxCreatedAt,
    MovementType movementType,
    String username,
    String productName,
    Pageable pageable
  );

  PageResponse<MovementListResponse> findAllMovementsByDeliveryLine(
    Long deliveryLineId,
    Integer minQuantity,
    Integer maxQuantity,
    LocalDateTime minCreatedAt, 
    LocalDateTime maxCreatedAt,
    MovementType movementType,
    String username,
    String productName,
    Pageable pageable);

  PageResponse<MovementListResponse> findAllMovementsByStockLot(
      Long stockLotId,
      Integer minQuantity,
      Integer maxQuantity,
      LocalDateTime minCreatedAt, 
      LocalDateTime maxCreatedAt,
      MovementType movementType,
      String username,
      String productName,
      Pageable pageable);

  PageResponse<MovementListResponse> findAllMovementsByProduct(
      Long productId,
      Integer minQuantity,
      Integer maxQuantity,
      LocalDateTime minCreatedAt, 
      LocalDateTime maxCreatedAt,
      MovementType movementType,
      String username,
      Pageable pageable);

  PageResponse<MovementListResponse> findAllMovementsByUser(
      Long userId,
      Integer minQuantity,
      Integer maxQuantity,
      LocalDateTime minCreatedAt,
      LocalDateTime maxCreatedAt,
      MovementType movementType,
      String productName,
      Pageable pageable);


  MovementDetailsResponse findMovementById(Long id);
}
