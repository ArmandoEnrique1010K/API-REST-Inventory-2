package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.response.MovementDetailsResponse;
import com.pe.inventoryapp.backend.movement.model.response.MovementListResponse;

public interface MovementService {
  PageResponse<MovementListResponse> findAllMovements(
    Pageable pageable,
    Integer minQuantity,
    Integer maxQuantity,
    LocalDateTime minCreatedAt,
    LocalDateTime maxCreatedAt,
    MovementType movementType,
    Long deliveryLineId,
    String username,
    String keyword,
    Long modelId,
    Long userId,
    Long stockLotReceiverId
  );

  MovementDetailsResponse findMovementById(Long id);
}
