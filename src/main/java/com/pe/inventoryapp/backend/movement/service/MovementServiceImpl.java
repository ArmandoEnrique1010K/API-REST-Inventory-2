package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import com.pe.inventoryapp.backend.movement.repository.specifications.MovementSpecifications;

@Service
public class MovementServiceImpl implements MovementService {

	private final MovementRepository movementRepository;

	public MovementServiceImpl(MovementRepository movementRepository) {
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
			String username,
			String keyword) {

		Specification<Movement> spec = (MovementSpecifications.quantityBetween(minQuantity, maxQuantity))
				.and(MovementSpecifications.createdAtBetween(minCreatedAt, maxCreatedAt))
				.and(MovementSpecifications.hasMovementType(movementType))
				.and(MovementSpecifications.usernameContains(username))
				.and(MovementSpecifications.keywordContains(keyword));

		// opcional si necesitas evitar N+1
		// .and(MovementSpecifications.fetchRelations());
		// spec = spec.and(MovementSpecifications.fetchRelations());

		// Pageable sortedPageable = PageRequest.of(
		// 		pageable.getPageNumber(),
		// 		pageable.getPageSize(),
		// 		Sort.by("createdAt").descending()
		// 	);

		Page<Movement> movements = movementRepository.findAll(
				spec,
				pageable);
		;
		// Page<Movement> movements = movementRepository.findAllByParams(pageable,
		// minQuantity, maxQuantity, minCreatedAt,
		// maxCreatedAt, movementType, username, keyword);

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
					ResponseStatus.BAD_REQUEST);
		}

		Movement movement = movementRepository.findByIdFull(id)
				.orElseThrow(() -> new BusinessException(
						ResponseStatus.NOT_FOUND,
						"El movimiento no existe"));

		return MovementMapper.builder()
				.setMovement(movement)
				.buildMovementDetailsResponse();

	}
}
