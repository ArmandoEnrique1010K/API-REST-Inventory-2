package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;
import java.util.List;

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

	private final MovementRepository movementRepository;

	public MovementServiceImpl (MovementRepository movementRepository) {
		this.movementRepository = movementRepository;
	}

	@Override
	public PageResponse<MovementListResponse> findAllMovements(
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
			Long userId) {

		Page<Movement> movements = movementRepository.findAllByParams(pageable, minQuantity, maxQuantity, minCreatedAt,
				maxCreatedAt, movementType, deliveryLineId, username, keyword, modelId, userId);

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
