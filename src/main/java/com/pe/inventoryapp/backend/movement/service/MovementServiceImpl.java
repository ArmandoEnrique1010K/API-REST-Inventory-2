package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.mapper.MovementMapper;
import com.pe.inventoryapp.backend.movement.model.response.MovementDetailsResponse;
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

    @Override
    public PageResponse<MovementListResponse> findAllMovementsByDeliveryLine(Long deliveryLineId, Integer minQuantity,
            Integer maxQuantity, LocalDateTime minCreatedAt, LocalDateTime maxCreatedAt, MovementType movementType,
            String username, String productName, Pageable pageable) {
        Page<Movement> movements = movementRepository.findAllByDeliveryLineIdAndParams(deliveryLineId, minQuantity,
                maxQuantity, minCreatedAt,
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

    @Override
    public PageResponse<MovementListResponse> findAllMovementsByStockLot(Long stockLotId, Integer minQuantity,
            Integer maxQuantity, LocalDateTime minCreatedAt, LocalDateTime maxCreatedAt, MovementType movementType,
            String username, String productName, Pageable pageable) {

        Page<Movement> movements = movementRepository.findAllByStockLotIdAndParams(
                stockLotId, minQuantity,
                maxQuantity, minCreatedAt,
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

    @Override
    public PageResponse<MovementListResponse> findAllMovementsByProduct(Long productId, Integer minQuantity,
            Integer maxQuantity, LocalDateTime minCreatedAt, LocalDateTime maxCreatedAt, MovementType movementType,
            String username, Pageable pageable) {
        Page<Movement> movements = movementRepository.findAllByProductIdAndParams(
                productId, minQuantity,
                maxQuantity, minCreatedAt,
                maxCreatedAt, movementType, username, pageable);

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

    @Override
    public PageResponse<MovementListResponse> findAllMovementsByUser(Long userId, Integer minQuantity,
            Integer maxQuantity,
            LocalDateTime minCreatedAt, LocalDateTime maxCreatedAt, MovementType movementType, String productName,
            Pageable pageable) {

        Page<Movement> movements = movementRepository.findAllByUserIdAndParams(
                userId, minQuantity,
                maxQuantity, minCreatedAt,
                maxCreatedAt, movementType, productName, pageable);

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

    @Override
    public MovementDetailsResponse findMovementById(Long id) {
      if (id == null) {
      throw new BusinessException(
          ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Movement movement = movementRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El movimiento no existe"));

    return MovementMapper.builder()
        .setMovement(movement)
        .buildMovementDetailsResponse();

    }
}
