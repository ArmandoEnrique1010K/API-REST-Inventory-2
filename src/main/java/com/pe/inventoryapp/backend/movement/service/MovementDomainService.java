package com.pe.inventoryapp.backend.movement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;

@Service
public class MovementDomainService {

  private final MovementRepository movementRepository;
  private static final int MAX_MOVEMENTS = 2000;

  public MovementDomainService(MovementRepository movementRepository) {
    this.movementRepository = movementRepository;
  }

  // Genera un comentario para la linea de entrega, si el
  // comentario es nulo o vacio, se asigna un comentario por defecto
  public String generateComment(String comment, String defaultComment) {
    if (comment == null || comment.trim().isEmpty()) {
      return defaultComment;
    }
    return comment;
  }

  public void deleteLastestMovement() {
    long total = movementRepository.countAllMovements();

    if (total >= MAX_MOVEMENTS) {
      // Obtiene el ID del movimiento más antiguo

      Optional<Movement> oldest = movementRepository.findFirstByOrderByCreatedAtAsc();

      oldest.ifPresent(m -> movementRepository.deleteById(m.getId()));
    }
  }

  public void deleteManyLatestMovements() {
    long total = movementRepository.countAllMovements();

    if (total > MAX_MOVEMENTS) {
      int toDelete = (int) (total - MAX_MOVEMENTS);

      // Obtiene los IDs de los movimientos más antiguos
      List<Long> oldestIds = movementRepository.findOldestIds(PageRequest.of(0, toDelete));

      if (!oldestIds.isEmpty()) {
        // Elimina todos los movimientos por una lista de IDs
        movementRepository.deleteAllById(oldestIds);
      }
    }
  }
}
