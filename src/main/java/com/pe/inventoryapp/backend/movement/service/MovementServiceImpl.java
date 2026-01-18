package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.mapper.MovementMapper;
import com.pe.inventoryapp.backend.movement.model.response.MovementListResponse;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;

@Service
public class MovementServiceImpl implements MovementService {

  @Autowired
  private MovementRepository movementRepository;

  @Override
  public PageResponse<MovementListResponse> findAllMovements(Integer minQuantity, Integer maxQuantity,
      LocalDateTime minCreatedAt, LocalDateTime maxCreatedAt, MovementType movementType, String username,
      String productName, Pageable pageable) {

    Page<Movement> movements = movementRepository.findAllByParams(minQuantity, maxQuantity, minCreatedAt,
        maxCreatedAt, movementType, username, productName, pageable);

    List<MovementListResponse> result = movements.getContent().stream().map(
        movement -> MovementMapper.builder()
            .setMovement(movement).buildMovementListResponse())
        .toList();

    PageResponse<MovementListResponse> pageResponse = new PageResponse<>(
        result,
        movements.getNumber(),
        movements.getSize(),
        movements.getTotalElements(),
        movements.getTotalPages(),
        movements.isFirst(),
        movements.isLast());

    return pageResponse;
  }
}
